package com.GreenThumb.api.apigateway.dto;

import com.GreenThumb.api.apigateway.utils.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record UserConnection(
        @NotBlank(message = "le login est requis")
        String login,

        @NotBlank(message = "le mot de passe est requis")
        @ValidPassword
        String password
) {

    public boolean isEmail() {
        return login.contains("@");
    }

}
