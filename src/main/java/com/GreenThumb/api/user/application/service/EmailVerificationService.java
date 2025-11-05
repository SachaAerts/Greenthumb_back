package com.GreenThumb.api.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redis;
    private static final Duration TTL = Duration.ofHours(24);

    private String key(String token) { return "email_verif:" + token; }

    public String generateToken() { return UUID.randomUUID().toString(); }

    public void storeToken(String token, String email) {
        redis.opsForValue().set(key(token), email, TTL);
    }

    public Optional<String> consumeToken(String token) {
        String email = redis.opsForValue().get(key(token));
        if (email != null) redis.delete(key(token));
        return Optional.ofNullable(email);
    }
}
