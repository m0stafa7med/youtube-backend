package com.mostafa.youtubeclone.dto;

import com.mostafa.youtubeclone.model.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoDto {
    private String id;
    private String title;
    private String description;
    private Set<String> tags;
    private String videoUrl;
    private VideoStatus videoStatus;
    private String thumbnailUrl;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private Long duration;
    private String category;
    private String userId;
    private String userFullName;
    private String userPicture;
}

