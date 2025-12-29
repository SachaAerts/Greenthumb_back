package com.GreenThumb.api.plant.domain.services;

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
public class PlantImageStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final CloudinaryService cloudinaryService;
    private final String cloudinaryFolder;

    public PlantImageStorageService(
            CloudinaryService cloudinaryService,
            @Value("${greenthumb.cloudinary.plants-folder}") String cloudinaryFolder
    ) {
        this.cloudinaryService = cloudinaryService;
        this.cloudinaryFolder = cloudinaryFolder;
        log.info("PlantImageStorageService initialized with folder: {}", cloudinaryFolder);
    }

    public boolean isBase64Image(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        return imageUrl.startsWith("data:image/");
    }

    public String processPlantImage(String imageUrl) {
        if (!isBase64Image(imageUrl)) {
            log.debug("Image is already a URL: {}", imageUrl);
            return imageUrl;
        }

        try {
            byte[] imageBytes = decodeBase64Image(imageUrl);
            validateImageSize(imageBytes);
            String extension = extractImageExtension(imageUrl);

            MultipartFile file = createMultipartFile(imageBytes, "plant" + extension);

            String cloudinaryUrl = cloudinaryService.uploadImage(file, cloudinaryFolder);
            log.info("Plant image uploaded: {}", cloudinaryUrl);

            return cloudinaryUrl;

        } catch (Exception e) {
            log.error("Failed to process plant image: {}", e.getMessage());
            throw new RuntimeException("Erreur lors du traitement de l'image", e);
        }
    }

    public void deletePlantImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            log.debug("Not a Cloudinary URL, skipping deletion: {}", imageUrl);
            return;
        }

        try {
            cloudinaryService.deleteImageByUrl(imageUrl);
            log.info("Plant image deleted: {}", imageUrl);
        } catch (Exception e) {
            log.warn("Failed to delete plant image (non-blocking): {}", e.getMessage());
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

    private void validateImageSize(byte[] imageBytes) {
        if (imageBytes.length > MAX_FILE_SIZE) {
            throw new RuntimeException("L'image est trop volumineuse. La taille maximale est de 5MB.");
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