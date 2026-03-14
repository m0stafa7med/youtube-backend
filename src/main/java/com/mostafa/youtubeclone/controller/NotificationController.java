package com.mostafa.youtubeclone.controller;

import com.mostafa.youtubeclone.dto.NotificationDto;
import com.mostafa.youtubeclone.service.NotificationService;
import com.mostafa.youtubeclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDto> getUserNotifications() {
        String userId = userService.getCurrentUser().getId();
        return notificationService.getUserNotifications(userId);
    }

    @GetMapping("/unread-count")
    @ResponseStatus(HttpStatus.OK)
    public long getUnreadCount() {
        String userId = userService.getCurrentUser().getId();
        return notificationService.getUnreadCount(userId);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    public void markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
    }

    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.OK)
    public void markAllAsRead() {
        String userId = userService.getCurrentUser().getId();
        notificationService.markAllAsRead(userId);
    }
}
