package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.infrastructure.entity.PostEntity;
import com.GreenThumb.api.forum.infrastructure.entity.PostId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataPostRepo extends JpaRepository<PostEntity, PostId> {

    @Query("""
        SELECT p.message, p.user.id
        FROM PostEntity p
        WHERE p.isLike = true
        GROUP BY p.message, p.user.id
    """)
    List<Object[]> findTopLikedMessages(PageRequest pageRequest);

    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
