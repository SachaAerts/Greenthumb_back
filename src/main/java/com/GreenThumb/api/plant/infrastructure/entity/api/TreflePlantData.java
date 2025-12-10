package com.GreenThumb.api.plant.infrastructure.entity.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TreflePlantData {
    private Long id;
    private String slug;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("scientific_name")
    private String scientificName;

    private Integer year;
    private String bibliography;
    private String author;
    private String status;
    private String rank;

    @JsonProperty("family_common_name")
    private String familyCommonName;

    private String genus;

    @JsonProperty("image_url")
    private String imageUrl;

    private String synonyms;
    private String family;
    private Links links;
}
