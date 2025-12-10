package com.GreenThumb.api.plant.infrastructure.entity.api;

import lombok.Data;

@Data
public class Links {
    private String self;
    private String first;
    private String next;
    private String last;
    private String plant;
    private String genus;
}
