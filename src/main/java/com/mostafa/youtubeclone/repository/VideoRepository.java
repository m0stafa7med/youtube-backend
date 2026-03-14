package com.mostafa.youtubeclone.repository;

import com.mostafa.youtubeclone.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VideoRepository extends MongoRepository<Video, String> {
    List<Video> findByUserId(String userId);
    long countByUserId(String userId);
    Page<Video> findAll(Pageable pageable);
    Page<Video> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
    List<Video> findByCategory(String category);
    Page<Video> findByCategory(String category, Pageable pageable);
    Page<Video> findByTagsContaining(String tag, Pageable pageable);
}
