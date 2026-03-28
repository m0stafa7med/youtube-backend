package com.mostafa.youtubeclone.controller;

import com.mostafa.youtubeclone.dto.CommentDto;
import com.mostafa.youtubeclone.dto.UploadVideoResponseDto;
import com.mostafa.youtubeclone.dto.VideoDto;
import com.mostafa.youtubeclone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponseDto uploadVideo(@RequestParam("file") MultipartFile file) {
        return videoService.uploadVideo(file);
    }

    @PostMapping("/thumbnail")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestParam("videoId") String videoId) {
        return videoService.uploadThumbnail(file, videoId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public VideoDto editVideoMetadata(@RequestBody VideoDto videoDto) {
        return videoService.editVideo(videoDto);
    }

    @GetMapping("/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto getVideoDetails(@PathVariable String videoId) {
        return videoService.getVideoDetails(videoId);
    }

    @PostMapping("/{videoId}/like")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto likeVideo(@PathVariable String videoId) {
        return videoService.likeVideo(videoId);
    }

    @PostMapping("/{videoId}/disLike")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto disLikeVideo(@PathVariable String videoId) {
        return videoService.disLikeVideo(videoId);
    }

    @PostMapping("/{videoId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public void addComment(@PathVariable String videoId, @RequestBody CommentDto commentDto) {
        videoService.addComment(videoId, commentDto);
    }

    @GetMapping("/{videoId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllComments(@PathVariable String videoId) {
        return videoService.getAllComments(videoId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<VideoDto> getAllVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(required = false) String category) {
        return videoService.getAllVideos(page, size, sort, category);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<VideoDto> searchVideos(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return videoService.searchVideos(query, page, size);
    }

    @GetMapping("/trending")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDto> getTrendingVideos() {
        return videoService.getTrendingVideos();
    }

    @GetMapping("/history")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDto> getHistory() {
        return videoService.getHistory();
    }

    @DeleteMapping("/{videoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideo(@PathVariable String videoId) {
        videoService.deleteVideo(videoId);
    }

    @DeleteMapping("/{videoId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable String videoId, @PathVariable String commentId) {
        videoService.deleteComment(videoId, commentId);
    }

    @PutMapping("/{videoId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void editComment(@PathVariable String videoId, @PathVariable String commentId, @RequestBody CommentDto commentDto) {
        videoService.editComment(videoId, commentId, commentDto);
    }

    @PostMapping("/{videoId}/comment/{commentId}/reply")
    @ResponseStatus(HttpStatus.OK)
    public void addReply(@PathVariable String videoId, @PathVariable String commentId, @RequestBody CommentDto commentDto) {
        videoService.addReply(videoId, commentId, commentDto);
    }

}
