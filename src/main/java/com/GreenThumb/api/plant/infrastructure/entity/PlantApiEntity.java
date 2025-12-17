package com.GreenThumb.api.plant.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plants_api")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantApiEntity {
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

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
}
