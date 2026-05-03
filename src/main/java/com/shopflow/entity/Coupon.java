package com.shopflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Le code du coupon est requis")
    private String code;

    @Column(nullable = false)
    @Convert(converter = CouponTypeConverter.class)
    private CouponType type; // FIXED_AMOUNT or PERCENTAGE

    @Column(nullable = false)
    @DecimalMin("0.01")
    private BigDecimal value;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal legacyDiscountAmount;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxUsages = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentUsages = 0;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime legacyExpiryDate;

    @Column(name = "usage_count", nullable = false)
    private Integer legacyUsageCount;

    @Column(name = "usage_limit", nullable = false)
    private Integer legacyUsageLimit;

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
        syncLegacyFields();

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (active == null) {
            active = true;
        }
        if (maxUsages == null) {
            maxUsages = 0;
        }
        if (currentUsages == null) {
            currentUsages = 0;
        }

        syncLegacyFields();
    }

    /**
     * Mettre à jour avant modification
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        syncLegacyFields();
    }

    private void syncLegacyFields() {
        this.legacyDiscountAmount = this.value;
        this.legacyExpiryDate = this.expirationDate;
        this.legacyUsageCount = this.currentUsages != null ? this.currentUsages : 0;
        this.legacyUsageLimit = this.maxUsages != null ? this.maxUsages : 0;
    }
}