package com.GreenThumb.api.apigateway.dto;

import java.util.List;

public record MessageDto(
        List<String> tags,
        String author,
        String date
) {

}
