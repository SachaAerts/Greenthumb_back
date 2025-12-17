package com.GreenThumb.api.plant.domain.exceptions;

public class PlantNotFoundException extends RuntimeException {
    public PlantNotFoundException(Long id) {
        super("Plant with id " + id + " not found");
    }

    public PlantNotFoundException(String query) {
        super("No plants found for query: " + query);
    }
}
