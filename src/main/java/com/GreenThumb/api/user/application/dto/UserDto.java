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
        String role,
        String avatar,
        Integer tasksCompleted
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
                user.role().label(),
                user.avatar().avatar(),
                user.tasksCompleted()
        );
    }
}
