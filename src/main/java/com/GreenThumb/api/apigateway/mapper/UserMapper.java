package com.GreenThumb.api.apigateway.mapper;

import com.GreenThumb.api.apigateway.dto.user.UserResponse;
import com.GreenThumb.api.user.domain.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.username().username(),
                user.fullName().firstName(),
                user.fullName().lastName(),
                user.email().getEmail(),
                user.phoneNumber().getNumber(),
                user.biography(),
                user.isPrivate(),
                user.role().label()
        );
    }
}
