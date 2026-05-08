package com.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carts") // le panier d’un utilisateur Il contient plusieurs produits via CartItem.
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "customer", "items" })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private User customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    // mappedby La relation est contrôlée par le champ cart qui existe dans
    // CartItem.
    // cascade all =Les opérations faites sur le panier peuvent se propager aux
    // items suprresion ou ...
    // Si un CartItem est retiré de la collection items, il sera supprimé de la
    // base.

    // Avec orphanRemoval = true, le cartItem supprimé de la liste sera aussi
    // supprimé de la table cart_items.

    // Sans ça, il pourrait rester en base même s’il n’est plus dans le panier.

    @Builder.Default
    private Set<CartItem> items = new HashSet<>();

    private String couponCode;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

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
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (active == null) {
            active = true;
        }
    }

    /**
     * Mettre à jour avant modification
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}