package com.GreenThumb.api.user.domain.repository;

import com.GreenThumb.api.user.domain.entity.User;

public interface UserRepository {
    String getUsername(Long id_user);

    User getUserByEmail(String email);

    long count();
}
