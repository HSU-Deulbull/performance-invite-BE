package com.deulbull.performance.domain.band.web.dto;

import jakarta.validation.constraints.NotBlank;

public record BandRequestDto(
        @NotBlank(message = "밴드 이름은 필수 입력 항목입니다.")
        String bandName,

        String logoUrl
) {
}
