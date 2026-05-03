package com.shopflow.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum CouponType {
    FIXED_AMOUNT("FIXED"), // Montant fixe
    PERCENTAGE("PERCENTAGE"); // Pourcentage

    private final String dbValue;

    CouponType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    @JsonValue
    public String toJson() {
        return name();
    }

    @JsonCreator
    public static CouponType fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "FIXED", "FIXED_AMOUNT" -> FIXED_AMOUNT;
            case "PERCENT", "PERCENTAGE" -> PERCENTAGE;
            default -> throw new IllegalArgumentException("Unsupported coupon type: " + value);
        };
    }
}