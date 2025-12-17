package com.GreenThumb.api.plant.infrastructure.entity.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {
    private Integer total;

    public Integer getTotal() {
        return total != null ? total : 0;
    }
}
