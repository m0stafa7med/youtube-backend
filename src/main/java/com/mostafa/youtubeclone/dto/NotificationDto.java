package com.mostafa.youtubeclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private String id;
    private String message;
    private String videoId;
    private String videoThumbnail;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;
}
