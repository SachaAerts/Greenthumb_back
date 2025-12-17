package com.GreenThumb.api.apigateway.dto.user;

public record CodeRequest(
        String code,
        String email
) {
}
