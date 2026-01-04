package com.GreenThumb.api.user.infrastructure.mapper;

import com.GreenThumb.api.user.domain.entity.Tier;
import com.GreenThumb.api.user.infrastructure.entity.ThreadLimitTierEntity;

public class TierMapper {

    public static Tier toDomain(ThreadLimitTierEntity entity) {
        return new Tier(
                entity.getTierName(),
                entity.getMessageRequired(),
                entity.getThreadUnlocked()
        );
    }
}
