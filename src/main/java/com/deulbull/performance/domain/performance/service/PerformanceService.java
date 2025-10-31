package com.deulbull.performance.domain.performance.service;

import com.deulbull.performance.domain.performance.web.dto.PerformanceDetailResponseDto;

public interface PerformanceService {
    // 공연 정보 상세 조회
    PerformanceDetailResponseDto getPerformanceDetail(Long performanceId);
}
