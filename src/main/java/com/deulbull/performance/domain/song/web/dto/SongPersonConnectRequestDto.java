package com.deulbull.performance.domain.song.web.dto;

import com.deulbull.performance.domain.band.entity.enums.SessionType;
import jakarta.validation.constraints.NotNull;

public record SongPersonConnectRequestDto(
        @NotNull(message = "곡 ID는 필수입니다.")
        Long songId,

        @NotNull(message = "공연 ID는 필수입니다.")
        Long performanceId,

        @NotNull(message = "사람 ID는 필수입니다.")
        Long personId,

        @NotNull(message = "세션 타입은 필수입니다.")
        SessionType sessionType
) {
}
