package com.GreenThumb.api.apigateway.dto;

import com.GreenThumb.api.apigateway.dto.user.UserResponse;

public record Session(
        UserResponse user,
        String accessToken,
        String refreshToken
) {

}
