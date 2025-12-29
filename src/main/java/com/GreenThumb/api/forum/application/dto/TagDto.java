package com.GreenThumb.api.forum.application.dto;

import com.GreenThumb.api.forum.domain.entity.Tag;

public record TagDto(String label) {

    public static TagDto to(Tag tag) {
        return new TagDto(tag.tag());
    }
}
