package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.repository.UserRepository;
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

    public User getUserByEmail(String email, String password) throws NoFoundException, IllegalArgumentException {
        return userRepository.getUserByEmail(email, password);
    }

    public User getUserByUsernameAndPassword(String username, String password) throws NoFoundException, IllegalArgumentException {
        return userRepository.getUserByUsernameAndPassword(username, password);
    }

    public User getUserByUsername(String username) throws NoFoundException, IllegalArgumentException {
        return userRepository.getUserByUsername(username);
    }

    public void postUserRegistration(UserRegister registerRequest) throws NoFoundException, IllegalArgumentException {
        userRepository.postUserRegistration(registerRequest);
    }
    public User findByEmail(String email) throws NoFoundException {
        return userRepository.findByEmail(email);
    }

    public void enableUser(String email) throws NoFoundException {
        userRepository.enableUser(email);
    }

    public boolean isUserEnabled(String email) throws NoFoundException {
        return userRepository.isUserEnabled(email);
    }

}
