package com.GreenThumb.api.apigateway.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${greenthumb.upload.dir:./uploads/users}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir directement /users/** depuis ./uploads/users
        Path usersDir = Paths.get(uploadDir);
        String usersLocation = "file:" + usersDir.toAbsolutePath().toString() + "/";
        registry.addResourceHandler("/users/**")
                .addResourceLocations(usersLocation, "classpath:/static/uploads/users/")
                .setCachePeriod(3600);

        // (optionnel) garder /uploads/** pour compatibilité si nécessaire
        Path uploadsRoot = usersDir.getParent() != null ? usersDir.getParent() : usersDir;
        String uploadsLocation = "file:" + uploadsRoot.toAbsolutePath().toString() + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsLocation, "classpath:/static/")
                .setCachePeriod(3600);
    }
}