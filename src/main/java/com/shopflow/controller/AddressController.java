package com.shopflow.controller;

import com.shopflow.dto.request.AddressCreateRequest;
import com.shopflow.dto.response.AddressResponse;
import com.shopflow.security.AuthenticatedUserPrincipal;
import com.shopflow.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Tag(name = "Adresses", description = "Endpoints pour la gestion des adresses")
public class AddressController {

    private final AddressService addressService;

    /**
     * Créer une adresse
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle adresse")
    public ResponseEntity<AddressResponse> createAddress(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody AddressCreateRequest request) {
        Long userId = currentUserId(principal);
        AddressResponse response = addressService.createAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtenir une adresse par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une adresse par ID")
    public ResponseEntity<AddressResponse> getAddressById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        AddressResponse response = addressService.getAddressById(id, principal.userId(), 
                com.shopflow.entity.UserRole.valueOf(principal.role()));
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir toutes les adresses de l'utilisateur
     */
    @GetMapping
    @Operation(summary = "Obtenir toutes mes adresses")
    public ResponseEntity<List<AddressResponse>> getMyAddresses(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        Long userId = currentUserId(principal);
        List<AddressResponse> response = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les adresses d'un utilisateur (Admin uniquement)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les adresses d'un utilisateur")
    public ResponseEntity<List<AddressResponse>> getAddressesByUserId(
            @PathVariable Long userId) {
        List<AddressResponse> response = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mettre à jour une adresse
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une adresse")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody AddressCreateRequest request) {
        AddressResponse response = addressService.updateAddress(id, principal.userId(), 
                com.shopflow.entity.UserRole.valueOf(principal.role()), request);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer une adresse
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une adresse")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        addressService.deleteAddress(id, principal.userId(), 
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