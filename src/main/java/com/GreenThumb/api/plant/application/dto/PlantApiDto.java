package com.GreenThumb.api.plant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantApiDto {
    private Long id;
    private String commonName;
    private String scientificName;
    private String family;
    private String familyCommonName;
    private String imageUrl;
}
