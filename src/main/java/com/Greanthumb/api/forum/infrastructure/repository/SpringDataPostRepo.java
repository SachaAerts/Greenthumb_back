package com.Greanthumb.api.forum.infrastructure.repository;

import com.Greanthumb.api.forum.infrastructure.entity.PostEntity;
import com.Greanthumb.api.forum.infrastructure.entity.PostId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpringDataPostRepo extends JpaRepository<PostEntity, PostId> {

    @Query("""
        SELECT p.message, p.user.id
        FROM PostEntity p
        WHERE p.isLike = true
        GROUP BY p.message, p.user.id
        ORDER BY p.message.likeCount DESC
    """)
    List<Object[]> findTopLikedMessages(PageRequest pageRequest);
}
