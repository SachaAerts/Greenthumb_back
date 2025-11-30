package com.GreenThumb.api.plant.application.dto;

public record TaskDto (
        String title,
        String description,
        String endDate,
        String color
){
}
