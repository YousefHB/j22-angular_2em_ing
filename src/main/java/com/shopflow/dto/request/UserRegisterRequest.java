package com.shopflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {

    @Email(message = "Email doit être valide")
    @NotBlank(message = "Email est requis")
    private String email;

    @Size(min = 8, message = "Le mot de passe doit avoir au moins 8 caractères")
    @NotBlank(message = "Mot de passe est requis")
    private String password;

    @NotBlank(message = "Prénom est requis")
    private String firstName;

    @NotBlank(message = "Nom est requis")
    private String lastName;

    private String role; // ADMIN, SELLER, CUSTOMER
}