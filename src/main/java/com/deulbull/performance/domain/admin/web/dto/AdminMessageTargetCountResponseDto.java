package com.deulbull.performance.domain.admin.web.dto;

public record AdminMessageTargetCountResponseDto(
        int smsTargetCount,
        int totalBookingCount
) {
}
