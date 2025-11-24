package com.GreenThumb.api.user.application.dto;

import com.GreenThumb.api.user.domain.entity.User;

public record UserDto(
        String username,
        String fullName,
        String email,
        String phoneNumber,
        String biography,
        boolean isPrivate,
        String role
) {
    public static UserDto of(User user) {
        return new UserDto(
                user.username().username(),
                user.fullName().getFullName(),
                user.email().getEmail(),
                user.phoneNumber().getPhoneNumber(),
                user.biography(),
                user.isPrivate(),
                user.role().label()
        );
    }
}
