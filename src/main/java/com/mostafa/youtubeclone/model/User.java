package com.mostafa.youtubeclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "Users")
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String picture;
    private String emailAddress;
    private String sub;
    private Set<String> subscribedToUsers = new HashSet<>();
    private Set<String> subscribers = new HashSet<>();
    private Set<String> videoHistory = new LinkedHashSet<>();
    private Set<String> likedVideos = new HashSet<>();
    private Set<String> disLikedVideos = new HashSet<>();


    public void addToLikeVideos(String videoId) {
        likedVideos.add(videoId);
    }

    public void removeFromLikedVideos(String videoId) {
        likedVideos.remove(videoId);
    }

    public void removeFromDislikedVideos(String videoId) {
        disLikedVideos.remove(videoId);
    }

    public void addToDislikedVideos(String videoId) {
        disLikedVideos.add(videoId);
    }

    public void addToVideoHistory(String videoId) {
        videoHistory.add(videoId);
    }

    public void addToSubscribedToUsers(String userId) {
        subscribedToUsers.add(userId);
    }

    public void addToSubscribers(String userId) {
        subscribers.add(userId);
    }

    public void removeFromSubscribedToUsers(String userId) {
        subscribedToUsers.remove(userId);
    }

    public void removeFromSubscribers(String userId) {
        subscribers.remove(userId);
    }

}
