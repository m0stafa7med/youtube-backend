package com.mostafa.youtubeclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String fullName;
    private String picture;
    private String emailAddress;
    private int subscriberCount;
    private int videoCount;
}
