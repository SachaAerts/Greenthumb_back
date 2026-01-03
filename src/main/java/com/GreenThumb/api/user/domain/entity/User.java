package com.GreenThumb.api.user.domain.entity;

import com.GreenThumb.api.user.domain.objectValue.*;

public record User(
        Username username,
        FullName fullName,
        Email email,
        PhoneNumber phoneNumber,
        String biography,
        boolean isPrivate,
        int messageCount,
        Tier tier,
        Role role,
        Avatar avatar
) {
}
