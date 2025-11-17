package com.deulbull.performance.domain.band.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonRequestDto(
        String studentId,

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        String name,

        String instagramId
) {
}
