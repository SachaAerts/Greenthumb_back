package com.GreenThumb.api.plant.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plant")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "scientific_name", nullable = false, length = 255)
    private String scientificName;

    @Column(name = "common_name", length = 255)
    private String commonName;

    @Column(name = "life_cycle", length = 100)
    private String duration;

    @Column(name = "water_need", length = 100)
    private String waterNeed;

    @Column(name = "light")
    private int lightLevel;

    @Column(name = "soil_ph_min", precision = 3, scale = 1)
    private Double soilPhMin;

    @Column(name = "soil_ph_max", precision = 3, scale = 1)
    private Double soilPhMax;

    @Column(name = "temperature_min")
    private Integer temperatureMin;

    @Column(name = "temperature_max")
    private Integer temperatureMax;

    @Column(name = "humidity_need", length = 100)
    private Integer humidityNeed;

    @Column(name = "bloom_months", length = 100)
    private String bloomMonths;

    @Column(name = "pet_toxic")
    private Boolean petToxic = false;

    @Column(name = "human_toxic")
    private Boolean humanToxic = false;

    @Column(name = "indoor_friendly")
    private Boolean indoorFriendly = true;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;
}
