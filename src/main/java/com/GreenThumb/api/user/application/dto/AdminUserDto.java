package com.GreenThumb.api.user.application.dto;

import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import java.time.LocalDateTime;

public record AdminUserDto(
        Long id,
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String biography,
        boolean isPrivate,
        String role,
        String avatar,
        boolean enabled,
        LocalDateTime deletedAt
) {
    public static AdminUserDto fromEntity(UserEntity entity) {
        return new AdminUserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getMail(),
                entity.getPhoneNumber(),
                entity.getBiography(),
                entity.isPrivate(),
                entity.getRole().getLabel(),
                entity.getAvatar(),
                entity.isEnabled(),
                entity.getDeletedAt()
        );
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}