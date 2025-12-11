package com.GreenThumb.api.resources.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LikedId implements Serializable {

    @Column(name = "id_user")
    private Long userId;

    @Column(name = "id_resource")
    private Long resourceId;
}
