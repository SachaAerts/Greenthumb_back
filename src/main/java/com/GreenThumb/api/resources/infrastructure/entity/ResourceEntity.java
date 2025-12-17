package com.GreenThumb.api.resources.infrastructure.entity;

import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ressources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resource")
    private Long id;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "title")
    private String title;

    @Column(name = "like_count")
    private int like;

    @Column(name = "summary")
    private String summary;

    @Column(name = "picture")
    private String pictureUrl;

    @Column(name = "text")
    private String description;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserEntity user;

    @ManyToMany
    @JoinTable(
            name = "categorise",
            joinColumns = @JoinColumn(name = "id_resource"),
            indexes = {
                    @Index(name = "idx_pca_project", columnList = "id_resource"),
                    @Index(name = "idx_pca_category", columnList = "id_resource_categories")
            }
    )
    @Builder.Default
    private Set<CategoryEntity> categories = new HashSet<>();
}
