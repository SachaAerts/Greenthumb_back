package com.Greanthumb.api.forum.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TagID {

    @Column(name = "id_tag")
    private Long tagId;

    @Column(name = "id_message")
    private Long messageId;
}
