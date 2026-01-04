package com.GreenThumb.api.apigateway.dto;

public record CreateMessageRequest(
        Long threadId,
        String text
) {
}
