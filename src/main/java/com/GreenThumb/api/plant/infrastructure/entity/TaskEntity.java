package com.GreenThumb.api.plant.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_task")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "color")
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plant", nullable = false)
    private PlantEntity plant;
}
