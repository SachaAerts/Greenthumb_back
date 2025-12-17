package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.user.application.dto.PageResponse;
import com.GreenThumb.api.user.application.dto.AdminUserDto;
import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.application.dto.UserSearchFilters;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final EmailResetCodeService emailResetCodeService;

    public UserService(UserRepository userRepository,  EmailResetCodeService emailResetCodeService) {
        this.userRepository = userRepository;
        this.emailResetCodeService = emailResetCodeService;
    }

    public long countUsers() {
        return userRepository.count();
    }

    public PageResponse<AdminUserDto> searchUsers(String query, String status, Boolean enabled, String role, int page, int size) {
        int validatedPage = Math.max(0, page);
        int validatedSize = Math.min(Math.max(1, size), 100);
        UserSearchFilters filters = UserSearchFilters.of(query, status, enabled, role);
        return userRepository.searchUsers(filters, validatedPage, validatedSize);
    }

    public String getUsername(long id_user) throws NoFoundException {
        return userRepository.getUsername(id_user);
    }

    public UserDto getUserByEmail(String email, String password) throws NoFoundException, IllegalArgumentException {
        return UserDto.of(userRepository.getUserByEmail(email, password));
    }

    public UserDto getUserByUsernameAndPassword(String username, String password) throws NoFoundException, IllegalArgumentException {
        return UserDto.of(userRepository.getUserByUsernameAndPassword(username, password));
    }

    public UserDto getUserByUsername(String username) throws NoFoundException, IllegalArgumentException {
        return UserDto.of(userRepository.getUserByUsername(username));
    }

    public void postUserRegistration(UserRegister registerRequest) throws NoFoundException, IllegalArgumentException {
        userRepository.postUserRegistration(registerRequest);
    }

    public UserDto findByEmail(String email) throws NoFoundException {
        return UserDto.of(userRepository.findByEmail(email));
    }

    public void enableUser(String email) throws NoFoundException {
        userRepository.enableUser(email);
    }

    public boolean isUserEnabled(String email) throws NoFoundException {
        return userRepository.isUserEnabled(email);
    }

    public void editUser(UserEdit user,  String oldUsername) throws JsonProcessingException {
        userRepository.editUser(user, oldUsername);
    }

    public void editPassword(Passwords passwords, String oldUsername) throws JsonProcessingException {
        userRepository.editPassword(passwords, oldUsername);
    }

    public void resetPassword(Passwords passwords, String email) {
        userRepository.editPasswordByMail(passwords, email);
    }

    public long getIdByUsername(String username) throws NoFoundException {
        return  userRepository.getIdByUsername(username);
    }

    public AdminUserDto findByUsernameForAdmin(String username) throws NoFoundException {
        return userRepository.findByUsernameForAdmin(username);
    }

    public void setUserEnabled(String username, boolean enabled) throws NoFoundException {
        userRepository.setUserEnabled(username, enabled);
    }

    public void softDeleteUserByUsername(String username) throws NoFoundException {
        userRepository.softDeleteUserByUsername(username);
    }

    public void hardDeleteUserByUsername(String username) throws NoFoundException {
        userRepository.hardDeleteUserByUsername(username);
    }

    public void restoreUserByUsername(String username) throws NoFoundException {
        userRepository.restoreUserByUsername(username);
    }

    public boolean isAdmin(String username) {
        return userRepository.isAdmin(username);
    }
    public boolean existeUser(String email) throws NoFoundException {
        return userRepository.existUser(email);
    }

    public void sendEmailResetCode(String email) {
        emailResetCodeService.sendResetCodeMail(email);
    }

}
