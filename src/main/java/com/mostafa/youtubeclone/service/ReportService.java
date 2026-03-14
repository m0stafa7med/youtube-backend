package com.mostafa.youtubeclone.service;

import com.mostafa.youtubeclone.dto.ReportDto;
import com.mostafa.youtubeclone.model.Report;
import com.mostafa.youtubeclone.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;

    public ReportDto submitReport(ReportDto reportDto) {
        Report report = new Report();
        report.setReporterId(userService.getCurrentUser().getId());
        report.setTargetId(reportDto.getTargetId());
        report.setTargetType(reportDto.getTargetType());
        report.setReason(reportDto.getReason());
        report.setStatus("PENDING");
        report.setCreatedAt(LocalDateTime.now());
        Report savedReport = reportRepository.save(report);

        return ReportDto.builder()
                .id(savedReport.getId())
                .targetId(savedReport.getTargetId())
                .targetType(savedReport.getTargetType())
                .reason(savedReport.getReason())
                .createdAt(savedReport.getCreatedAt())
                .build();
    }
}
