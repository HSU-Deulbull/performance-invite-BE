package com.deulbull.performance.domain.performanceSongs.web.controller;

import com.deulbull.performance.domain.performanceSongs.service.PerformanceSongsService;
import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsDetailResponseDto;
import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsLikeRequestDto;
import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsLikeResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class PerformanceSongsController {

    private final PerformanceSongsService performanceSongsService;

    @GetMapping("/{performanceSongId}")
    public SuccessResponse<PerformanceSongsDetailResponseDto> getPerformanceSongDetails(@PathVariable Long performanceSongId) {
        return SuccessResponse.ok(performanceSongsService.getPerformanceSongsDetail(performanceSongId));
    }

    @PostMapping("/{performanceSongId}/like")
    public SuccessResponse<PerformanceSongsLikeResponseDto> getlike(@PathVariable Long performanceSongId, @Valid @RequestBody PerformanceSongsLikeRequestDto liked) {
        return SuccessResponse.ok(performanceSongsService.getPerformanceSongsLike(performanceSongId, liked));
    }
}
