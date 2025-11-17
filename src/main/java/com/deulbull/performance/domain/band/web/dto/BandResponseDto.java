package com.deulbull.performance.domain.band.web.dto;

import com.deulbull.performance.domain.band.entity.Band;
import lombok.Builder;

@Builder
public record BandResponseDto(
        Long id,
        String bandName,
        String logoUrl,
        String createdAt,
        String updatedAt
) {
    public static BandResponseDto from(Band band) {
        return BandResponseDto.builder()
                .id(band.getId())
                .bandName(band.getBandName())
                .logoUrl(band.getLogoUrl())
                .createdAt(band.formatDateTimeWithDay(band.getCreatedAt()))
                .updatedAt(band.formatDateTimeWithDay(band.getUpdatedAt()))
                .build();
    }
}
