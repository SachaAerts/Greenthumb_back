package com.GreenThumb.api.resources.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resource_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resource_categories")
    private Long id;

    @Column(name = "label", unique = true, nullable = false, length = 100)
    private String label;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private Set<ResourceEntity> resources = new HashSet<>();
}
