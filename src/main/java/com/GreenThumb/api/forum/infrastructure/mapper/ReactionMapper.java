package com.GreenThumb.api.forum.infrastructure.mapper;

import com.GreenThumb.api.forum.domain.entity.Reaction;
import com.GreenThumb.api.forum.infrastructure.entity.ReactionEntity;

public class ReactionMapper {

    public static Reaction toDomain(ReactionEntity reaction) {
        return new Reaction(
                reaction.getIdReaction(),
                reaction.getEmoji(),
                reaction.getUser().getUsername(),
                reaction.getCreatedAt()
        );
    }
}
