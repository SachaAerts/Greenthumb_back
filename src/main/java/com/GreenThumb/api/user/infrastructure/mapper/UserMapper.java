package com.GreenThumb.api.user.infrastructure.mapper;

import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.FormatException;
import com.GreenThumb.api.user.domain.objectValue.*;
import com.GreenThumb.api.user.infrastructure.entity.RoleEntity;
import com.GreenThumb.api.user.infrastructure.entity.ThreadLimitTierEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity userEntity) throws FormatException {
        return new User(
                new Username(userEntity.getUsername()),
                new FullName(userEntity.getFirstname(), userEntity.getLastname()),
                new Email(userEntity.getMail()),
                new PhoneNumber(userEntity.getPhoneNumber()),
                userEntity.getBiography(),
                userEntity.isPrivate(),
                userEntity.getCountMessage(),
                userEntity.getTier() != null ? TierMapper.toDomain(userEntity.getTier()) : null,
                toDomain(userEntity.getRole()),
                new Avatar(userEntity.getAvatar())
        );
    }

    public static Role toDomain(RoleEntity roleEntity) {
        return new Role(roleEntity.getLabel());
    }

    public static UserEntity toEntityForRegistration(
            UserRegister userRegister,
            String hashedPassword,
            String avatar,
            RoleEntity role,
            ThreadLimitTierEntity tier
            ) {

        return UserEntity.builder()
                .username(userRegister.username())
                .firstname(userRegister.firstname())
                .lastname(userRegister.lastname())
                .mail(userRegister.email().toLowerCase())
                .password(hashedPassword)
                .phoneNumber(userRegister.phoneNumber())
                .biography(null)
                .isPrivate(false)
                .role(role)
                .enabled(false)
                .avatar(avatar)
                .tier(tier)
                .build();
    }
}
