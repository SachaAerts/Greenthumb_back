package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.infrastructure.entity.ThreadLimitTierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataThreadLimitTierRepository extends JpaRepository<ThreadLimitTierEntity, Long> {
    @Query("""
        SELECT t FROM ThreadLimitTierEntity t
        WHERE t.messageRequired <= :messageCount
        ORDER BY t.messageRequired DESC
        LIMIT 1
    """)
    Optional<ThreadLimitTierEntity> findCurrentTierByMessageCount(@Param("messageCount") int messageCount);

    @Query("""
        SELECT t FROM ThreadLimitTierEntity t
        WHERE t.messageRequired > :messageCount
        ORDER BY t.messageRequired ASC
        LIMIT 1
    """)
    Optional<ThreadLimitTierEntity> findNextTierByMessageCount(@Param("messageCount") int messageCount);

    @Query("SELECT t FROM ThreadLimitTierEntity t " +
            "WHERE t.messageRequired > (SELECT curr.messageRequired FROM ThreadLimitTierEntity curr WHERE curr.idTier = :currentTierId) " +
            "ORDER BY t.messageRequired ASC " +
            "LIMIT 1")
    Optional<ThreadLimitTierEntity> findNextTierByCurrentId(@Param("currentTierId") Long currentTierId);

    Optional<ThreadLimitTierEntity> findByTierName(String name);
}
