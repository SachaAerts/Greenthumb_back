package com.Greanthumb.api.forum.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ReplyId implements Serializable {

    @Column(name = "id_user")
    private Long userId;

    @Column(name = "id_commentary")
    private Long commentaryId;
}
