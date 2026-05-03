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
public class CouponCheckResponse {
    private String code;
    private Boolean exists;
    private Boolean valid;
    private String status;
    private String message;
    private String type;
    private BigDecimal value;
    private Boolean active;
    private LocalDateTime expirationDate;
    private Integer maxUsages;
    private Integer currentUsages;
}
