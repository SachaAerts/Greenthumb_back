package com.GreenThumb.api.apigateway.dto;

public record Session(
        UserResponse user,
        String accessToken,
        String refreshToken
) {

}
