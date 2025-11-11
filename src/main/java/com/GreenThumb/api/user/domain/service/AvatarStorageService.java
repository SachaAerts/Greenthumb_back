package com.GreenThumb.api.user.domain.service;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class AvatarStorageService {

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
            throw new RuntimeException("Type de fichier non supporté. Seuls PNG/JPG/JPEG sont autorisés.");
        }

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(dataPart);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64 invalide.", e);
        }

        String filename = UUID.randomUUID() + ext;
        Path targetLocation = staticDir.resolve(filename);

        try (FileOutputStream fos = new FileOutputStream(targetLocation.toFile())) {
            fos.write(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier.", e);
        }

        return "users/" + filename;
    }
}
