package com.GreenThumb.api.plant.infrastructure.entity;

import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "common_name")
    private String commonName;

    @Column(name = "life_cycle", length = 100)
    private String duration;

    @Column(name = "water_need", length = 100)
    private String waterNeed;

    @Column(name = "light")
    private String lightLevel;

    @Column(name = "soil_type", length = 100)
    private String soilType;

    @Column(name = "soil_ph_min")
    private Double soilPhMin;

    @Column(name = "soil_ph_max")
    private Double soilPhMax;

    @Column(name = "temperature_min")
    private Integer temperatureMin;

    @Column(name = "temperature_max")
    private Integer temperatureMax;

    @Column(name = "humidity_need", length = 100)
    private Boolean humidityNeed;

    @Column(name = "bloom_months", length = 100)
    private String bloomMonths;

    @Column(name = "pet_toxic")
    @Builder.Default
    private Boolean petToxic = false;

    @Column(name = "human_toxic")
    @Builder.Default
    private Boolean humanToxic = false;

    @Column(name = "indoor_friendly")
    @Builder.Default
    private Boolean indoorFriendly = true;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskEntity> tasks = new ArrayList<>();
}
