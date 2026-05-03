package com.shopflow.dto.request;

import com.shopflow.entity.CouponType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class CouponCreateRequest {

    @NotBlank(message = "Le code du coupon est requis")
    private String code;

    @NotNull(message = "Le type du coupon est requis")
    private CouponType type;

    @NotNull(message = "La valeur du coupon est requise")
    @DecimalMin(value = "0.01", message = "La valeur doit etre superieure a 0")
    private BigDecimal value;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    @PositiveOrZero(message = "maxUsages doit etre positif ou zero")
    private Integer maxUsages = 0;

    @NotNull(message = "La date d'expiration est requise")
    @Future(message = "La date d'expiration doit etre dans le futur")
    private LocalDateTime expirationDate;
}
