package com.GreenThumb.api.user.domain.repository;

import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;

public interface UserRepository {

    String getUsername(Long id_user);

    User getUserByEmail(String email, String password);

    User getUserByUsernameAndPassword(String username, String password);

    User getUserByUsername(String username);

    User findByEmail(String email);

    void enableUser(String email);

    boolean isUserEnabled(String email);

    long count();

    void postUserRegistration(UserRegister user);

    void editUser(UserEdit user);
}
