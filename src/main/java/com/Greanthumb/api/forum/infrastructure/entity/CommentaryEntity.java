package com.Greanthumb.api.forum.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_message", nullable = false, unique = true)
    private MessageEntity message;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MediaEntity> medias = new ArrayList<>();
}
