package com.GreenThumb.api.user.application.service;

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

}
