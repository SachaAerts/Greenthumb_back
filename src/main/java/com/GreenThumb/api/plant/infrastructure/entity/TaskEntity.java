package com.GreenThumb.api.plant.infrastructure.entity;

import com.GreenThumb.api.plant.domain.enums.TaskStatus;
import com.GreenThumb.api.plant.domain.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 50)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "color")
    private String color;

    @Column(name = "is_recurrent", nullable = false)
    @Builder.Default
    private Boolean isRecurrent = true;

    @Column(name = "recurrence_frequency")
    private Integer recurrenceFrequency;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plant", nullable = false)
    private PlantEntity plant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_template")
    private TaskTemplateEntity template;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        return status == TaskStatus.PENDING && endDate.isBefore(LocalDate.now());
    }

    public boolean isDueToday() {
        return status == TaskStatus.PENDING && endDate.equals(LocalDate.now());
    }
}
