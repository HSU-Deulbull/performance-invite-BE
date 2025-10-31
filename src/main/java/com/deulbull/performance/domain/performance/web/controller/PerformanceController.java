package com.deulbull.performance.domain.performance.web.controller;

import com.deulbull.performance.domain.performance.service.PerformanceService;
import com.deulbull.performance.domain.performance.web.dto.PerformanceDetailResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    // 공연 상세 조회 API
    @GetMapping("/{performanceId}")
    public SuccessResponse<PerformanceDetailResponseDto> getPerformanceDetail(
            @PathVariable Long performanceId
    ) {
        PerformanceDetailResponseDto responseDto = performanceService.getPerformanceDetail(performanceId);
        return SuccessResponse.ok(responseDto);
    }
}
