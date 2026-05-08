package com.shopflow.entity;

import jakarta.persistence.*;//C’est ce qui permet à Java de créer une table dans la base de donées
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "user" })
@NoArgsConstructor // @EqualsAndHashCode.Include
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false) // Un User peut avoir plusieurs Address
    // optional false cad une adresse doit obligatoirement être liée à un
    // utilisateur.
    @JoinColumn(name = "user_id", nullable = false) // une colonne : user_id
    private User user;

    @Column(nullable = false)
    @NotBlank(message = "La rue est requise")
    private String street;// string Parce que certains codes postaux peuvent commencer par zéro 0030 et
                          // int la rendre 30

    @Column(nullable = false)
    @NotBlank(message = "La ville est requise")
    private String city;

    @Column(nullable = false)
    @NotBlank(message = "Le code postal est requis")
    private String postalCode;

    @Column(nullable = false)
    @NotBlank(message = "Le pays est requis")
    private String country;

    @Column(nullable = false)
    @Builder.Default // Grâce à @Builder.Default la valeur par défaut reste false
    private Boolean isPrimary = false;// indique l'adresse est l’adresse principale de l’utilisateu

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
        if (isPrimary == null) {
            isPrimary = false;
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