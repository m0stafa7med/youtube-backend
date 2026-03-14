package com.mostafa.youtubeclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "Notification")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String message;
    private String videoId;
    private String videoThumbnail;
    private String type; // NEW_VIDEO, NEW_SUBSCRIBER
    private boolean read;
    private LocalDateTime createdAt;
}
