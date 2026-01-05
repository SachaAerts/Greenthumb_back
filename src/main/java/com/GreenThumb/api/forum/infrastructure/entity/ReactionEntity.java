package com.GreenThumb.api.forum.infrastructure.entity;


import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_message", "id_user", "emoji"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reaction")
    private Long idReaction;

    @Column(nullable = false, length = 10)
    private String emoji;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_message", nullable = false)
    private MessageEntity message;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
