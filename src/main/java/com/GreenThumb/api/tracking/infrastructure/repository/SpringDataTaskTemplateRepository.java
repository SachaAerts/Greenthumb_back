package com.GreenThumb.api.tracking.infrastructure.repository;

import com.GreenThumb.api.plant.application.enums.TaskType;
import com.GreenThumb.api.plant.infrastructure.entity.TaskTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataTaskTemplateRepository extends JpaRepository<TaskTemplateEntity, Long> {
    @Query("SELECT t FROM TaskTemplateEntity t WHERE t.isActive = true  ORDER BY t.priority ASC ")
    List<TaskTemplateEntity> findAllActive();

    @Query("SELECT t FROM TaskTemplateEntity t WHERE t.taskType = :taskType AND t.isActive = true ORDER BY t.priority ASC")
    List<TaskTemplateEntity> findByTaskType(@Param("taskType") TaskType taskType);

    @Query("SELECT COUNT(t) FROM TaskTemplateEntity t WHERE t.isActive = true")
    long countActive();

    @Query("SELECT t FROM TaskTemplateEntity t WHERE t.isActive = true " +
            "AND (t.waterNeed IS NULL OR t.waterNeed = :waterNeed) " +
            "AND (t.lifeCycle IS NULL OR t.lifeCycle = :lifeCycle) " +
            "AND (t.lightLevel IS NULL OR t.lightLevel = :lightLevel) " +
            "AND (t.humidityNeed IS NULL OR t.humidityNeed = :humidityNeed) " +
            "ORDER BY t.priority ASC")
    List<TaskTemplateEntity> findApplicableTemplates(
            @Param("waterNeed") String waterNeed,
            @Param("lifeCycle") String lifeCycle,
            @Param("lightLevel") String lightLevel,
            @Param("humidityNeed") Boolean humidityNeed
    );

    @Query("SELECT t FROM TaskTemplateEntity t WHERE t.isActive = true " +
            "AND t.waterNeed = :waterNeed " +
            "AND t.lifeCycle = :lifeCycle " +
            "ORDER BY t.priority ASC")
    List<TaskTemplateEntity> findExactMatch(
            @Param("waterNeed") String waterNeed,
            @Param("lifeCycle") String lifeCycle
    );

    @Query("SELECT t FROM TaskTemplateEntity t WHERE t.isActive = true " +
            "AND t.waterNeed IS NULL " +
            "AND t.lifeCycle IS NULL " +
            "AND t.lightLevel IS NULL " +
            "AND t.humidityNeed IS NULL " +
            "ORDER BY t.priority ASC")
    List<TaskTemplateEntity> findGenericTemplates();

}
