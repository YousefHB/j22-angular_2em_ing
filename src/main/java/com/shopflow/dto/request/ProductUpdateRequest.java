package com.shopflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin("0.01")
    private BigDecimal price; // ✅ Changé de Double à BigDecimal

    private BigDecimal promotionalPrice; // ✅ Changé de Double à BigDecimal

    private Integer discountPercentage;

    @NotNull(message = "Stock is required")
    @Min(0)
    private Integer stock;

    private String imageUrl;

    private Boolean active;

    private Set<Long> categoryIds;
}