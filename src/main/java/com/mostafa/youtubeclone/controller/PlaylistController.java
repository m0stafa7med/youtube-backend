package com.mostafa.youtubeclone.controller;

import com.mostafa.youtubeclone.dto.PlaylistDto;
import com.mostafa.youtubeclone.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistDto createPlaylist(@RequestBody PlaylistDto playlistDto) {
        return playlistService.createPlaylist(playlistDto);
    }

    @GetMapping("/{playlistId}")
    @ResponseStatus(HttpStatus.OK)
    public PlaylistDto getPlaylist(@PathVariable String playlistId) {
        return playlistService.getPlaylist(playlistId);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public List<PlaylistDto> getMyPlaylists() {
        return playlistService.getMyPlaylists();
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PlaylistDto> getUserPlaylists(@PathVariable String userId) {
        return playlistService.getUserPlaylists(userId);
    }

    @PostMapping("/{playlistId}/videos/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public void addVideoToPlaylist(@PathVariable String playlistId, @PathVariable String videoId) {
        playlistService.addVideoToPlaylist(playlistId, videoId);
    }

    @DeleteMapping("/{playlistId}/videos/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeVideoFromPlaylist(@PathVariable String playlistId, @PathVariable String videoId) {
        playlistService.removeVideoFromPlaylist(playlistId, videoId);
    }

    @DeleteMapping("/{playlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlaylist(@PathVariable String playlistId) {
        playlistService.deletePlaylist(playlistId);
    }
}
