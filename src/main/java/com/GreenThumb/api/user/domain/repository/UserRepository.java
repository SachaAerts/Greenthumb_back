package com.GreenThumb.api.user.domain.repository;

import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import java.util.Optional;

public interface UserRepository {

    String getUsername(Long id_user);
    long count();
    User getUserByEmail(String email);
    UserEntity save(UserEntity user);
}
