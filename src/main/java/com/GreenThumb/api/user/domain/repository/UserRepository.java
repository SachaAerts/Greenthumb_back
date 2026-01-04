package com.GreenThumb.api.user.domain.repository;

import com.GreenThumb.api.user.application.dto.PageResponse;
import com.GreenThumb.api.user.application.dto.AdminUserDto;
import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.application.dto.UserSearchFilters;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;

public interface UserRepository {

    PageResponse<AdminUserDto> searchUsers(UserSearchFilters filters, int page, int size);

    String getUsername(Long id_user);

    User getUserByEmail(String email, String password);

    User getUserByUsernameAndPassword(String username, String password);

    User getUserByUsername(String username);

    UserEntity getUserEntityByName(String username);

    User findByEmail(String email);

    void enableUser(String email);

    boolean isUserEnabled(String email);

    long count();

    void postUserRegistration(UserRegister user);

    void editUser(UserEdit user,  String oldUsername);

    void editPassword(Passwords passwords, String oldUsername);

    void editPasswordByMail(Passwords passwords, String email);

    long getIdByUsername(String username);

    AdminUserDto findByUsernameForAdmin(String username);

    void setUserEnabled(String username, boolean enabled);

    void deactivateUserByUsername(String username);

    void hardDeleteUserByUsername(String username);

    boolean isAdmin(String username);

    boolean existUser(String email);

    boolean existByUsername(String username);

    UserEntity findByUsername(String username);

    void incrementCreatedThread(Long id);
}
