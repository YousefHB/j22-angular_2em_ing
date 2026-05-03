package com.shopflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {

    @Email(message = "Email doit être valide")
    @NotBlank(message = "Email est requis")
    private String email;

    @NotBlank(message = "Mot de passe est requis")
    private String password;
}