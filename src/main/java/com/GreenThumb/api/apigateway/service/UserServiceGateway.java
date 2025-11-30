package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;

import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<PlantDto> getAllPlantsByUsername(String username, int page, int size) throws JsonProcessingException {
        String key = "user:" + username + ":plants:page:" + page + ":size:" + size;

        return redisService.checkKey(key)
                ? getPlantsInCache(key)
                : getPlantsPageInDBAndSaveInCache(username, page, size, key);
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

    private UserDto getUserInBdAndSaveInCache(String username) throws JsonProcessingException {
        UserDto user = userService.getUserByUsername(username);
        user = normalizeAvatar(user); // transforme le chemin avant cache / retour
        String userJson = objectMapper.writeValueAsString(user);
        redisService.saveJson(username, userJson);
        redisService.expiry(username, 5, TimeUnit.MINUTES);

        return user;
    }

    private UserDto getUserInCache(String username) throws JsonProcessingException {
        String userJson = redisService.get(username);

        return objectMapper.readValue(userJson, UserDto.class);
    }

    private Page<PlantDto> getPlantsPageInDBAndSaveInCache(
            String username,
            int page,
            int size,
            String key
    ) throws JsonProcessingException {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlantDto> result = plantModule.getAllPlantsByUsername(username, pageable);

        saveInCache(key, result);
        return result;
    }

    private void saveInCache(String key, Page<PlantDto> plants) throws JsonProcessingException {
        String plantJson = objectMapper.writeValueAsString(plants);
        redisService.saveJson(key, plantJson);
        redisService.expiry(key, 5, TimeUnit.MINUTES);
    }

    private Page<PlantDto> getPlantsInCache(String key) throws JsonProcessingException {
        String plantJson = redisService.get(key);
        return objectMapper.readValue(plantJson, new TypeReference<>() {});
    }

    public void editUser(UserEdit user, String oldUsername) throws JsonProcessingException {
        userService.editUser(user, oldUsername);
        redisService.delete(oldUsername);
    }

    public void editPassword(Passwords passwords, String oldUsername) throws JsonProcessingException {
        userService.editPassword(passwords, oldUsername);
        redisService.delete(oldUsername);
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
}
