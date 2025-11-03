package com.deulbull.performance.domain.performanceSongs.web.controller;

import com.deulbull.performance.domain.performanceSongs.service.PerformanceSongsService;
import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class PerformanceSongsController {

    private final PerformanceSongsService performanceSongsService;
    @GetMapping("/{performanceSongId}")
    public PerformanceSongsDetailResponseDto getPerformanceSongDetails(@PathVariable Long performanceSongId) {
        return performanceSongsService.getPerformanceSongsDetail(performanceSongId);
    }
}
