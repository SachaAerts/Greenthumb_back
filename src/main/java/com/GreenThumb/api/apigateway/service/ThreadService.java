package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.Exception.CreatedException;
import com.GreenThumb.api.apigateway.dto.ThreadRequest;
import com.GreenThumb.api.forum.application.dto.ThreadDto;
import com.GreenThumb.api.forum.application.service.ThreadModuleService;
import com.GreenThumb.api.infrastructure.service.RedisService;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.service.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class ThreadService {

    private final UserService userService;
    private final ThreadModuleService threadService;
    private final RedisService redisService;
    public ThreadService(UserService userService,
                         ThreadModuleService threadService,
                         RedisService redisService
    ) {
        this.userService = userService;
        this.threadService = threadService;
        this.redisService = redisService;
    }

    public boolean validateAddThread(ThreadRequest request) {
        String username = request.creator();

        if (!userService.existUserByUsername(username)) {
            return false;
        }

        UserDto userDto = userService.getUserByUsername(username);

        return canAddThread(userDto);
    }

    @Transactional
    public void saveThread(ThreadRequest request, String channel) throws CreatedException, JsonProcessingException {
        if (validateAddThread(request)) {
            ThreadDto thread = new ThreadDto(
                    null,
                    request.title(),
                    false,
                    false,
                    request.creator(),
                    new ArrayList<>()
            );

            threadService.saveThread(thread, channel);
            userService.incrementCreatedCount(thread.creator());

            redisService.delete(thread.creator());
        } else {
            throw new CreatedException("Vous ne pouvez pas crée de fils, car vous avez dépasser votre limite");
        }
    }

    private boolean canAddThread(UserDto user) {
        return isNotUser(user.role())
                || canCreateThread(user.countCreatedThread(), user.tier().threadUnlocked());
    }

    private boolean isNotUser(String role) {
        return !role.equals("UTILISATEUR");
    }

    private boolean canCreateThread(int countCreatedThread, int threadUnlocked) {
        return countCreatedThread < threadUnlocked;
    }
}
