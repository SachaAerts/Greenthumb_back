package com.GreenThumb.api.user.domain.repository;

import com.GreenThumb.api.user.domain.entity.Tier;
import com.GreenThumb.api.user.infrastructure.entity.ThreadLimitTierEntity;

public interface ThreadLimitTierRepository {
    Tier findNextTier(String name);
}
