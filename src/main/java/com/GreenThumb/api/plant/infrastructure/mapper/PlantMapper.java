package com.GreenThumb.api.plant.infrastructure.mapper;

import com.GreenThumb.api.plant.domain.entity.Plant;
import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.domain.objecValue.PhStat;
import com.GreenThumb.api.plant.domain.objecValue.PlantStat;
import com.GreenThumb.api.plant.domain.objecValue.TemperatureStat;
import com.GreenThumb.api.plant.domain.objecValue.Toxic;
import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;

import java.util.List;

public class PlantMapper {

    public static Plant toDomain(PlantEntity plantEntity) {
        return new Plant(
                plantEntity.getSlug(),
                plantEntity.getScientificName(),
                plantEntity.getCommonName(),
                plantEntity.getImageUrl(),
                plantEntity.getDescription(),
                statToDomain(plantEntity)
        );
    }

    private static PlantStat statToDomain(PlantEntity plantEntity) {
        return new PlantStat(
                plantEntity.getDuration(),
                plantEntity.getLightLevel(),
                new PhStat(
                        plantEntity.getSoilPhMin(),
                        plantEntity.getSoilPhMax()
                ),
                new TemperatureStat(
                        plantEntity.getTemperatureMin(),
                        plantEntity.getTemperatureMax()
                ),
                plantEntity.getHumidityNeed(),
                new Toxic(
                        plantEntity.getPetToxic(),
                        plantEntity.getHumanToxic()
                ),
                plantEntity.getIndoorFriendly()
        );
    }
}
