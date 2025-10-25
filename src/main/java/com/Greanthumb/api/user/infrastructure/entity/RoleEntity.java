package com.Greanthumb.api.user.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long id;

    @Column(name = "label", nullable = false, unique = true)
    private String label;

    @OneToMany(mappedBy = "role")
    @Builder.Default
    private List<UserEntity> users = new ArrayList<>();
}
