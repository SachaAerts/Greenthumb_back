package com.GreenThumb.api.resources.domain.entity;

import java.time.LocalDate;

public record Resource(
        String slug,
        String title,
        String summary,
        int like,
        String urlPicture,
        String text,
        LocalDate creationDate,
        String username
        ) {
}
