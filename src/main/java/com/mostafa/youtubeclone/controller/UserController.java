package com.mostafa.youtubeclone.controller;


import com.mostafa.youtubeclone.dto.UserDto;
import com.mostafa.youtubeclone.dto.VideoDto;
import com.mostafa.youtubeclone.service.UserRegistrationService;
import com.mostafa.youtubeclone.service.UserService;
import com.mostafa.youtubeclone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRegistrationService userRegistrationService;
    private final UserService userService;
    private final VideoService videoService;

    @GetMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String register(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return userRegistrationService.registerUser(jwt.getTokenValue());
    }

    @GetMapping("subscribe/{userId}/status")
    @ResponseStatus(HttpStatus.OK)
    public boolean isSubscribed(@PathVariable String userId) {
        return userService.isSubscribed(userId);
    }

    @PostMapping("subscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean subscribeUser(@PathVariable String userId) {
        userService.subscribeUser(userId);
        return true;
    }

    @PostMapping("unSubscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean unSubscribeUser(@PathVariable String userId) {
        userService.unSubscribeUser(userId);
        return true;
    }

    @GetMapping("/{userId}/history")
    @ResponseStatus(HttpStatus.OK)
    public Set<String> userHistory(@PathVariable String userId) {
        return userService.userHistory(userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserProfile(@PathVariable String userId) {
        return userService.getUserProfile(userId);
    }

    @GetMapping("/{userId}/videos")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDto> getUserVideos(@PathVariable String userId) {
        return videoService.getUserVideos(userId);
    }

}
