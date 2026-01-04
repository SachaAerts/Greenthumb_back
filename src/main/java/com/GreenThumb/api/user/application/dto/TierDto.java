package com.GreenThumb.api.user.application.dto;

import com.GreenThumb.api.user.domain.entity.Tier;

public record TierDto(
        String name,
        int messageRequired,
        int threadUnlocked
) {
    public static TierDto toDto(Tier tier) {
        return new TierDto(
                tier.name(),
                tier.messageRequired(),
                tier.threadUnlocked()
        );
    }
}
