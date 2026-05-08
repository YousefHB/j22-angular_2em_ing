package com.shopflow.service;

import com.shopflow.dto.request.ProductCreateRequest;
import com.shopflow.dto.request.ProductUpdateRequest;
import com.shopflow.dto.response.ProductResponse;
import com.shopflow.entity.Category;
import com.shopflow.entity.Product;
import com.shopflow.entity.SellerProfile;
import com.shopflow.entity.User;
import com.shopflow.entity.UserRole;
import com.shopflow.repository.CategoryRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.SellerProfileRepository;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

        private final ProductRepository productRepository;
        private final SellerProfileRepository sellerProfileRepository;
        private final UserRepository userRepository;
        private final CategoryRepository categoryRepository;

        @Transactional
        public ProductResponse createProduct(Long sellerId, ProductCreateRequest request) {
                log.info("Creating new product for user: {}", sellerId);

                User user = userRepository.findById(sellerId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + sellerId));

                if (user.getRole() != UserRole.SELLER && user.getRole() != UserRole.ADMIN) {
                        throw new IllegalArgumentException("Only SELLER or ADMIN can create products");
                }

                SellerProfile sellerProfile = sellerProfileRepository.findByUserId(sellerId)
                                .orElseGet(() -> {
                                        // Auto-provision a minimal seller profile for authorized users.
                                        SellerProfile profile = SellerProfile.builder()
                                                        .user(user)
                                                        .storeName((user.getFirstName() + " " + user.getLastName())
                                                                        .trim() + " Store")
                                                        .storeDescription("Auto-generated seller profile")
                                                        .approved(user.getRole() == UserRole.ADMIN)
                                                        .build();
                                        return sellerProfileRepository.save(profile);
                                });

                Product product = Product.builder()
                                .seller(sellerProfile)
                                .name(request.getName())
                                .description(request.getDescription())
                                .price(request.getPrice())
                                .promotionalPrice(request.getPromotionalPrice())
                                .discountPercentage(calculateDiscountPercentage(request.getPrice(), request.getPromotionalPrice()))
                                .stock(request.getStock())
                                .imageUrl(request.getImageUrl())
                                .active(true)
                                .build();

                // Associer les catégories
                if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                        Set<Category> categories = request.getCategoryIds().stream()
                                        .map(id -> categoryRepository.findById(id)
                                                        .orElseThrow(() -> new IllegalArgumentException(
                                                                        "Category not found: " + id)))
                                        .collect(Collectors.toSet());
                        product.setCategories(categories);
                }

                product = productRepository.save(product);
                log.info("Product created successfully with id: {}", product.getId());

                return mapToResponse(product);
        }

        @Transactional
        public ProductResponse updateProduct(Long productId, Long userId, UserRole role, ProductUpdateRequest request) {
                log.info("Updating product: {} by user: {}", productId, userId);

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                // Vérifier la propriété (sauf si ADMIN)
                if (role != UserRole.ADMIN && !product.getSeller().getUser().getId().equals(userId)) {
                        throw new IllegalArgumentException("Vous n'avez pas la permission de modifier ce produit");
                }

                product.setName(request.getName());
                product.setDescription(request.getDescription());
                product.setPrice(request.getPrice());
                product.setPromotionalPrice(request.getPromotionalPrice());
                product.setDiscountPercentage(calculateDiscountPercentage(request.getPrice(), request.getPromotionalPrice()));
                product.setStock(request.getStock());
                product.setImageUrl(request.getImageUrl());
                if (request.getActive() != null) {
                        product.setActive(request.getActive());
                }

                // Mettre à jour les catégories
                if (request.getCategoryIds() != null) {
                        Set<Category> categories = request.getCategoryIds().stream()
                                        .map(id -> categoryRepository.findById(id)
                                                        .orElseThrow(() -> new IllegalArgumentException(
                                                                        "Category not found: " + id)))
                                        .collect(Collectors.toSet());
                        product.setCategories(categories);
                }

                product = productRepository.save(product);
                return mapToResponse(product);
        }

        @Transactional(readOnly = true)
        public ProductResponse getProductById(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                return mapToResponse(product);
        }

        @Transactional(readOnly = true)
        public Page<ProductResponse> searchProducts(String name, Pageable pageable) {
                return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable)
                                .map(this::mapToResponse);
        }

        @Transactional(readOnly = true)
        public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
                return productRepository.findByCategoryId(categoryId, pageable)
                                .map(this::mapToResponse);
        }

        @Transactional(readOnly = true)
        public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice,
                        Pageable pageable) {
                // ✅ Convertir BigDecimal en Double pour la requête
                return productRepository.findByPriceBetween(
                                minPrice.doubleValue(),
                                maxPrice.doubleValue(),
                                pageable).map(this::mapToResponse);
        }

        @Transactional(readOnly = true)
        public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
                return productRepository.findByActive(true, pageable)
                                .map(this::mapToResponse);
        }

        @Transactional(readOnly = true)
        public Page<ProductResponse> getAllProductsAdmin(Pageable pageable) {
                // Par défaut, on ne montre que les actifs même aux admins pour éviter la confusion après suppression
                return productRepository.findByActive(true, pageable)
                                .map(this::mapToResponse);
        }

        @Transactional(readOnly = true)
        public Page<ProductResponse> getArchivedProductsAdmin(Pageable pageable) {
                return productRepository.findByActive(false, pageable)
                                .map(this::mapToResponse);
        }

        @Transactional(readOnly = true)
        public Page<ProductResponse> getProductsBySeller(Long userId, Pageable pageable) {
                return productRepository.findBySeller_User_IdAndActiveTrue(userId, pageable)
                                .map(this::mapToResponse);
        }

        @Transactional
        public void deleteProduct(Long id, Long userId, UserRole role) {
                log.info("Deleting/Deactivating product: {} by user: {}", id, userId);
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                // Vérifier la propriété (sauf si ADMIN)
                if (role != UserRole.ADMIN && !product.getSeller().getUser().getId().equals(userId)) {
                        throw new IllegalArgumentException("Vous n'avez pas la permission de supprimer ce produit");
                }

                product.setActive(false);
                productRepository.save(product);
        }

        @Transactional
        public ProductResponse restoreProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                product.setActive(true);
                return mapToResponse(productRepository.save(product));
        }

        private Integer calculateDiscountPercentage(BigDecimal price, BigDecimal promoPrice) {
                if (price == null || promoPrice == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                        return 0;
                }
                BigDecimal discount = price.subtract(promoPrice);
                BigDecimal percentage = discount.multiply(new BigDecimal(100)).divide(price, 0, java.math.RoundingMode.HALF_UP);
                return percentage.intValue();
        }

        private ProductResponse mapToResponse(Product product) {
                // Calculer dynamiquement pour éviter les désynchronisations
                BigDecimal rating = BigDecimal.ZERO;
                int reviewCount = 0;

                try {
                        java.util.List<com.shopflow.entity.Review> reviewsList = (product.getReviews() != null) 
                                        ? new java.util.ArrayList<>(product.getReviews()) 
                                        : new java.util.ArrayList<>();

                        if (!reviewsList.isEmpty()) {
                                Double avg = reviewsList.stream().mapToDouble(r -> r.getRating()).average().orElse(0.0);
                                rating = BigDecimal.valueOf(avg);
                                reviewCount = reviewsList.size();
                        }
                } catch (Exception e) {
                        // Si les reviews ne sont pas chargées (lazy loading), utiliser les valeurs par défaut
                        log.debug("Reviews not loaded for product {}, using default values", product.getId());
                        rating = product.getRating() != null ? product.getRating() : BigDecimal.ZERO;
                        reviewCount = product.getReviewCount() != null ? product.getReviewCount() : 0;
                }

                Set<Long> categoryIds = new HashSet<>();
                try {
                        if (product.getCategories() != null) {
                                categoryIds = product.getCategories().stream()
                                                .map(Category::getId)
                                                .collect(Collectors.toSet());
                        }
                } catch (Exception e) {
                        // Si les catégories ne sont pas chargées (lazy loading), retourner un set vide
                        log.debug("Categories not loaded for product {}, returning empty set", product.getId());
                }

                return ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .promotionalPrice(product.getPromotionalPrice())
                                .discountPercentage(product.getDiscountPercentage())
                                .stock(product.getStock())
                                .rating(rating)
                                .reviewCount(reviewCount)
                                .imageUrl(product.getImageUrl())
                                .active(product.getActive())
                                .sellerId(product.getSeller().getUser().getId())
                                .sellerName(product.getSeller().getUser().getFirstName() + " "
                                                + product.getSeller().getUser().getLastName())
                                .categoryIds(categoryIds)
                                .createdAt(product.getCreatedAt())
                                .build();
        }
}
