package com.GreenThumb.api.user.domain.entity;

public record Tier(
        String name,
        int messageRequired,
        int threadUnlocked
) {
}
