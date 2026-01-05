package com.GreenThumb.api.forum.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_picture")
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_message", nullable = false)
    private MessageEntity message;
}