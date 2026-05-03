package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO de réponse pour le tableau de bord vendeur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDashboardResponse {

    // ── KPIs vendeur ──────────────────────────────────────────────
    private long totalProduits;
    private long produitsActifs;
    private long totalCommandes;
    private long commandesEnAttente;
    private long commandesExpediees;
    private long commandesLivrees;
    private BigDecimal chiffreAffaires;
    private long alertesStockFaible; // produits avec stock < 5

    // ── Graphiques ────────────────────────────────────────────────
    /** Commandes par statut : {statut -> nombre} */
    private Map<String, Long> commandesParStatut;

    /** Revenus par mois (6 derniers mois) : {mois -> montant} */
    private Map<String, BigDecimal> revenusParMois;

    /** Top 5 produits par nombre de commandes : {nomProduit -> nombre} */
    private Map<String, Long> topProduits;
}
