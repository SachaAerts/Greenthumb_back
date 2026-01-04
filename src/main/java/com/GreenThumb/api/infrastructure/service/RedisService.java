package com.GreenThumb.api.infrastructure.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void saveJson(String key, String json) {
        redisTemplate.opsForValue().set(key, json);
    }
    public void expiry(String key, long duration, TimeUnit unit) {
        redisTemplate.expire(key, duration, unit);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean checkKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
}
