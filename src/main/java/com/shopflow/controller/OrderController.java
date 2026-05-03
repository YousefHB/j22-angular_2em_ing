package com.shopflow.controller;

import com.shopflow.dto.request.OrderCreateRequest;
import com.shopflow.dto.response.OrderResponse;
import com.shopflow.security.AuthenticatedUserPrincipal;
import com.shopflow.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "Endpoints pour la gestion des commandes")
public class OrderController {

    private final OrderService orderService;

    /**
     * Créer une commande
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle commande")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody OrderCreateRequest request) {
        Long customerId = currentUserId(principal);
        OrderResponse response = orderService.createOrder(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtenir une commande par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une commande par ID")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        OrderResponse response = orderService.getOrderById(id, principal.userId(), 
                com.shopflow.entity.UserRole.valueOf(principal.role()));
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les commandes de l'utilisateur
     */
    @GetMapping
    @Operation(summary = "Obtenir les commandes (toutes pour ADMIN, sinon utilisateur)")
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> response;
        
        if ("ADMIN".equals(principal.role())) {
            response = orderService.getAllOrders(pageable);
        } else if ("SELLER".equals(principal.role())) {
            response = orderService.getOrdersBySellerId(principal.userId(), pageable);
        } else {
            response = orderService.getOrdersByCustomerId(principal.userId(), pageable);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mettre à jour le statut d'une commande
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une commande")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        OrderResponse response = orderService.updateOrderStatus(id, status, principal.userId(), 
                com.shopflow.entity.UserRole.valueOf(principal.role()));
        return ResponseEntity.ok(response);
    }

    /**
     * Annuler une commande
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Annuler une commande")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        orderService.cancelOrder(id, principal.userId(), 
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