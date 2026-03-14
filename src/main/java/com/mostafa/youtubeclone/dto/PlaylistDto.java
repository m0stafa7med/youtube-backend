package com.mostafa.youtubeclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDto {
    private String id;
    private String name;
    private String description;
    private String userId;
    private List<String> videoIds;
    private String visibility;
    private LocalDateTime createdAt;
}
