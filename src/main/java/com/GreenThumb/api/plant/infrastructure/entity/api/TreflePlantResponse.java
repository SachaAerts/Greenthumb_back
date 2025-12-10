package com.GreenThumb.api.plant.infrastructure.entity.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TreflePlantResponse {
    private List<TreflePlantData> data;
    private Links links;
    private Meta meta;
}
