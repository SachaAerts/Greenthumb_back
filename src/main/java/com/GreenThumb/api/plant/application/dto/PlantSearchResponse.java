package com.GreenThumb.api.plant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantSearchResponse {
    private List<PlantApiDto> plants;
    private PaginationInfo pagination;
}