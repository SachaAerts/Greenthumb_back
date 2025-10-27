package com.GreenThumb.api.forum.infrastructure.entity;

import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reply")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyEntity {

    @EmbeddedId
    private ReplyId replyId;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentaryId")
    @JoinColumn(name = "id_commentary", nullable = false)
    private CommentaryEntity commentary;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;
}
