package com.GreenThumb.api.plant.application.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class PlantCreatedEvent extends ApplicationEvent {

    private final Long plantId;
    private final Long userId;
    private final String slug;
    private final String scientificName;
    private final String commonName;

    private final String waterNeed;
    private final String lifeCycle;
    private final String lightLevel;
    private final Boolean humidityNeed;
    private final Boolean indoorFriendly;

    private final LocalDateTime createdAt;

    public PlantCreatedEvent(
            Object source,
            Long plantId,
            Long userId,
            String slug,
            String scientificName,
            String commonName,
            String waterNeed,
            String lifeCycle,
            String lightLevel,
            Boolean humidityNeed,
            Boolean indoorFriendly
    ) {
        super(source);
        this.plantId = plantId;
        this.userId = userId;
        this.slug = slug;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.waterNeed = waterNeed;
        this.lifeCycle = lifeCycle;
        this.lightLevel = lightLevel;
        this.humidityNeed = humidityNeed;
        this.indoorFriendly = indoorFriendly;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "PlantCreatedEvent{" +
                "plantId=" + plantId +
                ", userId=" + userId +
                ", scientificName='" + scientificName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", waterNeed='" + waterNeed + '\'' +
                ", lifeCycle='" + lifeCycle + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
