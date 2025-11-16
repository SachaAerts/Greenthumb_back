package com.GreenThumb.api.apigateway.mapper;

import com.GreenThumb.api.apigateway.dto.user.UserResponse;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.domain.entity.User;

public class UserMapper {

    public static UserResponse toResponse(UserDto user) {
        String fullName = user.fullName();

        String[] parts = fullName.split(" ", 2);

        String firstName = parts.length > 0 ? parts[0] : "";
        String lastName = parts.length > 1 ? parts[1] : "";

        return new UserResponse(
                user.username(),
                firstName,
                lastName,
                user.email(),
                user.phoneNumber(),
                user.biography(),
                user.isPrivate(),
                user.role()
        );
    }
}
