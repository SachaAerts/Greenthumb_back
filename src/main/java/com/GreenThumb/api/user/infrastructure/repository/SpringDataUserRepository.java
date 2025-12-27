package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByMail(String mail);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByMail(String mail);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    @Query("SELECT u.id FROM UserEntity u WHERE u.username = :username")
    Long findIdByUsername(String username);

    @Modifying
    @Query("UPDATE UserEntity u SET u.enabled = :enabled WHERE u.username = :username")
    int updateEnabledByUsername(@Param("username") String username, @Param("enabled") boolean enabled);

    void deleteByUsername(String username);

    @Query("SELECT u.role.label FROM UserEntity u WHERE u.username = :username")
    String findRoleByUsername(@Param("username") String username);

    @Modifying
    @Query("UPDATE UserEntity u SET u.deletedAt = :deletedAt WHERE u.username = :username")
    int softDeleteByUsername(@Param("username") String username, @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying
    @Query("UPDATE UserEntity u SET u.deletedAt = null WHERE u.username = :username")
    int restoreByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.enabled = true AND u.deletedAt IS NULL AND u.role.label NOT IN ('ADMIN', 'MODERATEUR')")
    List<UserEntity> findEligibleUsersForBulkEmail();

    @Query("SELECT u FROM UserEntity u WHERE u.username IN :usernames AND u.enabled = true AND u.deletedAt IS NULL")
    List<UserEntity> findByUsernamesForBulkEmail(@Param("usernames") List<String> usernames);

    @Query("SELECT u FROM UserEntity u WHERE u.enabled = true AND u.deletedAt IS NULL AND u.role.label IN ('ADMIN', 'MODERATEUR') ORDER BY u.role.label DESC, u.username ASC")
    List<UserEntity> findStaffUsersForBulkEmail();
}
