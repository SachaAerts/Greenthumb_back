package com.Greanthumb.api.apigateway.dto;

import java.util.List;

public record Message(List<String> tags, String title, String author, int likeCount, String date) {}
