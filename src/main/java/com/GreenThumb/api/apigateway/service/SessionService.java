package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.dto.LoginRequest;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.mapper.UserMapper;
import com.GreenThumb.api.apigateway.utils.EmailValidator;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    private final EmailValidator emailValidator;
    private final UserService userService;
    private final TokenService tokenService;
    private final RedisService redisService;

    public SessionService(EmailValidator emailValidator, UserService userService,
                          TokenService tokenService, RedisService redisService) {
        this.emailValidator = emailValidator;
        this.userService = userService;
        this.tokenService = tokenService;
        this.redisService = redisService;
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

    private Session loginWithEmail(LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.login());
        generateTokenCookie(user);

        return new Session(UserMapper.toResponse(user));
    }

    private void generateTokenCookie(User user) {
        String username = user.username().username();
        String accessToken = tokenService.generateAccessToken(username, Map.of("role", user.role()));
        String refreshToken = tokenService.generateRefreshToken(username);

        redisService.save("refresh:" + username, refreshToken, 7, TimeUnit.DAYS);
    }
}
