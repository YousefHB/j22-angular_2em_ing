package com.shopflow.repository;

import com.shopflow.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // ✅ Trouver les variantes par product ID
    List<ProductVariant> findByProductId(Long productId);

    // ✅ Trouver une variante par product ID et name
    Optional<ProductVariant> findByProductIdAndName(Long productId, String name);
}