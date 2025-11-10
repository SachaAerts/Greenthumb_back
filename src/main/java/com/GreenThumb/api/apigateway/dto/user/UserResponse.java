package com.GreenThumb.api.apigateway.dto.user;

public record UserResponse(
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String biography,
        boolean isPrivate,
        String role
) {
}
