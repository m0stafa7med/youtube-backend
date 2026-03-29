package com.mostafa.youtubeclone.service;

import com.mostafa.youtubeclone.dto.CommentDto;
import com.mostafa.youtubeclone.dto.UploadVideoResponseDto;
import com.mostafa.youtubeclone.dto.VideoDto;
import com.mostafa.youtubeclone.model.Comment;
import com.mostafa.youtubeclone.model.User;
import com.mostafa.youtubeclone.model.Video;
import com.mostafa.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class VideoService {

    private final FileStorageService fileStorageService;
    private final VideoRepository videoRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Value("${upload.max-videos-per-user:50}")
    private int maxVideosPerUser;

    public UploadVideoResponseDto uploadVideo(MultipartFile multipartFile) {
        fileStorageService.validateVideoFile(multipartFile);

        User currentUser = userService.getCurrentUser();
        long userVideoCount = videoRepository.countByUserId(currentUser.getId());
        if (userVideoCount >= maxVideosPerUser) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Upload limit reached. Maximum " + maxVideosPerUser + " videos per user.");
        }

        String videoUrl = fileStorageService.uploadVideo(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);
        video.setCreatedAt(LocalDateTime.now());
        video.setUserId(currentUser.getId());

        var savedVideo = videoRepository.save(video);
        return new UploadVideoResponseDto(savedVideo.getId(), savedVideo.getVideoUrl());
    }

    public VideoDto editVideo(VideoDto videoDto) {
        var savedVideo = getVideoById(videoDto.getId());

        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());
        savedVideo.setCategory(videoDto.getCategory());
        savedVideo.setUpdatedAt(LocalDateTime.now());

        videoRepository.save(savedVideo);

        // Notify subscribers when video metadata is first set (title exists means it's being published)
        if (videoDto.getTitle() != null && !videoDto.getTitle().isEmpty()) {
            notificationService.notifySubscribers(
                    savedVideo.getUserId(),
                    savedVideo.getId(),
                    videoDto.getTitle(),
                    videoDto.getThumbnailUrl()
            );
        }

        return mapToVideoDto(savedVideo);
    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        fileStorageService.validateThumbnailFile(file);

        var savedVideo = getVideoById(videoId);

        // Delete old thumbnail if replacing
        if (savedVideo.getThumbnailUrl() != null) {
            fileStorageService.deleteFile(savedVideo.getThumbnailUrl());
        }

        String thumbnailUrl = fileStorageService.uploadThumbnail(file);
        savedVideo.setThumbnailUrl(thumbnailUrl);

        videoRepository.save(savedVideo);
        return thumbnailUrl;
    }

    Video getVideoById(String videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by id - " + videoId));
    }

    public VideoDto getVideoDetails(String videoId) {
        Video savedVideo = getVideoById(videoId);

        increaseVideoCount(savedVideo);
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Jwt) {
            try {
                userService.addVideoToHistory(videoId);
            } catch (IllegalArgumentException e) {
                // User authenticated but not registered yet — skip history tracking
            }
        }

        return mapToVideoDto(savedVideo);
    }

    private void increaseVideoCount(Video savedVideo) {
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    public VideoDto likeVideo(String videoId) {
        Video videoById = getVideoById(videoId);

        if (userService.ifLikedVideo(videoId)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        } else if (userService.ifDisLikedVideo(videoId)) {
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        } else {
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    public VideoDto disLikeVideo(String videoId) {
        Video videoById = getVideoById(videoId);

        if (userService.ifDisLikedVideo(videoId)) {
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(videoId);
        } else if (userService.ifLikedVideo(videoId)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoId);
        } else {
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    private VideoDto mapToVideoDto(Video videoById) {
        String userFullName = null;
        String userPicture = null;

        if (videoById.getUserId() != null) {
            try {
                User videoOwner = userService.getUserById(videoById.getUserId());
                userFullName = videoOwner.getFullName();
                userPicture = videoOwner.getPicture();
            } catch (IllegalArgumentException e) {
                // User not found, leave as null
            }
        }

        return VideoDto.builder()
                .id(videoById.getId())
                .videoUrl(videoById.getVideoUrl())
                .thumbnailUrl(videoById.getThumbnailUrl())
                .title(videoById.getTitle())
                .description(videoById.getDescription())
                .tags(videoById.getTags())
                .videoStatus(videoById.getVideoStatus())
                .likeCount(videoById.getLikes().get())
                .dislikeCount(videoById.getDisLikes().get())
                .viewCount(videoById.getViewCount().get())
                .createdAt(videoById.getCreatedAt())
                .duration(videoById.getDuration())
                .category(videoById.getCategory())
                .userId(videoById.getUserId())
                .userFullName(userFullName)
                .userPicture(userPicture)
                .build();
    }

    public void addComment(String videoId, CommentDto commentDto) {
        Video video = getVideoById(videoId);
        User currentUser = userService.getCurrentUser();
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(currentUser.getId());
        comment.setCreatedAt(LocalDateTime.now());
        video.addComment(comment);

        videoRepository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
        Video video = getVideoById(videoId);
        List<Comment> commentList = video.getComments();

        return commentList.stream().map(this::mapToCommentDto).toList();
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setParentCommentId(comment.getParentCommentId());

        if (comment.getAuthorId() != null) {
            try {
                User author = userService.getUserById(comment.getAuthorId());
                commentDto.setAuthorFullName(author.getFullName());
                commentDto.setAuthorPicture(author.getPicture());
            } catch (IllegalArgumentException e) {
                // Author not found, leave as null
            }
        }

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            commentDto.setReplies(comment.getReplies().stream().map(this::mapToCommentDto).toList());
        }

        return commentDto;
    }

    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll().stream().map(this::mapToVideoDto).toList();
    }

    public Page<VideoDto> getAllVideos(int page, int size, String sort, String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));

        Page<Video> videoPage;
        if (category != null && !category.isEmpty()) {
            videoPage = videoRepository.findByCategory(category, pageable);
        } else {
            videoPage = videoRepository.findAll(pageable);
        }

        return videoPage.map(this::mapToVideoDto);
    }

    public Page<VideoDto> searchVideos(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videoPage = videoRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                query, query, pageable);
        return videoPage.map(this::mapToVideoDto);
    }

    public List<VideoDto> getTrendingVideos() {
        List<Video> allVideos = videoRepository.findAll();
        return allVideos.stream()
                .sorted((v1, v2) -> Integer.compare(v2.getViewCount().get(), v1.getViewCount().get()))
                .limit(20)
                .map(this::mapToVideoDto)
                .toList();
    }

    public void deleteVideo(String videoId) {
        Video video = getVideoById(videoId);
        User currentUser = userService.getCurrentUser();

        if (!video.getUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to delete this video");
        }

        if (video.getVideoUrl() != null) {
            fileStorageService.deleteFile(video.getVideoUrl());
        }
        if (video.getThumbnailUrl() != null) {
            fileStorageService.deleteFile(video.getThumbnailUrl());
        }

        videoRepository.deleteById(videoId);
    }

    public void deleteComment(String videoId, String commentId) {
        Video video = getVideoById(videoId);
        User currentUser = userService.getCurrentUser();

        Comment commentToDelete = video.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find comment with id " + commentId));

        if (!commentToDelete.getAuthorId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to delete this comment");
        }

        video.getComments().removeIf(comment -> comment.getId().equals(commentId));
        videoRepository.save(video);
    }

    public void editComment(String videoId, String commentId, CommentDto commentDto) {
        Video video = getVideoById(videoId);
        User currentUser = userService.getCurrentUser();

        Comment commentToEdit = video.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find comment with id " + commentId));

        if (!commentToEdit.getAuthorId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to edit this comment");
        }

        commentToEdit.setText(commentDto.getCommentText());
        videoRepository.save(video);
    }

    public void addReply(String videoId, String commentId, CommentDto commentDto) {
        Video video = getVideoById(videoId);
        User currentUser = userService.getCurrentUser();

        Comment parentComment = video.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find comment with id " + commentId));

        Comment reply = new Comment();
        reply.setId(UUID.randomUUID().toString());
        reply.setText(commentDto.getCommentText());
        reply.setAuthorId(currentUser.getId());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setParentCommentId(commentId);

        parentComment.addReply(reply);
        videoRepository.save(video);
    }

    public List<VideoDto> getUserVideos(String userId) {
        List<Video> videos = videoRepository.findByUserId(userId);
        return videos.stream().map(this::mapToVideoDto).toList();
    }

    public List<VideoDto> getHistory() {
        User currentUser = userService.getCurrentUser();
        Set<String> videoHistory = currentUser.getVideoHistory();
        if (videoHistory == null || videoHistory.isEmpty()) {
            return List.of();
        }
        List<Video> videos = videoRepository.findAllById(videoHistory);
        return videos.stream().map(this::mapToVideoDto).toList();
    }
}
