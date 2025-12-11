package com.GreenThumb.api.plant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantApiDto {
    private String slug;
    private String commonName;
    private String scientificName;
    private String imageUrl;
}
