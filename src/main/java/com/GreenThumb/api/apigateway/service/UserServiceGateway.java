package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;

import com.GreenThumb.api.user.application.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceGateway {

    private final PlantFacade plantModule;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public UserServiceGateway(PlantFacade plantModule, RedisService redisService,
                              ObjectMapper objectMapper, UserService userService) {
        this.plantModule = plantModule;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    public long countUsers() {
        return userService.countUsers();
    }

    public List<PlantDto> getAllPlantsByUsername(String username) throws JsonProcessingException {
        String key = "user:" + username + ":plants";

        return redisService.checkKey(key)
                ? getPlantsInCache(key)
                : getPlantsInDBAndSaveInCache(username, key);
    }

    private List<PlantDto> getPlantsInDBAndSaveInCache(String username, String key) throws JsonProcessingException {
        List<PlantDto> plants;
        plants = plantModule.getAllPlantsByUsername(username);

        saveInCache(key, plants);
        return plants;
    }

    private void saveInCache(String key, List<PlantDto> plants) throws JsonProcessingException {
        String plantJson = objectMapper.writeValueAsString(plants);
        redisService.saveJson(key, plantJson);
    }

    private List<PlantDto> getPlantsInCache(String key) throws JsonProcessingException {
        String plantJson = redisService.get(key);
        return objectMapper.readValue(plantJson, new TypeReference<>() {});
    }
}
