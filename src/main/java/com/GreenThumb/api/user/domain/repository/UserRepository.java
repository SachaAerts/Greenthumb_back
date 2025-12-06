package com.GreenThumb.api.user.domain.repository;

import com.GreenThumb.api.user.application.dto.AdminUserDto;
import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    void editUser(UserEdit user,  String oldUsername);

    void editPassword(Passwords passwords, String oldUsername);

    long getIdByUsername(String username);
    
    Page<AdminUserDto> findAllUsers(Pageable pageable);

    Page<AdminUserDto> findActiveUsers(Pageable pageable);

    Page<AdminUserDto> findDeletedUsers(Pageable pageable);

    AdminUserDto findByUsernameForAdmin(String username);

    void setUserEnabled(String username, boolean enabled);

    void softDeleteUserByUsername(String username);

    void hardDeleteUserByUsername(String username);

    void restoreUserByUsername(String username);

    boolean isAdmin(String username);
}
