package com.mostafa.youtubeclone.controller;

import com.mostafa.youtubeclone.dto.ReportDto;
import com.mostafa.youtubeclone.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportDto submitReport(@RequestBody ReportDto reportDto) {
        return reportService.submitReport(reportDto);
    }
}
