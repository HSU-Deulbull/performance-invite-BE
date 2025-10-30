package com.deulbull.performance.domain.performance.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDetailRequestDto {
    private Long performanceId; // 공연 ID
}
