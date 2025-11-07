package com.deulbull.performance.domain.admin.web.dto;

public record AdminCurrentSongResponseDto(
        String title,
        String artist,
        String albumImgUrl
) {
}
