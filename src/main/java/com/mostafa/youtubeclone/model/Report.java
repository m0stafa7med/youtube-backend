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
@Document(value = "Report")
public class Report {
    @Id
    private String id;
    private String reporterId;
    private String targetId;
    private String targetType; // VIDEO, COMMENT
    private String reason;
    private String status; // PENDING, REVIEWED
    private LocalDateTime createdAt;
}
