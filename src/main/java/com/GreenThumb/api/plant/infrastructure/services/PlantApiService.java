package com.GreenThumb.api.plant.infrastructure.services;

import com.GreenThumb.api.plant.domain.exceptions.TrefleApiException;
import com.GreenThumb.api.plant.domain.repository.PlantApiRepository;
import com.GreenThumb.api.plant.infrastructure.config.TrefleApiProperties;
import com.GreenThumb.api.plant.infrastructure.entity.api.TreflePlantResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Slf4j
@Service
public class PlantApiService implements PlantApiRepository {
    private final WebClient webClient;
    private final TrefleApiProperties properties;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    public PlantApiService(WebClient trefleWebClient, TrefleApiProperties properties) {
        this.webClient = trefleWebClient;
        this.properties = properties;
    }

    @Override
    @Cacheable(value = "plantSearch", key = "#query + '_' + #page")
    public TreflePlantResponse searchPlants(String query, int page) {
        log.info("Searching plants with query='{}', page={}", query, page);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/plants/search")
                        .queryParam("token", properties.getKey())
                        .queryParam("q", query.trim())
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(TreflePlantResponse.class)
                .timeout(REQUEST_TIMEOUT)
                .doOnSuccess(response -> {
                    if (response != null) {
                        log.info("Found {} plants for query '{}' (total: {})",
                                response.getData() != null ? response.getData().size() : 0,
                                query,
                                response.getMeta() != null ? response.getMeta().getTotal() : 0);
                    }
                })
                .onErrorMap(WebClientResponseException.Unauthorized.class, e -> {
                    log.error("Unauthorized: Invalid Trefle API key");
                    return new TrefleApiException("Invalid API key", e);
                })
                .onErrorMap(WebClientResponseException.TooManyRequests.class, e -> {
                    log.error("Rate limit exceeded for Trefle API");
                    return new TrefleApiException("Too many requests, please try again later", e);
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error("Error calling Trefle API: {} - {}", e.getStatusCode(), e.getMessage());
                    return new TrefleApiException("Failed to search plants from Trefle API", e);
                })
                .onErrorMap(e -> !(e instanceof TrefleApiException), e -> {
                    log.error("Unexpected error while searching plants", e);
                    return new TrefleApiException("Unexpected error occurred", e);
                })
                .block();
    }
}
