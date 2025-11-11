package com.GreenThumb.api.user.domain.repository;

import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.domain.entity.User;

public interface UserRepository {
    String getUsername(Long id_user);

    User getUserByEmail(String email, String password);

    User getUserByUsernameAndPassword(String username, String password);

    User getUserByUsername(String username);

    long count();

    void postUserRegistration(UserRegister user);
}
