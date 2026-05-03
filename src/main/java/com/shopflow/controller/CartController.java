package com.shopflow.controller;

import com.shopflow.dto.request.CartAddItemRequest;
import com.shopflow.dto.response.CartResponse;
import com.shopflow.security.AuthenticatedUserPrincipal;
import com.shopflow.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Panier", description = "Endpoints pour la gestion du panier")
public class CartController {

    private final CartService cartService;

    /**
     * Ajouter un article au panier
     */
    @PostMapping("/add")
    @Operation(summary = "Ajouter un article au panier")
    public ResponseEntity<CartResponse> addItemToCart(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody CartAddItemRequest request) {
        Long customerId = currentUserId(principal);
        CartResponse response = cartService.addItemToCart(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtenir le panier de l'utilisateur
     */
    @GetMapping
    @Operation(summary = "Obtenir le panier de l'utilisateur")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        Long customerId = currentUserId(principal);
        CartResponse response = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Modifier la quantité d'un article
     */
    @PutMapping("/item/{cartItemId}")
    @Operation(summary = "Modifier la quantité d'un article")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        CartResponse response = cartService.updateCartItemQuantity(cartItemId, quantity, principal.userId());
        return ResponseEntity.ok(response);
    }

    /**
     * Retirer un article du panier
     */
    @DeleteMapping("/item/{cartItemId}")
    @Operation(summary = "Retirer un article du panier")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        CartResponse response = cartService.removeItemFromCart(cartItemId, principal.userId());
        return ResponseEntity.ok(response);
    }

    /**
     * Vider le panier
     */
    @DeleteMapping
    @Operation(summary = "Vider le panier")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        Long customerId = currentUserId(principal);
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Appliquer un coupon au panier
     */
    @PostMapping("/coupon")
    @Operation(summary = "Appliquer un coupon au panier")
    public ResponseEntity<CartResponse> applyCoupon(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @RequestParam String couponCode) {
        Long customerId = currentUserId(principal);
        CartResponse response = cartService.applyCouponToCart(customerId, couponCode);
        return ResponseEntity.ok(response);
    }

    private Long currentUserId(AuthenticatedUserPrincipal principal) {
        if (principal == null || principal.userId() == null) {
            throw new RuntimeException("Utilisateur non authentifie");
        }
        return principal.userId();
    }
}
