package com.GreenThumb.api.forum.domain.entity;

import java.util.List;

public record Channel(
        String  name,
        String description,
        List<Thread> threads
) {
}
