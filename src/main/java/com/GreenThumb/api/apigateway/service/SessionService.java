package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.dto.UserConnection;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.mapper.UserMapper;
import com.GreenThumb.api.apigateway.utils.EmailValidator;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SessionService {
    private final static String ACCESS_TOKEN = "access_token";
    private final static String REFRESH_TOKEN = "refresh_token";
    private final static String RADIS_REFRESH_TOKEN_ID = "refresh:";

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

    public Session loginRequest(UserConnection loginRequest) {
        if (loginRequest == null || loginRequest.login().isEmpty() || loginRequest.password().isEmpty()) {
            throw new IllegalArgumentException("Requête invalide");
        }

        boolean isEmail = loginRequest.isEmail();

        if (isEmail && !emailValidator.isValid(loginRequest.login())) {
            throw new IllegalArgumentException("Email invalide");
        }

        return isEmail ?
                loginWithEmail(loginRequest)
                : loginWithUsername(loginRequest);
    }

    public boolean checkRefreshToken(String refreshToken) {
        String username = tokenService.extractUsername(refreshToken);
        String token = redisService.get(RADIS_REFRESH_TOKEN_ID + username);

        return tokenService.isEquals(refreshToken, token);
    }

    public Map<String, String> refreshToken(String refreshToken) {
        String username = tokenService.extractUsername(refreshToken);

        User user = userService.getUserByUsername(username);

        return saveAndCreateTokens(user);
    }

    private Session loginWithEmail(UserConnection loginRequest) throws IllegalArgumentException {
        User user = userService.getUserByEmail(loginRequest.login(), loginRequest.password());

        Map<String, String> tokens = saveAndCreateTokens(user);

        return new Session(UserMapper.toResponse(user), tokens.get(ACCESS_TOKEN), tokens.get(REFRESH_TOKEN));
    }

    private Session loginWithUsername(UserConnection loginRequest) {
        User user = userService.getUserByUsernameAndPassword(loginRequest.login(), loginRequest.password());

        Map<String, String> tokens = saveAndCreateTokens(user);

        return new Session(UserMapper.toResponse(user), tokens.get(ACCESS_TOKEN), tokens.get(REFRESH_TOKEN));
    }

    private Map<String, String> createToken(String username, String role) {
        Map<String, String> tokens = new HashMap<>();

        tokens.put(ACCESS_TOKEN, tokenService.generateAccessToken(username, Map.of("role", role)));
        tokens.put(REFRESH_TOKEN, tokenService.generateRefreshToken(username));

        return tokens;
    }

    private Map<String, String> saveAndCreateTokens(User user) {
        String username = user.username().username();

        Map<String, String> tokens = createToken(username, user.role().label());

        redisService.save(RADIS_REFRESH_TOKEN_ID + username, tokens.get(REFRESH_TOKEN), 7, TimeUnit.DAYS);
        return tokens;
    }

}
