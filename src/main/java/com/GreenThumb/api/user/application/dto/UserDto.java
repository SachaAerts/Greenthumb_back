package com.GreenThumb.api.user.application.dto;

import com.GreenThumb.api.user.domain.entity.User;

public record UserDto(
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String biography,
        boolean isPrivate,
        int messageCount,
        TierDto tier,
        int countCreatedThread,
        String role,
        String avatar
) {
    public static UserDto of(User user) {
        return new UserDto(
                user.username().username(),
                user.fullName().firstName(),
                user.fullName().lastName(),
                user.email().getEmail(),
                user.phoneNumber().getPhoneNumber(),
                user.biography(),
                user.isPrivate(),
                user.messageCount(),
                user.tier() != null ? TierDto.toDto(user.tier()) : null,
                user.countCreatedThread(),
                user.role().label(),
                user.avatar().avatar()
        );
    }
}
