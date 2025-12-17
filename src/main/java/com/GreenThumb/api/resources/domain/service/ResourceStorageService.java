package com.GreenThumb.api.resources.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;


@Slf4j
@Service
public class ResourceStorageService {
    private static final String DEFAULT_IMAGE = "articles/default.png";

    private static final String RESOURCE_FOLDER = "articles/";

    private final Path staticDir;
    public ResourceStorageService() {
        this.staticDir = Paths.get("src/main/resources/static/articles").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.staticDir);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de créer le dossier static/articles.", ex);
        }
    }

    public String storeResourceImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return DEFAULT_IMAGE;
        }

        byte[] imageBytes = decodeBase64Image(base64Image);
        String extension = extractImageExtension(base64Image);
        String filename = generateFilename(extension);

        saveImageToFile(imageBytes, filename);

        return RESOURCE_FOLDER + filename;
    }

    public String replaceUserImage(String oldImagePath, String newImage) {
        if (newImage == null || newImage.isEmpty()) {
            deleteImage(oldImagePath);
            return DEFAULT_IMAGE;
        }

        if (newImage.equals(oldImagePath)) {
            return newImage;
        }

        byte[] imageBytes = decodeBase64Image(newImage);
        String extension = extractImageExtension(newImage);
        String filename = generateFilename(extension);

        saveImageToFile(imageBytes, filename);

        deleteImage(oldImagePath);

        return RESOURCE_FOLDER + filename;
    }

    public void deleteImage(String path) {
        if (path == null || path.equals(DEFAULT_IMAGE)) {
            return;
        }

        String filename = path.replace(RESOURCE_FOLDER, "");
        Path fileToDelete = staticDir.resolve(filename);

        try {
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
           log.error("Impossible de supprimer l'image: " + fileToDelete);
        }
    }

    private byte[] decodeBase64Image(String base64Image) {
        String[] parts = base64Image.split(",");
        String dataPart = parts.length > 1 ? parts[1] : parts[0];

        try {
            return Base64.getDecoder().decode(dataPart);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64 invalide.", e);
        }
    }

    private String extractImageExtension(String base64Image) {
        if (base64Image.startsWith("data:image/jpeg") || base64Image.startsWith("data:image/jpg")) {
            return ".jpg";
        } else if (base64Image.startsWith("data:image/png")) {
            return ".png";
        } else {
            throw new RuntimeException("Type de fichier non supporté. Seuls PNG/JPG/JPEG sont autorisés.");
        }
    }

    private String generateFilename(String extension) {
        return UUID.randomUUID() + extension;
    }

    private void saveImageToFile(byte[] imagesBytes, String filename) {
        Path targetLocation = staticDir.resolve(filename);

        try (FileOutputStream fos = new FileOutputStream(targetLocation.toFile())) {
            fos.write(imagesBytes);
        }  catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier.", e);
        }
    }
}
