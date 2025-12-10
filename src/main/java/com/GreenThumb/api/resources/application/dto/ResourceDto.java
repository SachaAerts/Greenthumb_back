package com.GreenThumb.api.resources.application.dto;

import com.GreenThumb.api.resources.domain.entity.Resource;

import java.util.Date;

public record ResourceDto(
        String title,
        int like,
        String pictureUrl,
        String description,
        Date creationDate,
        String username
) {

    public static ResourceDto to(Resource resource) {
        String articleUrl = buildArticleUrl(resource.urlPicture());
        return new ResourceDto(
                resource.title(),
                resource.like(),
                articleUrl,
                resource.text(),
                resource.creationDate(),
                resource.username()
        );
    }

    private static String buildArticleUrl(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return "/articles/default.png";
        }

        return storedPath.toLowerCase();
    }
}
