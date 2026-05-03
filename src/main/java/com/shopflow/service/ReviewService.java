package com.shopflow.service;

import com.shopflow.dto.request.ReviewCreateRequest;
import com.shopflow.dto.response.ReviewResponse;
import com.shopflow.entity.Review;
import com.shopflow.entity.Product;
import com.shopflow.entity.User;
import com.shopflow.repository.ReviewRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(Long customerId, ReviewCreateRequest request) {
        log.info("Creating review for product: {} by customer: {}", request.getProductId(), customerId);

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Vérifier si l'utilisateur a déjà commenté
        reviewRepository.findByProductIdAndCustomerId(product.getId(), customerId)
                .ifPresent(review -> {
                    throw new IllegalArgumentException("You have already reviewed this product");
                });

        Review review = Review.builder()
                .customer(customer)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .approved(true) // Approuvé par défaut pour affichage immédiat
                .build();

        review = reviewRepository.save(review);

        // Mettre à jour la note moyenne du produit
        updateProductRating(product.getId());

        log.info("Review created successfully");

        return mapToResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getApprovedReviewsByProduct(Long productId, Pageable pageable) {
        // Pour éviter la confusion, on retourne tous les avis même non approuvés en phase de test
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public ReviewResponse approveReview(Long reviewId, Long userId, com.shopflow.entity.UserRole role) {
        log.info("Approving review: {} by user: {}", reviewId, userId);
        
        if (role != com.shopflow.entity.UserRole.ADMIN) {
            throw new IllegalArgumentException("Seul un administrateur peut approuver des avis");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        review.setApproved(true);
        review = reviewRepository.save(review);

        // Mettre à jour la note moyenne
        updateProductRating(review.getProduct().getId());

        return mapToResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId, com.shopflow.entity.UserRole role) {
        log.info("Deleting review: {} by user: {}", reviewId, userId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Vérifier la permission : Propriétaire ou Admin
        if (role != com.shopflow.entity.UserRole.ADMIN && !review.getCustomer().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous n'avez pas la permission de supprimer cet avis");
        }

        reviewRepository.delete(review);

        // Mettre à jour la note moyenne si l'avis était approuvé
        if (Boolean.TRUE.equals(review.getApproved())) {
            updateProductRating(review.getProduct().getId());
        }
    }

    private void updateProductRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        java.util.List<Review> allReviews = new java.util.ArrayList<>(product.getReviews());

        if (allReviews.isEmpty()) {
            product.setRating(BigDecimal.ZERO);
            product.setReviewCount(0);
        } else {
            Double avgRating = allReviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            product.setRating(BigDecimal.valueOf(avgRating));
            product.setReviewCount(allReviews.size());
        }

        productRepository.save(product);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .customerId(review.getCustomer().getId())
                .customerName(review.getCustomer().getFirstName() + " " + review.getCustomer().getLastName())
                .rating(review.getRating())
                .comment(review.getComment())
                .approved(review.getApproved())
                .createdAt(review.getCreatedAt())
                .build();
    }
}