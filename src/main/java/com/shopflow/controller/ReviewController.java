package com.shopflow.controller;

import com.shopflow.dto.request.ReviewCreateRequest;
import com.shopflow.dto.response.ReviewResponse;
import com.shopflow.security.AuthenticatedUserPrincipal;
import com.shopflow.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Avis", description = "Endpoints pour la gestion des avis clients")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Créer un avis sur un produit
     */
    @PostMapping
    @Operation(summary = "Créer un avis sur un produit")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody ReviewCreateRequest request) {
        Long customerId = currentUserId(principal);
        ReviewResponse response = reviewService.createReview(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtenir les avis approuvés d'un produit
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Obtenir les avis d'un produit")
    public ResponseEntity<Page<ReviewResponse>> getApprovedReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> response = reviewService.getApprovedReviewsByProduct(productId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Approuver un avis (Admin uniquement)
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approuver un avis (Admin uniquement)")
    public ResponseEntity<ReviewResponse> approveReview(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        ReviewResponse response = reviewService.approveReview(id, principal.userId(), 
                com.shopflow.entity.UserRole.valueOf(principal.role()));
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer un avis
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un avis")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        reviewService.deleteReview(id, principal.userId(), 
                com.shopflow.entity.UserRole.valueOf(principal.role()));
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(AuthenticatedUserPrincipal principal) {
        if (principal == null || principal.userId() == null) {
            throw new RuntimeException("Utilisateur non authentifie");
        }
        return principal.userId();
    }
}