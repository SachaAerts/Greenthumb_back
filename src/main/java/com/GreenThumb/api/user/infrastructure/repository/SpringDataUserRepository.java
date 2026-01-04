package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.hibernate.sql.Update;
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

    @Modifying
    @Query("UPDATE UserEntity u SET u.isPrivate = :isPrivate WHERE u.username = :username")
    int updateIsPrivateByUsername(@Param("username") String username, @Param("isPrivate") boolean isPrivate);

    void deleteByUsername(String username);

    @Query("SELECT u.role.label FROM UserEntity u WHERE u.username = :username")
    String findRoleByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.enabled = true AND u.deletedAt IS NULL AND u.role.label NOT IN ('ADMIN', 'MODERATEUR')")
    List<UserEntity> findEligibleUsersForBulkEmail();

    @Query("SELECT u FROM UserEntity u WHERE u.username IN :usernames AND u.enabled = true AND u.deletedAt IS NULL")
    List<UserEntity> findByUsernamesForBulkEmail(@Param("usernames") List<String> usernames);

    @Query("SELECT u FROM UserEntity u WHERE u.enabled = true AND u.deletedAt IS NULL AND u.role.label IN ('ADMIN', 'MODERATEUR') ORDER BY u.role.label DESC, u.username ASC")
    List<UserEntity> findStaffUsersForBulkEmail();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.countCreatedThread = u.countCreatedThread + 1 WHERE u.id = :id")
    void incrementCreatedThread(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.countMessage = u.countMessage + 1 WHERE u.id = :id")
    void incrementCountMessage(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("Update UserEntity u SET u.tier.idTier = :tierId WHERE u.id = :userId")
    void updateUserTier(
            @Param("userId") Long userId,
            @Param("tierId") Long tierId
    );
}
