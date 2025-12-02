package com.mostafa.youtubeclone.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String commentText;
    private String authorId;
}