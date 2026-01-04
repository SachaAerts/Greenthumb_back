package com.GreenThumb.api.user.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "thread_limit_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreadLimitTierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tier")
    private Long idTier;

    @Column(name = "messages_required", nullable = false)
    private int messageRequired;

    @Column(name = "threads_unlocked", nullable = false)
    private int threadUnlocked;

    @Column(name = "tier_name", nullable = false, unique = true)
    private String tierName;

    @OneToMany(mappedBy = "tier", fetch = FetchType.LAZY)
    private List<UserEntity> users = new ArrayList<>();
}
