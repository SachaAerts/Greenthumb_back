package com.GreenThumb.api.plant.infrastructure.entity.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TreflePlantData {
    private String slug;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("scientific_name")
    private String scientificName;

    @JsonProperty("image_url")
    private String imageUrl;
}
