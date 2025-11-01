package com.deulbull.performance.domain.booking.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record BookingRequestDto(
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        String name,

        @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
        String phoneNumber,

        @NotNull(message = "인원수는 필수 입력 항목입니다.")
        @Positive(message = "인원수는 1명 이상이어야 합니다.")
        Integer headCount
) {
}
