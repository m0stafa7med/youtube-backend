package com.mostafa.youtubeclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "Playlist")
public class Playlist {
    @Id
    private String id;
    private String name;
    private String description;
    private String userId;
    private List<String> videoIds = new ArrayList<>();
    private String visibility; // PUBLIC, PRIVATE
    private LocalDateTime createdAt;

    public void addVideo(String videoId) {
        videoIds.add(videoId);
    }

    public void removeVideo(String videoId) {
        videoIds.remove(videoId);
    }
}
