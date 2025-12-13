package com.GreenThumb.api.resources.application.dto;

import com.GreenThumb.api.resources.domain.entity.Resource;

import java.time.LocalDate;
import java.util.Date;

public record ResourceDto(
        String slug,
        String title,
        String summary,
        int like,
        String pictureUrl,
        String description,
        LocalDate creationDate,
        String username
) {

    public static ResourceDto to(Resource resource) {
        String articleUrl = buildArticleUrl(resource.urlPicture());
        return new ResourceDto(
                resource.slug(),
                resource.title(),
                resource.summary(),
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
