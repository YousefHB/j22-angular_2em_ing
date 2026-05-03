package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price; // ✅ Changé de Double à BigDecimal
    private BigDecimal promotionalPrice; // ✅ Changé de Double à BigDecimal
    private Integer discountPercentage;
    private Integer stock;
    private BigDecimal rating; // ✅ Changé de Double à BigDecimal
    private Integer reviewCount;
    private String imageUrl;
    private Boolean active;
    private Long sellerId;
    private String sellerName;
    private Set<Long> categoryIds;
    private LocalDateTime createdAt;
}