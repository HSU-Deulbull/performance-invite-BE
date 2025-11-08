package com.deulbull.performance.domain.performance.service;

import com.deulbull.performance.domain.performance.web.dto.PerformanceDetailResponseDto;
import com.deulbull.performance.domain.performance.web.dto.PerformanceSetlistResponse;

public interface PerformanceService {
    // 공연 정보 상세 조회
    PerformanceDetailResponseDto getPerformanceDetail(Long performanceId);

    // 공연 셋리스트 조회
    PerformanceSetlistResponse getPerformanceSetlist(Long performanceId);
}
