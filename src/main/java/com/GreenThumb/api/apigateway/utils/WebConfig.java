package com.GreenThumb.api.apigateway.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir /users/** depuis src/main/resources/static/users/ (développement) et classpath (production)
        Path staticUsersDir = Paths.get("src/main/resources/static/users").toAbsolutePath().normalize();
        Path staticResourceDir = Paths.get("src/main/resources/static/articles").toAbsolutePath().normalize();
        String fileLocationUser = "file:" + staticUsersDir.toString() + "/";
        String  fileLocationResource = "file:" + staticResourceDir.toString() + "/";

        registry.addResourceHandler("/users/**")
                .addResourceLocations(fileLocationUser, "classpath:/static/users/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/articles/**")
                .addResourceLocations(fileLocationResource,  "classpath:/static/articles/")
                .setCachePeriod(3600);
    }
}