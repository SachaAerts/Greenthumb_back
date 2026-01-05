package com.GreenThumb.api.forum.application.dto;

import com.GreenThumb.api.forum.domain.entity.Reaction;

import java.time.LocalDateTime;

public record ReactionDto(
        Long idReaction,
        String emoji,
        String username,
        LocalDateTime createAt
) {

    public static ReactionDto toDto(Reaction reaction) {
        return new ReactionDto(
                reaction.idReaction(),
                reaction.emoji(),
                reaction.username(),
                reaction.createAt()
        );
    }

    public Reaction toDomain() {
        return new Reaction(
                this.idReaction,
                this.emoji,
                this.username,
                this.createAt
        );
    }
}
