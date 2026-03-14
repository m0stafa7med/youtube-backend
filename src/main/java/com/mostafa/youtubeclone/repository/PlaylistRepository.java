package com.mostafa.youtubeclone.repository;

import com.mostafa.youtubeclone.model.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlaylistRepository extends MongoRepository<Playlist, String> {
    List<Playlist> findByUserId(String userId);
}
