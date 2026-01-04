package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.plant.application.dto.PageResponse;
import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;

import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.exception.EmailAlreadyUsedException;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceGateway {

    private final PlantFacade plantModule;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final TokenService tokenService;

    public UserServiceGateway(PlantFacade plantModule, RedisService redisService,
                              ObjectMapper objectMapper, UserService userService, TokenService tokenService) {
        this.plantModule = plantModule;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    public long countUsers() {
        return userService.countUsers();
    }

    public PageResponse<PlantDto> getAllPlantsByUsername(String username, Pageable pageable) {
        return plantModule.getAllPlantsByUsername(username, pageable);
    }

    public PageResponse<PlantDto> getAllPlantsByUsernameAndSearch(String username, String search, Pageable pageable) {
        return plantModule.getAllPlantsByUsernameAndSearch(username, search, pageable);
    }

    public UserDto getMe(String token) throws JsonProcessingException {
        String username = tokenService.extractUsername(token);

        return redisService.checkKey(username)
                ? getUserInCache(username)
                : getUserInBdAndSaveInCache(username);
    }

    public long getIdByUsername(String username) {
        return userService.getIdByUsername(username);
    }

    public void resetCode(String email){
        userService.sendEmailResetCode(email);
    }

    public boolean checkResetCode(String code, String email) {
        String codeRedis = redisService.get(email.toLowerCase());

        return code.equals(codeRedis);
    }

    public void editUser(UserEdit user, String oldUsername) throws JsonProcessingException {
        userService.editUser(user, oldUsername);
        redisService.delete(oldUsername);
    }

    public void editPassword(Passwords passwords, String oldUsername) throws JsonProcessingException {
        userService.editPassword(passwords, oldUsername);
        redisService.delete(oldUsername);
    }

    public void resetPassword(Passwords passwords, String email) {
        userService.resetPassword(passwords, email);
    }

    public void deactivateUser(String username) {
        userService.deactivateUserByUsername(username);
        redisService.delete(username);
    }

    public boolean isAdmin(String username) {
        return userService.isAdmin(username);
    }

    private String buildAvatarUrl(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return "/uploads/users/default.png";
        }
        String lower = storedPath.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://") || storedPath.startsWith("/")) {
            return storedPath;
        }
        return storedPath; // storedPath attendu comme "users/uuid.png"
    }

    private UserDto normalizeAvatar(UserDto user) {
        if (user == null) return null;
        String avatarUrl = buildAvatarUrl(user.avatar());
        return new UserDto(
                user.username(),
                user.firstname(),
                user.lastname(),
                user.email(),
                user.phoneNumber(),
                user.biography(),
                user.isPrivate(),
                user.role(),
                avatarUrl
        );
    }

    private UserDto getUserInBdAndSaveInCache(String username) throws JsonProcessingException {
        UserDto user = userService.getUserByUsername(username);
        user = normalizeAvatar(user);
        String userJson = objectMapper.writeValueAsString(user);
        redisService.saveJson(username, userJson);
        redisService.expiry(username, 5, TimeUnit.MINUTES);

        return user;
    }

    private UserDto getUserInCache(String username) throws JsonProcessingException {
        String userJson = redisService.get(username);

        return objectMapper.readValue(userJson, UserDto.class);
    }
}
