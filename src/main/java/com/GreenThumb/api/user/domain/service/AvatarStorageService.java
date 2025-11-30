package com.GreenThumb.api.user.domain.service;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private static final String DEFAULT_AVATAR = "users/default.png";
    private static final String USERS_FOLDER = "users/";

    private final Path staticDir;

    public AvatarStorageService() {
        this.staticDir = Paths.get("src/main/resources/static/users").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.staticDir);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de créer le dossier static/users.", ex);
        }
    }

    public String storeUserImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return DEFAULT_AVATAR;
        }

        byte[] imageBytes = decodeBase64Image(base64Image);
        String extension = extractImageExtension(base64Image);
        String filename = generateFilename(extension);

        saveImageToFile(imageBytes, filename);

        return USERS_FOLDER + filename;
    }

    public String replaceUserImage(String oldAvatarPath, String newBase64Image) {
        if (newBase64Image == null || newBase64Image.isEmpty()) {
            deleteOldAvatar(oldAvatarPath);
            return DEFAULT_AVATAR;
        }

        if (newBase64Image.equals(oldAvatarPath)) {
            return newBase64Image;
        }

        byte[] imageBytes = decodeBase64Image(newBase64Image);
        String extension = extractImageExtension(newBase64Image);
        String filename = generateFilename(extension);

        saveImageToFile(imageBytes, filename);

        deleteOldAvatar(oldAvatarPath);

        return USERS_FOLDER + filename;
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

    private void saveImageToFile(byte[] imageBytes, String filename) {
        Path targetLocation = staticDir.resolve(filename);

        try (FileOutputStream fos = new FileOutputStream(targetLocation.toFile())) {
            fos.write(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier.", e);
        }
    }

    private void deleteOldAvatar(String avatarPath) {
        if (avatarPath == null || avatarPath.equals(DEFAULT_AVATAR)) {
            return;
        }

        String filename = avatarPath.replace(USERS_FOLDER, "");
        Path fileToDelete = staticDir.resolve(filename);

        try {
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
            System.err.println("Impossible de supprimer l'ancien avatar: " + fileToDelete);
        }
    }
}