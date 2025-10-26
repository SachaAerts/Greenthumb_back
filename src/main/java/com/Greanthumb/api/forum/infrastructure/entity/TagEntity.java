package com.Greanthumb.api.forum.infrastructure.entity;

import com.Greanthumb.api.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagEntity {

    @EmbeddedId
    private TagID tagID;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "id_tag", nullable = false)
    private CategoryEntity tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    @JoinColumn(name = "id_message", nullable = false)
    private MessageEntity message;

}
