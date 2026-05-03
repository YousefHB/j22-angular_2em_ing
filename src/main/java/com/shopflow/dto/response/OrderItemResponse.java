package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;

    private Long productId;

    private String productName;

    private Long sellerId;

    private String sellerName;

    private Long variantId;

    private String variantInfo;

    private Integer quantity;

    private java.math.BigDecimal unitPrice;
}
