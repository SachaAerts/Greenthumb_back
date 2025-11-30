package com.GreenThumb.api.forum.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PostId implements Serializable {

    @Column(name = "id_user")
    private Long userId;

    @Column(name = "id_message")
    private Long messageId;
}
