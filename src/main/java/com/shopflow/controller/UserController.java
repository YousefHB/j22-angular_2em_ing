package com.shopflow.controller;

import com.shopflow.dto.request.UserRegisterRequest;
import com.shopflow.dto.response.UserResponse;
import com.shopflow.entity.UserRole;
import com.shopflow.security.AuthenticatedUserPrincipal;
import com.shopflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Endpoints pour la gestion des utilisateurs")
public class UserController {

    private final UserService userService;

    /**
     * Obtenir les informations de l'utilisateur connecté
     */
    @GetMapping("/me")
    @Operation(summary = "Obtenir mes informations")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        Long userId = currentUserId(principal);
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir un utilisateur par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un utilisateur par ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir un utilisateur par email
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Obtenir un utilisateur par email")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir tous les utilisateurs (Admin uniquement)
     */
    @GetMapping
    @Operation(summary = "Obtenir tous les utilisateurs (Admin uniquement)")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les utilisateurs par rôle (Admin uniquement)
     */
    @GetMapping("/role/{role}")
    @Operation(summary = "Obtenir les utilisateurs par rôle")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        List<UserResponse> response = userService.getUsersByRole(UserRole.valueOf(role));
        return ResponseEntity.ok(response);
    }

    /**
     * Mettre à jour les informations de l'utilisateur
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations de l'utilisateur")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Désactiver un utilisateur
     */
    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un utilisateur")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur (Admin uniquement)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(AuthenticatedUserPrincipal principal) {
        if (principal == null || principal.userId() == null) {
            throw new RuntimeException("Utilisateur non authentifie");
        }
        return principal.userId();
    }
}