package com.shopflow.service;

import com.shopflow.dto.request.OrderCreateRequest;
import com.shopflow.dto.response.OrderItemResponse;
import com.shopflow.dto.response.OrderResponse;
import com.shopflow.entity.*;
import com.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public OrderResponse createOrder(Long customerId, OrderCreateRequest request) {
        log.info("Creating order for customer: {}", customerId);

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found or empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Address deliveryAddress = addressRepository.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Delivery address not found"));

        // Créer la commande
        Order order = Order.builder()
                .customer(customer)
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING)
                .deliveryAddress(deliveryAddress)
                .shippingFee(BigDecimal.valueOf(5.0)) // ✅ Frais de port en BigDecimal
                .build();

        // Calculer le sous-total et créer les order items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            BigDecimal effectivePrice = cartItem.getProduct().getPromotionalPrice() != null ? 
                                       cartItem.getProduct().getPromotionalPrice() : 
                                       cartItem.getProduct().getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .variant(cartItem.getVariant())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(effectivePrice)
                    .build();

            order.getItems().add(orderItem);
            subtotal = subtotal.add(effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            // Décrémenter le stock
            cartItem.getProduct().setStock(cartItem.getProduct().getStock() - cartItem.getQuantity());
        }

        order.setSubtotal(subtotal);

        // Appliquer le coupon persiste sur le panier, avec la requete en priorite si fournie.
        BigDecimal discount = BigDecimal.ZERO;
        String couponCode = request.getCouponCode() != null && !request.getCouponCode().isBlank()
                ? request.getCouponCode().trim()
                : cart.getCouponCode();

        if (couponCode != null && !couponCode.isBlank()) {
            Coupon coupon = couponRepository.findByCodeAndActiveTrue(couponCode)
                    .orElse(null);

            if (coupon != null && isValidCoupon(coupon)) {
                discount = calculateDiscount(subtotal, coupon);
                order.setCoupon(coupon);
                coupon.setCurrentUsages(coupon.getCurrentUsages() + 1);
                couponRepository.save(coupon);
            }
        }

        order.setDiscountAmount(discount);
        // ✅ Calculer le total correct
        BigDecimal totalAmount = subtotal
                .add(order.getShippingFee())
                .subtract(discount)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);

        // Vider le panier
        cart.getItems().clear();
        cart.setCouponCode(null);
        cartRepository.save(cart);

        log.info("Order created successfully: {}", order.getOrderNumber());

        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, Long userId, com.shopflow.entity.UserRole role) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Vérifier la permission
        if (role == com.shopflow.entity.UserRole.CUSTOMER && !order.getCustomer().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous n'avez pas la permission de voir cette commande");
        }
        
        // Pour un SELLER, on pourrait vérifier s'il a des produits dans cette commande
        if (role == com.shopflow.entity.UserRole.SELLER) {
            boolean hasProduct = order.getItems().stream()
                    .anyMatch(item -> item.getProduct().getSeller().getUser().getId().equals(userId));
            if (!hasProduct) {
                throw new IllegalArgumentException("Vous n'avez pas la permission de voir cette commande");
            }
        }

        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByCustomerId(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersBySellerId(Long sellerId, Pageable pageable) {
        return orderRepository.findBySellerUserId(sellerId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status, Long userId, com.shopflow.entity.UserRole role) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Seul ADMIN ou un SELLER concerné (ou le système) peut changer le statut
        if (role == com.shopflow.entity.UserRole.CUSTOMER) {
            throw new IllegalArgumentException("Les clients ne peuvent pas changer le statut d'une commande");
        }

        if (role == com.shopflow.entity.UserRole.SELLER) {
            boolean hasProduct = order.getItems().stream()
                    .anyMatch(item -> item.getProduct().getSeller().getUser().getId().equals(userId));
            if (!hasProduct) {
                throw new IllegalArgumentException("Vous n'avez pas la permission de modifier cette commande");
            }
        }

        order.setStatus(OrderStatus.valueOf(status));

        if (status.equals(OrderStatus.DELIVERED.toString())) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId, com.shopflow.entity.UserRole role) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Seul le client propriétaire ou un ADMIN peut annuler
        if (role != com.shopflow.entity.UserRole.ADMIN && !order.getCustomer().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous n'avez pas la permission d'annuler cette commande");
        }

        if (!order.getStatus().equals(OrderStatus.PENDING) && !order.getStatus().equals(OrderStatus.PAID)) {
            throw new IllegalArgumentException("Impossible d'annuler une commande déjà traitée ou expédiée");
        }

        // Restaurer le stock
        for (OrderItem item : order.getItems()) {
            item.getProduct().setStock(item.getProduct().getStock() + item.getQuantity());
            productRepository.save(item.getProduct());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private String generateOrderNumber() {
        long count = orderRepository.count() + 1;
        return String.format("ORD-%d-%05d", LocalDateTime.now().getYear(), count);
    }

    private boolean isValidCoupon(Coupon coupon) {
        if (!coupon.getActive())
            return false;

        if (coupon.getExpirationDate() != null &&
                coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (coupon.getMaxUsages() > 0 && coupon.getCurrentUsages() >= coupon.getMaxUsages()) {
            return false;
        }

        return true;
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, Coupon coupon) {
        BigDecimal discount;
        if (coupon.getType() == CouponType.PERCENTAGE) {
            discount = subtotal.multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getValue();
        }
        return discount.min(subtotal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .status(order.getStatus().toString())
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .deliveredAt(order.getDeliveredAt())
                .items(order.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .id(item.getId())
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .sellerId(item.getProduct().getSeller().getUser().getId())
                                .sellerName(item.getProduct().getSeller().getUser().getFirstName() + " "
                                        + item.getProduct().getSeller().getUser().getLastName())
                                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
