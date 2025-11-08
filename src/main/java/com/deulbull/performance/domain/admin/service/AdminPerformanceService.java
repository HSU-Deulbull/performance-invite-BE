package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.web.dto.AdminCurrentSongResponseDto;

public interface AdminPerformanceService {
    // 현재 곡 조회
    AdminCurrentSongResponseDto getCurrentSong(Long adminId);

    // 다음 곡 전달
    AdminCurrentSongResponseDto getNextSong(Long adminId);

    // 이전 곡 전달
    AdminCurrentSongResponseDto getPreviousSong(Long adminId);
}
