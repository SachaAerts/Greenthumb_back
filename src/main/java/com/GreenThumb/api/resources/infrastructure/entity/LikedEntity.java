package com.GreenThumb.api.resources.infrastructure.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "liked")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedEntity {

    @EmbeddedId
    private LikedId id;
}
