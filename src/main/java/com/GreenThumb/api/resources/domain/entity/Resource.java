package com.GreenThumb.api.resources.domain.entity;

import java.util.Date;

public record Resource(
        String title,
        int like,
        String urlPicture,
        String text,
        Date creationDate,
        String username
        ) {
}
