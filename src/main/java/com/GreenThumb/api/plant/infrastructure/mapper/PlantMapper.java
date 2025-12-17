package com.GreenThumb.api.plant.infrastructure.mapper;

import com.GreenThumb.api.plant.application.dto.PlantApiDto;
import com.GreenThumb.api.plant.application.dto.PlantRegister;
import com.GreenThumb.api.plant.domain.entity.Plant;
import com.GreenThumb.api.plant.domain.objecValue.PhStat;
import com.GreenThumb.api.plant.domain.objecValue.PlantStat;
import com.GreenThumb.api.plant.domain.objecValue.TemperatureStat;
import com.GreenThumb.api.plant.domain.objecValue.Toxic;
import com.GreenThumb.api.plant.infrastructure.entity.PlantApiEntity;
import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import com.GreenThumb.api.plant.infrastructure.entity.api.TreflePlantData;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;

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

    public static PlantApiDto mapToPlantDTO(TreflePlantData data) {
        if (data == null) {
            throw new IllegalArgumentException("TreflePlantData cannot be null");
        }

        return new PlantApiDto(
                data.getSlug(),
                data.getCommonName() != null && !data.getCommonName().isBlank()
                    ? data.getCommonName()
                    : "Unknown",
                data.getScientificName() != null && !data.getScientificName().isBlank()
                    ? data.getScientificName()
                    : "Unknown",
                data.getImageUrl() != null && !data.getImageUrl().isBlank()
                    ? data.getImageUrl()
                    : null
        );
    }

    public static PlantEntity toEntity(PlantRegister plantRegister, UserEntity user, String processedImageUrl) {
        return PlantEntity.builder()
                .slug(plantRegister.slug())
                .scientificName(plantRegister.scientificName())
                .commonName(plantRegister.commonName())
                .duration(plantRegister.lifeCycle())
                .waterNeed(plantRegister.waterNeed())
                .lightLevel(plantRegister.lightLevel())
                .soilType(plantRegister.soilType())
                .soilPhMin(plantRegister.soilPhMin())
                .soilPhMax(plantRegister.soilPhMax())
                .temperatureMin(plantRegister.temperatureMin())
                .temperatureMax(plantRegister.temperatureMax())
                .humidityNeed(plantRegister.humidityNeed())
                .bloomMonths(plantRegister.bloomMonthStart() + "-" + plantRegister.bloomMonthEnd())
                .petToxic(plantRegister.petToxic())
                .humanToxic(plantRegister.humanToxic())
                .indoorFriendly(plantRegister.indoorFriendly())
                .imageUrl(processedImageUrl)
                .description(plantRegister.description())
                .user(user)
                .build();
    }

    public static PlantApiEntity toPlantApiEntity(PlantApiDto plantApiDto) {
        return PlantApiEntity.builder()
                .slug(plantApiDto.getSlug())
                .scientificName(plantApiDto.getScientificName())
                .commonName(plantApiDto.getCommonName())
                .imageUrl(plantApiDto.getImageUrl())
                .build();
    }
}