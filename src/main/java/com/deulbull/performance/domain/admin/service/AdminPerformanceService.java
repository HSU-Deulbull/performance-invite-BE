package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.web.dto.AdminCurrentSongResponseDto;

public interface AdminPerformanceService {
    AdminCurrentSongResponseDto getCurrentSong(Long adminId);
}
