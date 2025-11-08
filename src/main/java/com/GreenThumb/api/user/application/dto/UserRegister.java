package com.GreenThumb.api.user.application.dto;

import com.GreenThumb.api.apigateway.utils.tags.PasswordMatch;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@PasswordMatch(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "Les mots de passe ne correspondent pas"
)
public record UserRegister(
    @NotBlank(message = "le nom d'utilisateur est requis")
    @Pattern(regexp="^[a-zA-Z0-9]+$",
            message="Le nom d'utilisateur ne peut pas contenir de caractères spéciaux")
    String username,

    @NotBlank(message = "Le prénom est requis")
    @Pattern(regexp="^[a-zA-Z0-9]+$",
            message="Le prénom ne doit pas contenir de caractères spéciaux")
    String firstname,

    @NotBlank(message = "Le nom est requis")
    @Pattern(regexp="^[a-zA-Z0-9]+$",
            message="Le nom ne doit pas contenir de caractères spéciaux")
    String lastname,

    @NotBlank(message = "Le mot de passe est requis")
    @Min(8)
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).+$",
            message="Le mot de passe doit contenir au moins une majuscule, " +
                    "une minuscule, un chiffre et un caractère spécial")
    String password,

    @NotBlank(message = "La confirmation du mot de passe est requise")
    @Min(8)
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).+$",
            message="Le mot de passe doit contenir au moins une majuscule, " +
                    "une minuscule, un chiffre et un caractère spécial")
    String confirmPassword,

    @NotBlank(message = "Le mail est requis")
    @Pattern(regexp="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message="Le format de l'adresse mail n'est pas valide")
    String email,

    @NotBlank(message = "Le numéro de téléphone est requis")
    @Pattern(regexp="^\\d{10,}$",
            message="Le format du numéro de téléphone n'est pas valide")
    String phoneNumber,

    @AssertTrue(message="Les termes du contrat doivent être acceptés")
    boolean terms,

    String avatar
) {

}
