package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO de réponse pour le tableau de bord administrateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    // ── KPIs globaux ──────────────────────────────────────────────
    private long totalUsers;
    private long totalSellers;
    private long totalCustomers;
    private long totalProducts;
    private long totalProductsActive;
    private long totalProductsArchived;
    private long totalOrders;
    private long totalCategories;
    private long pendingOrders;
    private long deliveredOrders;
    private BigDecimal chiffreAffairesTotal;

    // ── Graphiques ────────────────────────────────────────────────
    /** Répartition des commandes par statut : {statut -> nombre} */
    private Map<String, Long> commandesParStatut;

    /** Top 5 vendeurs par chiffre d'affaires : {nomBoutique -> montant} */
    private Map<String, BigDecimal> topVendeurs;

    /** Top artistes par nombre d'avis sur leurs oeuvres : {nomBoutique -> nombreAvis} */
    private Map<String, Long> avisParArtiste;

    /** Repartition des utilisateurs par role : {role -> nombre} */
    private Map<String, Long> utilisateursParRole;

    /** Nouveaux utilisateurs par mois (12 derniers mois) : {mois -> nombre} */
    private Map<String, Long> nouveauxUtilisateursParMois;

    /** Répartition des produits par catégorie : {nomCategorie -> nombre} */
    private Map<String, Long> produitsParCategorie;
}
