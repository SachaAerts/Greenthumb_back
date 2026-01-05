package com.GreenThumb.api.user.application.dto;

import com.GreenThumb.api.user.domain.entity.User;

public record UserPrivateDto(
        String username,
        boolean isPrivate,
        int messageCount,
        TierDto tier,
        int countCreatedThread,
        String role,
        String avatar,
        Integer tasksCompleted
) {

    public static UserPrivateDto of(User user) {
        return new UserPrivateDto(
                user.username().username(),
                user.isPrivate(),
                user.messageCount(),
                user.tier() != null ? TierDto.toDto(user.tier()) : null,
                user.countCreatedThread(),
                user.role().label(),
                user.avatar().avatar(),
                user.tasksCompleted()
        );
    }
}
