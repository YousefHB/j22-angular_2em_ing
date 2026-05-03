package com.shopflow.controller;

import com.shopflow.dto.response.AdminDashboardResponse;
import com.shopflow.dto.response.SellerDashboardResponse;
import com.shopflow.security.AuthenticatedUserPrincipal;
import com.shopflow.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints du tableau de bord par rôle
 * - GET /api/dashboard/admin → ADMIN uniquement
 * - GET /api/dashboard/seller → SELLER uniquement
 */
@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Tableau de Bord", description = "Statistiques et graphiques par rôle")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Statistiques globales pour l'administrateur
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tableau de bord administrateur — KPIs globaux et graphiques")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        AdminDashboardResponse response = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(response);
    }

    /**
     * Statistiques pour le vendeur connecté
     */
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Tableau de bord vendeur — revenus, commandes, alertes stock")
    public ResponseEntity<SellerDashboardResponse> getSellerDashboard(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {

        if (principal == null || principal.userId() == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        SellerDashboardResponse response = dashboardService.getSellerDashboard(principal.userId());
        return ResponseEntity.ok(response);
    }
}
