package com.shopflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seller_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "shop_name", nullable = false)
    @NotBlank(message = "Le nom du magasin est requis")
    private String storeName;

    @Column(name = "store_name", nullable = false)
    private String legacyStoreName;

    @Column(columnDefinition = "TEXT")
    private String storeDescription;

    @Column(nullable = false)
    @Builder.Default
    private Boolean approved = false;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalSales = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Initialiser avant insertion en base
     */
    @PrePersist
    protected void onCreate() {
        if (legacyStoreName == null || legacyStoreName.isBlank()) {
            legacyStoreName = storeName;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (approved == null) {
            approved = false;
        }
        if (rating == null) {
            rating = BigDecimal.ZERO;
        }
        if (totalSales == null) {
            totalSales = 0;
        }
    }

    /**
     * Mettre à jour avant modification
     */
    @PreUpdate
    protected void onUpdate() {
        if (legacyStoreName == null || legacyStoreName.isBlank()) {
            legacyStoreName = storeName;
        }
        updatedAt = LocalDateTime.now();
    }
}