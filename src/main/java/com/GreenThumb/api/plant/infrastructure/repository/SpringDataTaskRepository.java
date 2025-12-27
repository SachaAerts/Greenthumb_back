package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.domain.enums.TaskStatus;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SpringDataTaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("SELECT t FROM TaskEntity t WHERE t.plant.id = :plantId ORDER BY t.endDate ASC ")
    List<TaskEntity> findByPlantId(@Param("plantId") Long plantId);

    @Query("SELECT t FROM TaskEntity t JOIN FETCH t.plant p WHERE p.user.id = :userId ORDER BY t.endDate ASC ")
    List<TaskEntity> findAllByUserId(@Param("userId") Long userId);

    long countByPlantUserId(Long userId);

    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.plant.user.id = :userId AND t.status = 'PENDING'")
    long countPendingTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM TaskEntity t JOIN FETCH t.plant p WHERE t.status = :status AND t.endDate = :date")
    List<TaskEntity> findByStatusAndEndDate(
            @Param("status")TaskStatus status,
            @Param("date")LocalDate date
            );

    @Query("SELECT t FROM TaskEntity t JOIN FETCH t.plant p WHERE t.status = 'PENDING' AND t.endDate < :date")
    List<TaskEntity> findOverdueTasks(@Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE TaskEntity t SET t.status = 'OVERDUE' WHERE t.status = :currentStatus AND t.endDate < :date")
    int updateStatusToOverdue(
            @Param("currentStatus") TaskStatus currentStatus,
            @Param("date") LocalDate date
    );

    @Query("SELECT t FROM TaskEntity t WHERE t.plant.id = :plantId AND t.status = :status ORDER BY t.endDate ASC")
    List<TaskEntity> findByPlantIdAndStatus(
            @Param("plantId") Long plantId,
            @Param("status") TaskStatus status
    );

    @Query("SELECT t FROM TaskEntity t " +
            "JOIN FETCH t.plant p " +
            "WHERE t.status = 'COMPLETED' " +
            "AND t.isRecurrent = true " +
            "AND t.completedDate = :date")
    List<TaskEntity> findCompletedRecurrentTasks(@Param("date") LocalDate date);
}
