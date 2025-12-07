package com.deulbull.performance.domain.song.web.dto;

import com.deulbull.performance.domain.band.entity.enums.SessionType;
import lombok.Builder;

@Builder
public record SongPersonConnectResponseDto(
        Long bandSessionId,
        Long performanceSongId,
        Long songId,
        String songTitle,
        String songArtist,
        Long personId,
        String personName,
        SessionType sessionType,
        Long performanceId,
        String performanceTitle
) {
}
