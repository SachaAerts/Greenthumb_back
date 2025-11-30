package com.GreenThumb.api.user.application.dto;

import com.GreenThumb.api.user.domain.service.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordMatch(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "Les mots de passe ne correspondent pas"
)
public record Passwords(
        @NotBlank(message = "Le mot de passe est requis")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).+$",
                message="Le mot de passe doit contenir au moins une majuscule, " +
                        "une minuscule, un chiffre et un caractère spécial")
        String password,

        @NotBlank(message = "La confirmation du mot de passe est requise")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).+$",
                message="Le mot de passe doit contenir au moins une majuscule, " +
                        "une minuscule, un chiffre et un caractère spécial")
        String confirmPassword
){}
