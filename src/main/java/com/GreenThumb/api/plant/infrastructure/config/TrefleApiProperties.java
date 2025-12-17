package com.GreenThumb.api.plant.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "trefle.api")
public class TrefleApiProperties {
    private String baseUrl;
    private String key;
    private RateLimit rateLimit = new RateLimit();

    @Data
    public static class RateLimit {
        private int maxRequests = 120;
        private int periodInSeconds = 60;
    }
}