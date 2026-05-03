package com.shopflow.service;

import com.shopflow.dto.request.CouponCreateRequest;
import com.shopflow.dto.response.CouponCheckResponse;
import com.shopflow.entity.Coupon;
import com.shopflow.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public CouponCheckResponse checkCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code).orElse(null);

        if (coupon == null) {
            return CouponCheckResponse.builder()
                    .code(code)
                    .exists(false)
                    .valid(false)
                    .status("NOT_FOUND")
                    .message("Coupon introuvable")
                    .build();
        }

        String status = "VALID";
        String message = "Coupon valide";
        boolean valid = true;

        if (!Boolean.TRUE.equals(coupon.getActive())) {
            status = "INACTIVE";
            message = "Coupon inactif";
            valid = false;
        } else if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
            status = "EXPIRED";
            message = "Coupon expire";
            valid = false;
        } else if (coupon.getMaxUsages() != null && coupon.getCurrentUsages() != null
                && coupon.getMaxUsages() > 0 && coupon.getCurrentUsages() >= coupon.getMaxUsages()) {
            status = "MAX_USAGES_REACHED";
            message = "Limite d'utilisation atteinte";
            valid = false;
        }

        return CouponCheckResponse.builder()
                .code(coupon.getCode())
                .exists(true)
                .valid(valid)
                .status(status)
                .message(message)
                .type(coupon.getType() != null ? coupon.getType().name() : null)
                .value(coupon.getValue())
                .active(coupon.getActive())
                .expirationDate(coupon.getExpirationDate())
                .maxUsages(coupon.getMaxUsages())
                .currentUsages(coupon.getCurrentUsages())
                .build();
    }

    @Transactional
    public CouponCheckResponse createCoupon(CouponCreateRequest request) {
        if (couponRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Un coupon avec ce code existe deja");
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().trim())
                .type(request.getType())
                .value(request.getValue())
                .active(request.getActive() != null ? request.getActive() : true)
                .maxUsages(request.getMaxUsages() != null ? request.getMaxUsages() : 0)
                .currentUsages(0)
                .expirationDate(request.getExpirationDate())
                .build();

        coupon = couponRepository.save(coupon);

        return CouponCheckResponse.builder()
                .code(coupon.getCode())
                .exists(true)
                .valid(true)
                .status("CREATED")
                .message("Coupon cree avec succes")
                .type(coupon.getType() != null ? coupon.getType().name() : null)
                .value(coupon.getValue())
                .active(coupon.getActive())
                .expirationDate(coupon.getExpirationDate())
                .maxUsages(coupon.getMaxUsages())
                .currentUsages(coupon.getCurrentUsages())
                .build();
    }

    @Transactional(readOnly = true)
    public java.util.List<CouponCheckResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(coupon -> CouponCheckResponse.builder()
                        .code(coupon.getCode())
                        .exists(true)
                        .valid(coupon.getActive() && (coupon.getExpirationDate() == null || coupon.getExpirationDate().isAfter(LocalDateTime.now())))
                        .status(coupon.getActive() ? "ACTIVE" : "INACTIVE")
                        .message("Coupon found")
                        .type(coupon.getType() != null ? coupon.getType().name() : null)
                        .value(coupon.getValue())
                        .active(coupon.getActive())
                        .expirationDate(coupon.getExpirationDate())
                        .maxUsages(coupon.getMaxUsages())
                        .currentUsages(coupon.getCurrentUsages())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }
}
