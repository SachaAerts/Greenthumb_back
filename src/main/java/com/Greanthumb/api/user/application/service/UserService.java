package com.Greanthumb.api.user.application.service;

import com.Greanthumb.api.user.domain.repository.UserRepository;
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
}
