package com.mostafa.youtubeclone.service;

import com.mostafa.youtubeclone.dto.UserDto;
import com.mostafa.youtubeclone.model.User;
import com.mostafa.youtubeclone.repository.UserRepository;
import com.mostafa.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final UserRegistrationService userRegistrationService;

    public User getCurrentUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sub = jwt.getClaim("sub");

        return userRepository.findBySub(sub)
                .orElseGet(() -> {
                    // Auto-register the user if they have a valid JWT but aren't in the database
                    userRegistrationService.registerUser(jwt.getTokenValue());
                    return userRepository.findBySub(sub)
                            .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub - " + sub));
                });
    }

    public Optional<User> findCurrentUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sub = jwt.getClaim("sub");
        Optional<User> user = userRepository.findBySub(sub);
        if (user.isEmpty()) {
            // Auto-register
            userRegistrationService.registerUser(jwt.getTokenValue());
            user = userRepository.findBySub(sub);
        }
        return user;
    }

    public void addToLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToLikeVideos(videoId);
        userRepository.save(currentUser);
    }

    public boolean ifLikedVideo(String videoId) {
        return getCurrentUser().getLikedVideos().stream().anyMatch(likedVideo -> likedVideo.equals(videoId));
    }

    public boolean ifDisLikedVideo(String videoId) {
        return getCurrentUser().getDisLikedVideos().stream().anyMatch(likedVideo -> likedVideo.equals(videoId));
    }

    public void removeFromLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void removeFromDislikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromDislikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addToDisLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToDislikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addVideoToHistory(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToVideoHistory(videoId);
        userRepository.save(currentUser);
    }

    public void subscribeUser(String userId) {
        User currentUser = getCurrentUser();
        currentUser.addToSubscribedToUsers(userId);

        User user = getUserById(userId);
        user.addToSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public void unSubscribeUser(String userId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromSubscribedToUsers(userId);

        User user = getUserById(userId);
        user.removeFromSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public Set<String> userHistory(String userId) {
        User user = getUserById(userId);
        return user.getVideoHistory();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with userId " + userId));
    }

    public UserDto getUserProfile(String userId) {
        User user = getUserById(userId);
        int videoCount = videoRepository.findByUserId(userId).size();
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .picture(user.getPicture())
                .emailAddress(user.getEmailAddress())
                .subscriberCount(user.getSubscribers() != null ? user.getSubscribers().size() : 0)
                .videoCount(videoCount)
                .build();
    }
}