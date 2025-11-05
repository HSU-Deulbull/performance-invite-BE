package com.deulbull.performance.domain.admin.web.dto;

public record AdminLoginResponseDto(
        String accessToken,
        String description,
        String bandName,
        String role
) {
}
