package com.deulbull.performance.domain.performanceSongs.service;

import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsDetailResponseDto;

public interface PerformanceSongsService {
    // 곡 정보 상세 조회
    PerformanceSongsDetailResponseDto getPerformanceSongsDetail(Long performanceSongId);
}
