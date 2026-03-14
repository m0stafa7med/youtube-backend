package com.mostafa.youtubeclone.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${upload.storage-dir:/uploads}")
    private String storageDir;

    @Value("${upload.base-url:}")
    private String baseUrl;

    @Value("${upload.video.max-size-mb:50}")
    private long videoMaxSizeMb;

    @Value("${upload.thumbnail.max-size-mb:5}")
    private long thumbnailMaxSizeMb;

    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/webm", "video/quicktime", "video/x-msvideo", "video/x-matroska"
    );

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @PostConstruct
    public void init() {
        try {
            Path videosDir = Paths.get(storageDir, "videos");
            Path thumbnailsDir = Paths.get(storageDir, "thumbnails");
            Files.createDirectories(videosDir);
            Files.createDirectories(thumbnailsDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    public void validateVideoFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid video format. Allowed: mp4, webm, mov, avi, mkv");
        }
        long maxBytes = videoMaxSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Video file too large. Maximum size: " + videoMaxSizeMb + "MB");
        }
    }

    public void validateThumbnailFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid image format. Allowed: jpeg, png, webp, gif");
        }
        long maxBytes = thumbnailMaxSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thumbnail file too large. Maximum size: " + thumbnailMaxSizeMb + "MB");
        }
    }

    public String uploadFile(MultipartFile file, String subfolder) {
        try {
            String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String filename = UUID.randomUUID() + "." + extension;

            Path filePath = Paths.get(storageDir, subfolder, filename);
            Files.copy(file.getInputStream(), filePath);

            return baseUrl + "/api/files/" + subfolder + "/" + filename;
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error saving file"
            );
        }
    }

    public String uploadVideo(MultipartFile file) {
        return uploadFile(file, "videos");
    }

    public String uploadThumbnail(MultipartFile file) {
        return uploadFile(file, "thumbnails");
    }

    public void deleteFile(String fileUrl) {
        try {
            // URL format: .../api/files/videos/filename.ext or .../api/files/thumbnails/filename.ext
            String path = fileUrl.substring(fileUrl.indexOf("/api/files/") + "/api/files/".length());
            Path filePath = Paths.get(storageDir, path);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            // Log but don't fail if file doesn't exist
        }
    }

    public Path getFilePath(String subfolder, String filename) {
        return Paths.get(storageDir, subfolder, filename);
    }
}
