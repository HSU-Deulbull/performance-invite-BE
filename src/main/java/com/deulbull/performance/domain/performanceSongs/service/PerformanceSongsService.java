package com.deulbull.performance.domain.performanceSongs.service;

import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsDetailResponseDto;
import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsLikeRequestDto;
import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsLikeResponseDto;

public interface PerformanceSongsService {
    // 곡 정보 상세 조회
    PerformanceSongsDetailResponseDto getPerformanceSongsDetail(Long performanceSongId);
    // 곡 좋아요 추가/취소
    PerformanceSongsLikeResponseDto getPerformanceSongsLike(Long performanceSongId, PerformanceSongsLikeRequestDto liked);
}
