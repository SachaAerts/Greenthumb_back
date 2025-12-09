package com.GreenThumb.api.user.application.dto;

import com.GreenThumb.api.user.application.service.MaxWords;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserEdit(
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

        @NotBlank(message = "Le mail est requis")
        @Pattern(regexp="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message="Le format de l'adresse mail n'est pas valide")
        String email,

        @NotBlank(message = "Le numéro de téléphone est requis")
        @Pattern(regexp="^\\d{10,}$",
                message="Le format du numéro de téléphone n'est pas valide")
        String phoneNumber,

        @MaxWords(value = 45, message = "La taille limite de la biography est de {value} mots")
        String biography,

        boolean is_private,

        String avatar
){}
