package com.shopflow.controller;

import com.shopflow.dto.request.UserLoginRequest;
import com.shopflow.dto.request.UserRegisterRequest;
import com.shopflow.dto.response.AuthResponse;
import com.shopflow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour l'authentification")
public class AuthController {

    private final AuthService authService;

    /**
     * Enregistrer un nouvel utilisateur
     * 
     * @param request Email, Password, FirstName, LastName, Role
     * @return Token JWT et infos utilisateur
     */
    @PostMapping("/register")
    @Operation(summary = "Enregistrer un nouvel utilisateur")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Connecter un utilisateur
     * 
     * @param request Email et Password
     * @return Token JWT et infos utilisateur
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Renouveler le token d'accès
     * 
     * @param refreshToken Token de renouvellement
     * @return Nouveau token d'accès
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renouveler le token d'accès")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}