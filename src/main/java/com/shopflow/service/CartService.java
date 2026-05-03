package com.shopflow.service;

import com.shopflow.dto.request.CartAddItemRequest;
import com.shopflow.dto.response.CartItemResponse;
import com.shopflow.dto.response.CartResponse;
import com.shopflow.entity.Cart;
import com.shopflow.entity.CartItem;
import com.shopflow.entity.Coupon;
import com.shopflow.entity.CouponType;
import com.shopflow.entity.Product;
import com.shopflow.entity.User;
import com.shopflow.repository.CartItemRepository;
import com.shopflow.repository.CartRepository;
import com.shopflow.repository.CouponRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final CouponRepository couponRepository;

        @Transactional
        public CartResponse addItemToCart(Long customerId, CartAddItemRequest request) {
                log.info("Adding item to cart for customer: {}", customerId);

                User customer = userRepository.findById(customerId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                if (product.getStock() < request.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient stock available");
                }

                Cart cart = cartRepository.findByCustomerId(customerId)
                                .orElseGet(() -> cartRepository.save(Cart.builder()
                                                .customer(customer)
                                                .build()));

                CartItem existingItem = cart.getItems().stream()
                                .filter(item -> item.getProduct().getId().equals(request.getProductId())
                                                && (request.getVariantId() == null
                                                                || (item.getVariant() != null
                                                                                && item.getVariant().getId().equals(request.getVariantId()))))
                                .findFirst()
                                .orElse(null);

                if (existingItem != null) {
                        existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                        cartItemRepository.save(existingItem);
                } else {
                        CartItem newItem = CartItem.builder()
                                        .cart(cart)
                                        .product(product)
                                        .quantity(request.getQuantity())
                                        .build();

                        cartItemRepository.save(newItem);
                        cart.getItems().add(newItem);
                }

                return mapToResponse(cartRepository.findById(cart.getId()).orElseThrow());
        }

        @Transactional
        public CartResponse removeItemFromCart(Long cartItemId, Long userId) {
                log.info("Removing item {} from cart for user {}", cartItemId, userId);
                CartItem item = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

                if (!item.getCart().getCustomer().getId().equals(userId)) {
                        throw new IllegalArgumentException("Vous n'avez pas la permission de modifier ce panier");
                }

                Cart cart = item.getCart();
                cartItemRepository.delete(item);
                cartItemRepository.flush();
                return mapToResponse(cart);
        }

        @Transactional
        public CartResponse updateCartItemQuantity(Long cartItemId, Integer quantity, Long userId) {
                CartItem item = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

                if (!item.getCart().getCustomer().getId().equals(userId)) {
                        throw new IllegalArgumentException("Vous n'avez pas la permission de modifier ce panier");
                }

                Cart cart = item.getCart();
                if (quantity <= 0) {
                        cartItemRepository.delete(item);
                        cartItemRepository.flush();
                } else {
                        item.setQuantity(quantity);
                        cartItemRepository.save(item);
                }

                return mapToResponse(cart);
        }

        @Transactional
        public CartResponse applyCouponToCart(Long customerId, String couponCode) {
                Cart cart = cartRepository.findByCustomerId(customerId)
                                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

                if (couponCode != null && !couponCode.isBlank()) {
                        Coupon coupon = couponRepository.findByCodeAndActiveTrue(couponCode.trim())
                                        .orElseThrow(() -> new IllegalArgumentException("Coupon invalide ou expire"));
                        if (!isValidCoupon(coupon)) {
                                throw new IllegalArgumentException("Coupon invalide ou expire");
                        }
                        cart.setCouponCode(coupon.getCode());
                } else {
                        cart.setCouponCode(null);
                }

                return mapToResponse(cartRepository.save(cart));
        }

        @Transactional(readOnly = true)
        public CartResponse getCartByCustomerId(Long customerId) {
                Cart cart = cartRepository.findByCustomerId(customerId).orElse(null);

                if (cart == null) {
                        userRepository.findById(customerId)
                                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                        return CartResponse.builder()
                                        .customerId(customerId)
                                        .items(List.of())
                                        .subtotal(BigDecimal.ZERO)
                                        .shippingFee(BigDecimal.ZERO)
                                        .discountAmount(BigDecimal.ZERO)
                                        .totalAmount(BigDecimal.ZERO)
                                        .totalItems(0)
                                        .build();
                }

                return mapToResponse(cart);
        }

        @Transactional
        public void clearCart(Long customerId) {
                Cart cart = cartRepository.findByCustomerId(customerId)
                                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
                cart.getItems().clear();
                cart.setCouponCode(null);
                cartRepository.save(cart);
        }

        private CartResponse mapToResponse(Cart cart) {
                List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

                List<CartItemResponse> items = cartItems.stream()
                                .map(item -> {
                                        BigDecimal unitPrice = item.getProduct().getPromotionalPrice() != null
                                                        ? item.getProduct().getPromotionalPrice()
                                                        : item.getProduct().getPrice();
                                        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
                                                        .setScale(2, RoundingMode.HALF_UP);

                                        return CartItemResponse.builder()
                                                        .id(item.getId())
                                                        .productId(item.getProduct().getId())
                                                        .productName(item.getProduct().getName())
                                                        .productImage(item.getProduct().getImageUrl())
                                                        .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                                                        .quantity(item.getQuantity())
                                                        .unitPrice(unitPrice)
                                                        .totalPrice(lineTotal)
                                                        .subtotal(lineTotal)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                BigDecimal subtotal = items.stream()
                                .map(CartItemResponse::getTotalPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .setScale(2, RoundingMode.HALF_UP);

                Integer totalItems = items.stream()
                                .mapToInt(CartItemResponse::getQuantity)
                                .sum();

                BigDecimal shippingFee = items.isEmpty() ? BigDecimal.ZERO : BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);
                BigDecimal discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

                String couponCode = cart.getCouponCode();
                if (couponCode != null && !couponCode.isBlank()) {
                        Coupon coupon = couponRepository.findByCodeAndActiveTrue(couponCode).orElse(null);
                        if (coupon != null && isValidCoupon(coupon)) {
                                discount = calculateDiscount(subtotal, coupon);
                        }
                }

                BigDecimal totalAmount = subtotal.add(shippingFee).subtract(discount)
                                .max(BigDecimal.ZERO)
                                .setScale(2, RoundingMode.HALF_UP);

                return CartResponse.builder()
                                .id(cart.getId())
                                .customerId(cart.getCustomer().getId())
                                .items(items)
                                .subtotal(subtotal)
                                .shippingFee(shippingFee)
                                .discountAmount(discount)
                                .totalAmount(totalAmount)
                                .couponCode(cart.getCouponCode())
                                .totalItems(totalItems)
                                .build();
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

        private boolean isValidCoupon(Coupon coupon) {
                if (!Boolean.TRUE.equals(coupon.getActive())) {
                        return false;
                }
                if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
                        return false;
                }
                return coupon.getMaxUsages() <= 0 || coupon.getCurrentUsages() < coupon.getMaxUsages();
        }
}
