package com.GreenThumb.api.user.domain.service;

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
public class AvatarStorageService {

    private final CloudinaryService cloudinaryService;
    private final String defaultAvatarUrl;
    private final String cloudinaryFolder;

    public AvatarStorageService(
            CloudinaryService cloudinaryService,
            @Value("${greenthumb.avatar.default-url}") String defaultAvatarUrl,
            @Value("${greenthumb.cloudinary.folder}") String cloudinaryFolder
    ) {
        this.cloudinaryService = cloudinaryService;
        this.defaultAvatarUrl = defaultAvatarUrl;
        this.cloudinaryFolder = cloudinaryFolder;

        log.info("✅ AvatarStorageService initialized:");
        log.info("   - Cloudinary folder: {}", cloudinaryFolder);
        log.info("   - Default avatar: {}", defaultAvatarUrl);
    }

    /**
     * Stocke un avatar (base64) sur Cloudinary
     */
    public String storeUserImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            log.debug("Empty image, returning default avatar");
            return defaultAvatarUrl;
        }

        try {
            byte[] imageBytes = decodeBase64(base64Image);
            String extension = getExtension(base64Image);
            MultipartFile file = toMultipartFile(imageBytes, "avatar" + extension);

            String uploadedUrl = cloudinaryService.uploadImage(file, cloudinaryFolder);
            log.info("📤 Avatar uploaded: {}", uploadedUrl);

            return uploadedUrl;

        } catch (Exception e) {
            log.error("❌ Failed to upload avatar: {}", e.getMessage());
            throw new RuntimeException("Erreur upload avatar", e);
        }
    }

    /**
     * Remplace un avatar
     */
    public String replaceUserImage(String oldUrl, String newBase64) {
        if (newBase64 == null || newBase64.isEmpty()) {
            log.debug("Empty new image, deleting old and returning default");
            deleteAvatar(oldUrl);
            return defaultAvatarUrl;
        }

        if (newBase64.equals(oldUrl)) {
            log.debug("Same URL, no change needed");
            return newBase64;
        }

        try {
            // Upload nouveau
            String newUrl = storeUserImage(newBase64);

            // Supprimer ancien
            deleteAvatar(oldUrl);

            return newUrl;

        } catch (Exception e) {
            log.error("❌ Failed to replace avatar: {}", e.getMessage());
            throw new RuntimeException("Erreur remplacement avatar", e);
        }
    }

    /**
     * Supprime un avatar sur Cloudinary
     */
    private void deleteAvatar(String url) {
        if (url == null || url.equals(defaultAvatarUrl)) {
            log.debug("Default avatar or null, skipping deletion");
            return;
        }

        if (!url.contains("cloudinary.com")) {
            log.debug("Not a Cloudinary URL, skipping deletion: {}", url);
            return;
        }

        try {
            cloudinaryService.deleteImageByUrl(url);
            log.info("🗑️ Avatar deleted: {}", url);
        } catch (Exception e) {
            log.warn("⚠️ Failed to delete avatar (non-blocking): {}", e.getMessage());
        }
    }

    // ========== Utilitaires ==========

    private byte[] decodeBase64(String base64) {
        String data = base64.contains(",") ? base64.split(",")[1] : base64;
        return Base64.getDecoder().decode(data);
    }

    private String getExtension(String base64) {
        if (base64.contains("image/png")) return ".png";
        if (base64.contains("image/webp")) return ".webp";
        return ".jpg";
    }

    private MultipartFile toMultipartFile(byte[] content, String filename) {
        return new MultipartFile() {
            @Override public String getName() { return "file"; }
            @Override public String getOriginalFilename() { return filename; }
            @Override public String getContentType() {
                if (filename.endsWith(".png")) return "image/png";
                if (filename.endsWith(".webp")) return "image/webp";
                return "image/jpeg";
            }
            @Override public boolean isEmpty() { return content.length == 0; }
            @Override public long getSize() { return content.length; }
            @Override public byte[] getBytes() { return content; }
            @Override public java.io.InputStream getInputStream() {
                return new ByteArrayInputStream(content);
            }
            @Override public void transferTo(java.io.File dest) throws IOException {
                java.nio.file.Files.write(dest.toPath(), content);
            }
        };
    }
}