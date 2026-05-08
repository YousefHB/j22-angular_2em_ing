package com.shopflow.repository;

import com.shopflow.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);

    @Modifying // @Modifying quand une requête change la base de donnée
    @Transactional // Une transaction garantit que l’opération est faite proprement.
    void deleteByCartId(Long cartId);
}