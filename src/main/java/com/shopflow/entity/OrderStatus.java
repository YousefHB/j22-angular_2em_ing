package com.shopflow.entity;

public enum OrderStatus {
    PENDING, // En attente de paiement
    PAID, // Paiement effectué
    PROCESSING, // En cours de traitement
    SHIPPED, // Expédié
    DELIVERED, // Livré
    CANCELLED, // Annulé
    REFUNDED // Remboursé
}