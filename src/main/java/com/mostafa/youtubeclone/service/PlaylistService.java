package com.mostafa.youtubeclone.service;

import com.mostafa.youtubeclone.dto.PlaylistDto;
import com.mostafa.youtubeclone.model.Playlist;
import com.mostafa.youtubeclone.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserService userService;

    public PlaylistDto createPlaylist(PlaylistDto playlistDto) {
        Playlist playlist = Playlist.builder()
                .name(playlistDto.getName())
                .description(playlistDto.getDescription())
                .userId(userService.getCurrentUser().getId())
                .visibility(playlistDto.getVisibility())
                .createdAt(LocalDateTime.now())
                .build();
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return mapToPlaylistDto(savedPlaylist);
    }

    public PlaylistDto getPlaylist(String playlistId) {
        Playlist playlist = getPlaylistById(playlistId);
        return mapToPlaylistDto(playlist);
    }

    public List<PlaylistDto> getUserPlaylists(String userId) {
        List<Playlist> playlists = playlistRepository.findByUserId(userId);
        return playlists.stream().map(this::mapToPlaylistDto).toList();
    }

    public void addVideoToPlaylist(String playlistId, String videoId) {
        Playlist playlist = getPlaylistById(playlistId);
        playlist.addVideo(videoId);
        playlistRepository.save(playlist);
    }

    public void removeVideoFromPlaylist(String playlistId, String videoId) {
        Playlist playlist = getPlaylistById(playlistId);
        playlist.removeVideo(videoId);
        playlistRepository.save(playlist);
    }

    public void deletePlaylist(String playlistId) {
        playlistRepository.deleteById(playlistId);
    }

    private Playlist getPlaylistById(String playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find playlist with id " + playlistId));
    }

    private PlaylistDto mapToPlaylistDto(Playlist playlist) {
        return PlaylistDto.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .userId(playlist.getUserId())
                .videoIds(playlist.getVideoIds())
                .visibility(playlist.getVisibility())
                .createdAt(playlist.getCreatedAt())
                .build();
    }
}
