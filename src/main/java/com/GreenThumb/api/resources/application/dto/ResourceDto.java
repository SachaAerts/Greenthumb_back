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
        return new ResourceDto(
                resource.title(),
                resource.like(),
                resource.urlPicture(),
                resource.text(),
                resource.creationDate(),
                resource.username()
        );
    }
}
