package com.shopflow.repository;

import com.shopflow.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    Page<Review> findByProductId(Long productId, Pageable pageable);

    Page<Review> findByProductIdAndApprovedTrue(Long productId, Pageable pageable);

    Optional<Review> findByProductIdAndCustomerId(Long productId, Long customerId);

    Page<Review> findByApprovedFalse(Pageable pageable); // Pour modération
}