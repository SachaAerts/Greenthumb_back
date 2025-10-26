package com.Greanthumb.api.forum.infrastructure.entity;

import com.Greanthumb.api.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {

    @EmbeddedId
    private PostId id;

    @Column(name = "is_like")
    private boolean isLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    @JoinColumn(name = "id_message", nullable = false)
    private MessageEntity message;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;
}
