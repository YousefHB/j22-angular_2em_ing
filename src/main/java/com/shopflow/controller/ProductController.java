
package com.shopflow.controller;

import com.shopflow.dto.request.ProductCreateRequest;
import com.shopflow.dto.request.ProductUpdateRequest;
import com.shopflow.dto.response.ProductResponse;
import com.shopflow.security.AuthenticatedUserPrincipal;
import com.shopflow.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Produits", description = "Endpoints pour la gestion des produits")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Créer un nouveau produit")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request,
            @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUser,
            Authentication authentication) {

        log.info("Creating product for user: {}", authentication != null ? authentication.getName() : "unknown");
        Long sellerId = authenticatedUser != null ? authenticatedUser.userId() : null;

        if (sellerId == null) {
            throw new RuntimeException("Impossible de récupérer l'id utilisateur depuis le token");
        }

        ProductResponse response = productService.createProduct(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un produit par ID")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("Fetching product: {}", id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les produits actifs")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching all active products - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> response = productService.getAllActiveProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les produits (Admin)")
    public ResponseEntity<Page<ProductResponse>> getAllProductsAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> response = productService.getAllProductsAdmin(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/archived")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les produits archives (Admin)")
    public ResponseEntity<Page<ProductResponse>> getArchivedProductsAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> response = productService.getArchivedProductsAdmin(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seller/me")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Obtenir tous les produits du vendeur connecté")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (authenticatedUser == null || authenticatedUser.userId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> response = productService.getProductsBySeller(authenticatedUser.userId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Chercher des produits par nom")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Searching products with keyword: {}", keyword);
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> response = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Obtenir les produits d'une catégorie")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching products for category: {}", categoryId);
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> response = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Mettre à jour un produit")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {

        log.info("Updating product: {} by user: {}", id, principal.userId());
        ProductResponse response = productService.updateProduct(id, principal.userId(), 
                com.shopflow.entity.UserRole.valueOf(principal.role()), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desarchiver un produit")
    public ResponseEntity<ProductResponse> restoreProduct(@PathVariable Long id) {
        ProductResponse response = productService.restoreProduct(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Supprimer un produit")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {

        log.info("Deleting product: {} by user: {}", id, principal.userId());
        productService.deleteProduct(id, principal.userId(), 
                com.shopflow.entity.UserRole.valueOf(principal.role()));
        return ResponseEntity.noContent().build();
    }
}
