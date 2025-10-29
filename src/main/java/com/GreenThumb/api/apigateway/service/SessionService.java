package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.dto.LoginRequest;
import com.GreenThumb.api.apigateway.utils.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SessionService {

    private final EmailValidator emailValidator;

    public SessionService(EmailValidator emailValidator) {
        this.emailValidator = emailValidator;
    }

    public void loginRequest(LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("Requête invalide");
        }
        boolean isEmail = loginRequest.isEmail();

        if (isEmail && !emailValidator.isValid(loginRequest.login())) {
            throw new IllegalArgumentException("Email invalide");
        }

        if (isEmail) {
            loginWithEmail(loginRequest);
        }
    }

    private void loginWithEmail(LoginRequest loginRequest) {

    }
}
