package com.mostafa.youtubeclone.service;

import com.mostafa.youtubeclone.dto.NotificationDto;
import com.mostafa.youtubeclone.model.Notification;
import com.mostafa.youtubeclone.model.User;
import com.mostafa.youtubeclone.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public void createNotification(String userId, String message, String videoId, String videoThumbnail, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setVideoId(videoId);
        notification.setVideoThumbnail(videoThumbnail);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void notifySubscribers(String uploaderUserId, String videoId, String videoTitle, String videoThumbnail) {
        User uploader = userService.getUserById(uploaderUserId);
        Set<String> subscribers = uploader.getSubscribers();
        if (subscribers != null) {
            String message = uploader.getFullName() + " uploaded a new video: " + videoTitle;
            for (String subscriberId : subscribers) {
                createNotification(subscriberId, message, videoId, videoThumbnail, "NEW_VIDEO");
            }
        }
    }

    public List<NotificationDto> getUserNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(this::mapToNotificationDto).toList();
    }

    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find notification with id " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    private NotificationDto mapToNotificationDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .videoId(notification.getVideoId())
                .videoThumbnail(notification.getVideoThumbnail())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
