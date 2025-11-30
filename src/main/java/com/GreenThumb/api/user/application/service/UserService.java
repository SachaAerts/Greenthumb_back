package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public long countUsers() {
        return userRepository.count();
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

    public void editPassword(Passwords passwords, String oldPassword) throws JsonProcessingException {
        userRepository.editPassword(passwords, oldPassword);
    }

    public long getIdByUsername(String username) throws NoFoundException {
        return  userRepository.getIdByUsername(username);
    }

}
