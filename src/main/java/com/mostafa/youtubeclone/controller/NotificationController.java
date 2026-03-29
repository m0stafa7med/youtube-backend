package com.mostafa.youtubeclone.controller;

import com.mostafa.youtubeclone.dto.NotificationDto;
import com.mostafa.youtubeclone.model.User;
import com.mostafa.youtubeclone.service.NotificationService;
import com.mostafa.youtubeclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDto> getUserNotifications() {
        Optional<User> user = userService.findCurrentUser();
        if (user.isEmpty()) {
            return List.of();
        }
        return notificationService.getUserNotifications(user.get().getId());
    }

    @GetMapping("/unread-count")
    @ResponseStatus(HttpStatus.OK)
    public long getUnreadCount() {
        Optional<User> user = userService.findCurrentUser();
        return user.map(u -> notificationService.getUnreadCount(u.getId())).orElse(0L);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    public void markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
    }

    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.OK)
    public void markAllAsRead() {
        Optional<User> user = userService.findCurrentUser();
        user.ifPresent(u -> notificationService.markAllAsRead(u.getId()));
    }
}
