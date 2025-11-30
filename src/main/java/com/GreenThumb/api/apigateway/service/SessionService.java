package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.dto.user.UserConnection;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.mapper.UserMapper;
import com.GreenThumb.api.apigateway.utils.EmailValidator;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.service.EmailVerificationService;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.InvalidTokenException;
import com.GreenThumb.api.user.domain.exception.UserAlreadyVerifiedException;
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

    private final UserService userService;
    private final TokenService tokenService;
    private final RedisService redisService;
    private final EmailVerificationService emailVerificationService;
    private final EmailValidator emailValidator;

    @org.springframework.beans.factory.annotation.Value("${greenthumb.frontend.url}")
    private String frontendUrl;

    @org.springframework.beans.factory.annotation.Value("${security.jwt.expiration.refresh}")
    private long refreshTokenExpirationLong;

    @org.springframework.beans.factory.annotation.Value("${security.jwt.expiration.refresh.short}")
    private long refreshTokenExpirationShort;

    public SessionService(EmailValidator emailValidator, UserService userService,
                          TokenService tokenService, RedisService redisService,
                          EmailVerificationService emailVerificationService) {
        this.emailValidator = emailValidator;
        this.userService = userService;
        this.tokenService = tokenService;
        this.redisService = redisService;
        this.emailVerificationService = emailVerificationService;
    }

    public Session sessionLoginRequest(UserConnection loginRequest) {
        boolean isEmail = loginRequest.isEmail();

        if (isEmail && !EmailValidator.isValid(loginRequest.login())) {
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

        UserDto user = userService.getUserByUsername(username);

        return saveAndCreateTokens(user);
    }

    private Session loginWithEmail(UserConnection loginRequest) throws IllegalArgumentException {
        UserDto user = userService.getUserByEmail(loginRequest.login(), loginRequest.password());

        Map<String, String> tokens = saveAndCreateTokens(user, loginRequest.rememberMe());

        return new Session(UserMapper.toResponse(user), tokens.get(ACCESS_TOKEN), tokens.get(REFRESH_TOKEN));
    }

    private Session loginWithUsername(UserConnection loginRequest) {
        UserDto user = userService.getUserByUsernameAndPassword(loginRequest.login(), loginRequest.password());

        Map<String, String> tokens = saveAndCreateTokens(user, loginRequest.rememberMe());

        return new Session(UserMapper.toResponse(user), tokens.get(ACCESS_TOKEN), tokens.get(REFRESH_TOKEN));
    }

    private Map<String, String> createToken(String username, String role) {
        Map<String, String> tokens = new HashMap<>();

        tokens.put(ACCESS_TOKEN, tokenService.generateAccessToken(username, Map.of("role", role)));
        tokens.put(REFRESH_TOKEN, tokenService.generateRefreshToken(username));

        return tokens;
    }

    private Map<String, String> saveAndCreateTokens(UserDto user, boolean rememberMe) {
        String username = user.username();

        Map<String, String> tokens = createToken(username, user.role());

        String key = RADIS_REFRESH_TOKEN_ID + username;
        redisService.save(key, tokens.get(REFRESH_TOKEN));

        long expirationMs = rememberMe ? refreshTokenExpirationLong : refreshTokenExpirationShort;
        long expirationSeconds = expirationMs / 1000;

        redisService.expiry(key, expirationSeconds, TimeUnit.SECONDS);

        log.info("Refresh token cr�� pour l'utilisateur {} avec dur�e de {} jours (rememberMe: {})",
                username,
                rememberMe ? 7 : 1,
                rememberMe);

        return tokens;
    }

    private Map<String, String> saveAndCreateTokens(UserDto user) {
        return saveAndCreateTokens(user, true);
    }

    public void verifyEmailWithCode(String email, String code) {
        String verifiedEmail = emailVerificationService.verifyAndConsumeCode(email, code)
                .orElseThrow(() -> new InvalidTokenException(
                        "Code de vérification invalide, expiré ou nombre maximum de tentatives dépassé"
                ));

        if (userService.isUserEnabled(verifiedEmail)) {
            throw new UserAlreadyVerifiedException(
                    "Votre compte est déjà vérifié. Vous pouvez vous connecter normalement."
            );
        }

        userService.enableUser(verifiedEmail);
        log.info("Compte vérifié avec succès pour: {}", verifiedEmail);
    }

    public void resendVerificationEmail(String email) {
        UserDto user = userService.findByEmail(email);

        if (userService.isUserEnabled(email)) {
            throw new UserAlreadyVerifiedException(
                    "Votre compte est déjà vérifié. Vous pouvez vous connecter normalement."
            );
        }
        
        emailVerificationService.sendVerificationEmail(email, frontendUrl);
        log.info("Nouvel email de vérification envoyé à {}", email);
    }

    public void invalidateRefreshToken(String refreshToken) {
        String username = tokenService.extractUsername(refreshToken);

        redisService.delete(RADIS_REFRESH_TOKEN_ID + username);
    }

}
