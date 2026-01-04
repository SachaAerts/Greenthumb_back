package com.GreenThumb.api.forum.domain.services;

import com.GreenThumb.api.infrastructure.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ForumMediaStorageService {

    private final CloudinaryService cloudinaryService;
    private final String cloudinaryFolder;

    public ForumMediaStorageService(
            CloudinaryService cloudinaryService,
            @Value("${greenthumb.cloudinary.forum-folder}") String forumFolder
    ) {
        this.cloudinaryService = cloudinaryService;
        this.cloudinaryFolder = forumFolder;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }

        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("L'image ne doit pas dépasser 5MB");
        }

        return cloudinaryService.uploadImage(file, cloudinaryFolder);
    }

    public List<String> uploadImages(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        return files.stream()
                .map(file -> {
                    try {
                        return uploadImage(file);
                    } catch (Exception e) {
                        log.error("Failed to upload image {}: {}", file.getOriginalFilename(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        if (!imageUrl.contains("cloudinary.com")) {
            log.debug("Not a Cloudinary URL, skipping deletion: {}", imageUrl);
            return;
        }

        try {
            cloudinaryService.deleteImageByUrl(imageUrl);
            log.info("Forum image deleted: {}", imageUrl);
        } catch (Exception e) {
            log.warn("Failed to delete forum image (non-blocking): {}", e.getMessage());
        }
    }

    public void deleteImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        for (String url : imageUrls) {
            deleteImage(url);
        }
    }
}
