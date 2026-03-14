package com.mostafa.youtubeclone.repository;

import com.mostafa.youtubeclone.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report, String> {
}
