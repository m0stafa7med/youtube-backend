package com.mostafa.youtubeclone.controller;

import com.mostafa.youtubeclone.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{subfolder}/{filename}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String subfolder,
            @PathVariable String filename) throws IOException {

        // Only allow videos and thumbnails subfolders
        if (!subfolder.equals("videos") && !subfolder.equals("thumbnails")) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = fileStorageService.getFilePath(subfolder, filename);

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .body(resource);
    }
}
