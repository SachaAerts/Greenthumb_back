package com.GreenThumb.api.apigateway.mapper;

import com.GreenThumb.api.apigateway.dto.user.UserResponse;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.domain.entity.User;

public class UserMapper {

    public static UserResponse toResponse(UserDto user) {
        return new UserResponse(
                user.username(),
                user.firstname(),
                user.lastname(),
                user.email(),
                user.phoneNumber(),
                user.biography(),
                user.isPrivate(),
                user.role()
        );
    }
}
