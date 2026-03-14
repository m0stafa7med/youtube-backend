package com.mostafa.youtubeclone.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    private String id;
    private String text;
    private String authorId;
    private Integer likeCount;
    private Integer disLikeCount;
    private LocalDateTime createdAt;
    private String parentCommentId;
    private List<Comment> replies = new ArrayList<>();

    public void addReply(Comment reply) {
        replies.add(reply);
    }
}
