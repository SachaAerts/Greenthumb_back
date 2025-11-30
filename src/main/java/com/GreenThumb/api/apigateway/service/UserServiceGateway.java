package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;

import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
}
