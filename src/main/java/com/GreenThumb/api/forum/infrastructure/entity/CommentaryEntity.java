package com.GreenThumb.api.forum.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Commentaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commentary")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_message", nullable = false)
    private MessageEntity message;

    @OneToMany(mappedBy = "commentary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MediaEntity> medias = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}