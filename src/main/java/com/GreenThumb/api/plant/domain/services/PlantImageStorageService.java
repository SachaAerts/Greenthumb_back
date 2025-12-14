package com.GreenThumb.api.plant.domain.services;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class PlantImageStorageService {

    private static final String PLANTS_FOLDER = "plants/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final Path staticDir;

    public PlantImageStorageService() {
        this.staticDir = Paths.get("src/main/resources/static/plants").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.staticDir);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de créer le dossier static/plants.", ex);
        }
    }

    /**
     * Vérifie si la chaîne est une image encodée en base64
     */
    public boolean isBase64Image(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        return imageUrl.startsWith("data:image/");
    }

    /**
     * Stocke l'image de la plante si c'est un base64, sinon retourne l'URL telle quelle
     */
    public String processPlantImage(String imageUrl) {
        if (!isBase64Image(imageUrl)) {
            return imageUrl;
        }

        byte[] imageBytes = decodeBase64Image(imageUrl);
        validateImageSize(imageBytes);
        String extension = extractImageExtension(imageUrl);
        String filename = generateFilename(extension);

        saveImageToFile(imageBytes, filename);

        return PLANTS_FOLDER + filename;
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

    private void validateImageSize(byte[] imageBytes) {
        if (imageBytes.length > MAX_FILE_SIZE) {
            throw new RuntimeException("L'image est trop volumineuse. La taille maximale est de 5MB.");
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

    public void deletePlantImage(String imagePath) {
        if (imagePath == null || !imagePath.startsWith(PLANTS_FOLDER)) {
            return;
        }

        String filename = imagePath.replace(PLANTS_FOLDER, "");
        Path fileToDelete = staticDir.resolve(filename);

        try {
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
            System.err.println("Impossible de supprimer l'ancienne image de plante: " + fileToDelete);
        }
    }
}
