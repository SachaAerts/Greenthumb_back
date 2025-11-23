package com.GreenThumb.api.user.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private static final Logger log = LoggerFactory.getLogger(AvatarStorageService.class);
    private final Path uploadDir;

    public AvatarStorageService(@Value("${greenthumb.upload.dir:/app/uploads/users}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.uploadDir);
            log.info("Dossier d'uploads créé avec succès: {}", uploadDir);
        } catch (IOException ex) {
            log.error("Impossible de créer le dossier d'uploads: {}", uploadDir, ex);
            throw new RuntimeException("Impossible de créer le dossier d'uploads: " + uploadDir, ex);
        }
    }

    public String storeUserImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return "users/default.png";
        }

        String[] parts = base64Image.split(",");
        String dataPart = parts.length > 1 ? parts[1] : parts[0];

        String ext;
        if (base64Image.startsWith("data:image/jpeg") || base64Image.startsWith("data:image/jpg")) {
            ext = ".jpg";
        } else if (base64Image.startsWith("data:image/png")) {
            ext = ".png";
        } else {
            log.error("Type de fichier non supporté: {}", base64Image.substring(0, Math.min(50, base64Image.length())));
            throw new RuntimeException("Type de fichier non supporté. Seuls PNG/JPG/JPEG sont autorisés.");
        }

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(dataPart);
            log.info("Image décodée avec succès, taille: {} bytes", imageBytes.length);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors du décodage Base64", e);
            throw new RuntimeException("Base64 invalide.", e);
        }

        String filename = UUID.randomUUID() + ext;
        Path targetLocation = uploadDir.resolve(filename);

        log.info("Tentative d'enregistrement de l'image à: {}", targetLocation);

        try (FileOutputStream fos = new FileOutputStream(targetLocation.toFile())) {
            fos.write(imageBytes);
            log.info("Image enregistrée avec succès: {}", filename);
        } catch (IOException e) {
            log.error("Erreur lors de l'enregistrement du fichier à: {}", targetLocation, e);
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier: " + e.getMessage(), e);
        }

        return "users/" + filename;
    }
}