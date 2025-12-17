package com.GreenThumb.api.apigateway.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${greenthumb.frontend.url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir /users/** depuis src/main/resources/static/users/ (développement) et classpath (production)
        Path staticUsersDir = Paths.get("src/main/resources/static/users").toAbsolutePath().normalize();
        Path staticResourceDir = Paths.get("src/main/resources/static/articles").toAbsolutePath().normalize();
        String fileLocationUser = "file:" + staticUsersDir.toString() + "/";
        String  fileLocationResource = "file:" + staticResourceDir.toString() + "/";
        String userFileLocation = "file:" + staticUsersDir.toString() + "/";

        registry.addResourceHandler("/users/**")
                .addResourceLocations(fileLocationUser, "classpath:/static/users/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/articles/**")
                .addResourceLocations(fileLocationResource,  "classpath:/static/articles/")
                .addResourceLocations(userFileLocation, "classpath:/static/users/")
                .setCachePeriod(3600);

        // Servir /static/plants/** depuis src/main/resources/static/plants/ (développement) et classpath (production)
        Path staticPlantsDir = Paths.get("src/main/resources/static/plants").toAbsolutePath().normalize();
        String plantFileLocation = "file:" + staticPlantsDir.toString() + "/";

        registry.addResourceHandler("/static/plants/**")
                .addResourceLocations(plantFileLocation, "classpath:/static/plants/")
                .setCachePeriod(3600);
    }
}