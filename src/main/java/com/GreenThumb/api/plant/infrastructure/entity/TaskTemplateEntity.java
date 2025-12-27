package com.GreenThumb.api.plant.infrastructure.entity;

import com.GreenThumb.api.plant.application.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 50)
    private TaskType taskType;

    @Column(name = "title_template", nullable = false)
    private String titleTemplate;

    @Column(name = "description_template", columnDefinition = "TEXT")
    private String descriptionTemplate;

    @Column(name = "color", length = 7)
    private String color;

    // Conditions d'application (nullable = s'applique à tous)
    @Column(name = "water_need", length = 50)
    private String waterNeed;

    @Column(name = "life_cycle", length = 50)
    private String lifeCycle;

    @Column(name = "light_level", length = 50)
    private String lightLevel;

    @Column(name = "humidity_need")
    private Boolean humidityNeed;

    // Fréquence de base (en jours)
    @Column(name = "base_frequency", nullable = false)
    private Integer baseFrequency;

    @Column(name = "is_recurrent", nullable = false)
    @Builder.Default
    private Boolean isRecurrent = true;

    // Ajustements saisonniers (en pourcentage)
    @Column(name = "spring_adjustment")
    @Builder.Default
    private Integer springAdjustment = 0;

    @Column(name = "summer_adjustment")
    @Builder.Default
    private Integer summerAdjustment = 0;

    @Column(name = "autumn_adjustment")
    @Builder.Default
    private Integer autumnAdjustment = 0;

    @Column(name = "winter_adjustment")
    @Builder.Default
    private Integer winterAdjustment = 0;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 1;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si ce template s'applique à une plante donnée
     */
    public boolean matchesPlant(PlantEntity plant) {
        // Vérifier water_need
        if (waterNeed != null && !waterNeed.equals(plant.getWaterNeed())) {
            return false;
        }

        // Vérifier life_cycle
        if (lifeCycle != null && !lifeCycle.equals(plant.getDuration())) {
            return false;
        }

        // Vérifier light_level
        if (lightLevel != null && !lightLevel.equals(plant.getLightLevel())) {
            return false;
        }

        // Vérifier humidity_need
        return humidityNeed == null || humidityNeed.equals(plant.getHumidityNeed());
    }
}
