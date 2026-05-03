package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private String status;
    private BigDecimal subtotal; // ✅ Changé de Double à BigDecimal
    private BigDecimal shippingFee; // ✅ Changé de Double à BigDecimal
    private BigDecimal discountAmount; // ✅ Changé de Double à BigDecimal
    private BigDecimal totalAmount; // ✅ Changé de Double à BigDecimal
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private java.util.List<OrderItemResponse> items;
}
