package com.GreenThumb.api.apigateway.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        return uploadImage(file, folder, Map.of());
    }

    public String uploadImage(
            MultipartFile file,
            String folder,
            Map<String, Object> extraOptions
    ) throws IOException {

        log.info("Uploading: folder={}, file={}, size={}KB",
                folder, file.getOriginalFilename(), file.getSize() / 1024);

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "quality", "auto:good",
                "fetch_format", "auto"
        );
        options.putAll(extraOptions);

        Map<String, Object> result = cloudinary.uploader().upload(
                file.getBytes(),
                options
        );

        String imageUrl = (String) result.get("secure_url");
        log.info("Uploaded: {}", imageUrl);

        return imageUrl;
    }

    public void deleteImageByUrl(String imageUrl) throws IOException {
        String publicId = extractPublicId(imageUrl);
        if (publicId != null) {
            deleteImage(publicId);
        } else {
            log.warn("Could not extract publicId from URL: {}", imageUrl);
        }
    }

    public void deleteImage(String publicId) throws IOException {
        log.info("Deleting: publicId={}", publicId);

        Map<String, Object> result = cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );

        String status = (String) result.get("result");
        if ("ok".equals(status)) {
            log.info("Deleted successfully");
        } else {
            log.warn("Delete status: {}", status);
        }
    }

    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return null;
        }

        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) return null;

            String path = parts[1].replaceFirst("v\\d+/", "");
            int lastDot = path.lastIndexOf('.');
            return lastDot > 0 ? path.substring(0, lastDot) : path;

        } catch (Exception e) {
            log.error("Failed to extract publicId from: {}", imageUrl);
            return null;
        }
    }

    public String getTransformedUrl(String imageUrl, String transformation) {
        if (imageUrl == null || !imageUrl.contains("/upload/")) {
            return imageUrl;
        }

        return imageUrl.replace("/upload/", "/upload/" + transformation + "/");
    }
}