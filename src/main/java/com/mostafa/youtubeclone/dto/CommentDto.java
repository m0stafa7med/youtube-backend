package com.mostafa.youtubeclone.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String id;
    private String commentText;
    private String authorId;
    private LocalDateTime createdAt;
    private String authorFullName;
    private String authorPicture;
    private String parentCommentId;
    private List<CommentDto> replies;
}