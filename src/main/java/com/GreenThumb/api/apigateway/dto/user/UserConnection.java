package com.GreenThumb.api.apigateway.dto.user;

import com.GreenThumb.api.apigateway.utils.tags.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record UserConnection(
        @NotBlank(message = "le login est requis")
        String login,

        @NotBlank(message = "le mot de passe est requis")
        @ValidPassword
        String password,

        Boolean rememberMe
) {

    public UserConnection {
        if (rememberMe == null) {
            rememberMe = false;
        }
    }

    public boolean isEmail() {
        return login.contains("@");
    }

}
