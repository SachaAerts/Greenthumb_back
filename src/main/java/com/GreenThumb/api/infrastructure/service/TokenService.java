package com.GreenThumb.api.infrastructure.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class TokenService {
    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public TokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration.access}") long accessTokenExpirationMs,
            @Value("${security.jwt.expiration.refresh}") long refreshTokenExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(String username, Map<String, Object> claims) {
        return generateToken(username, claims, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, Map.of(), refreshTokenExpirationMs);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Map<String, Object> extractClaims(String token) {
       return Jwts.parser()
               .verifyWith(secretKey)
               .build()
               .parseSignedClaims(token)
               .getPayload();
    }

    public boolean isEquals(String userToken, String token) {
        if (userToken == null || token == null) {
            return false;
        }

        return userToken.trim().equals(token.trim());
    }

    private String generateToken(String subject, Map<String, Object> claims, long expirationMs) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();

    }
}
