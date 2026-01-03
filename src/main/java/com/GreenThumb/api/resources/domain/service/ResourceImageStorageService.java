package com.GreenThumb.api.resources.domain.service;

import com.GreenThumb.api.infrastructure.service.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
public class ResourceImageStorageService {

    private final CloudinaryService cloudinaryService;
    private final String defaultImageUrl;
    private final String cloudinaryFolder;

    public ResourceImageStorageService(
            CloudinaryService cloudinaryService,
            @Value("${greenthumb.resources.default-url}") String defaultImageUrl,
            @Value("${greenthumb.cloudinary.resources-folder}") String cloudinaryFolder
    ) {
        this.cloudinaryService = cloudinaryService;
        this.defaultImageUrl = defaultImageUrl;
        this.cloudinaryFolder = cloudinaryFolder;

        log.info("ResourceStorageService initialized:");
        log.info("   - Cloudinary folder: {}", cloudinaryFolder);
        log.info("   - Default image: {}", defaultImageUrl);
    }

    public String storeResourceImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            log.debug("Empty image, returning default");
            return defaultImageUrl;
        }

        try {
            byte[] imageBytes = decodeBase64Image(base64Image);
            String extension = extractImageExtension(base64Image);
            MultipartFile file = createMultipartFile(imageBytes, "resource" + extension);

            String uploadedUrl = cloudinaryService.uploadImage(file, cloudinaryFolder);
            log.info("Resource image uploaded: {}", uploadedUrl);

            return uploadedUrl;

        } catch (Exception e) {
            log.error("Failed to upload resource image: {}", e.getMessage());
            throw new RuntimeException("Erreur upload image ressource", e);
        }
    }

    public String replaceResourceImage(String oldImageUrl, String newBase64Image) {
        if (newBase64Image == null || newBase64Image.isEmpty()) {
            log.debug("Empty new image, deleting old and returning default");
            deleteImage(oldImageUrl);
            return defaultImageUrl;
        }

        if (newBase64Image.equals(oldImageUrl)) {
            log.debug("Same URL, no change needed");
            return newBase64Image;
        }

        try {
            String newUrl = storeResourceImage(newBase64Image);

            deleteImage(oldImageUrl);

            return newUrl;

        } catch (Exception e) {
            log.error("Failed to replace resource image: {}", e.getMessage());
            throw new RuntimeException("Erreur remplacement image ressource", e);
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.equals(defaultImageUrl)) {
            log.debug("Default image or null, skipping deletion");
            return;
        }

        if (!imageUrl.contains("cloudinary.com")) {
            log.debug("Not a Cloudinary URL, skipping deletion: {}", imageUrl);
            return;
        }

        try {
            cloudinaryService.deleteImageByUrl(imageUrl);
            log.info("🗑️ Resource image deleted: {}", imageUrl);
        } catch (Exception e) {
            log.warn("⚠️ Failed to delete resource image (non-blocking): {}", e.getMessage());
        }
    }

    private byte[] decodeBase64Image(String base64Image) {
        String[] parts = base64Image.split(",");
        String dataPart = parts.length > 1 ? parts[1] : parts[0];

        try {
            return Base64.getDecoder().decode(dataPart);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64 invalide", e);
        }
    }

    private String extractImageExtension(String base64Image) {
        if (base64Image.startsWith("data:image/jpeg") || base64Image.startsWith("data:image/jpg")) {
            return ".jpg";
        } else if (base64Image.startsWith("data:image/png")) {
            return ".png";
        } else if (base64Image.startsWith("data:image/webp")) {
            return ".webp";
        } else {
            return ".jpg";
        }
    }

    private MultipartFile createMultipartFile(byte[] content, String filename) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return filename;
            }

            @Override
            public String getContentType() {
                if (filename.endsWith(".png")) return "image/png";
                if (filename.endsWith(".webp")) return "image/webp";
                return "image/jpeg";
            }

            @Override
            public boolean isEmpty() {
                return content.length == 0;
            }

            @Override
            public long getSize() {
                return content.length;
            }

            @Override
            public byte[] getBytes() {
                return content;
            }

            @Override
            public java.io.InputStream getInputStream() {
                return new ByteArrayInputStream(content);
            }

            @Override
            public void transferTo(java.io.File dest) throws IOException {
                java.nio.file.Files.write(dest.toPath(), content);
            }
        };
    }
}