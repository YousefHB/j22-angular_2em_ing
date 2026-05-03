package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long userId;

    private String email;

    private String firstName;

    private String lastName;

    private String role;

    private String accessToken;

    private String refreshToken;

    private String tokenType = "Bearer";
}