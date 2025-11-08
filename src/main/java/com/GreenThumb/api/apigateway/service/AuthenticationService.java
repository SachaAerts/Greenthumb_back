package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.application.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public void registerRequest(UserRegister registerRequest) {
        userService.postUserRegistration(registerRequest);
    }
}
