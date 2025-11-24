package com.GreenThumb.api.plant.domain.entity;

import java.time.LocalDate;

public record Task(
        String title,
        String description,
        LocalDate endDate,
        String color
) {
}
