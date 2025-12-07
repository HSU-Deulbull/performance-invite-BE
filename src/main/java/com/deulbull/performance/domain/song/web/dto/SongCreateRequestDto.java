package com.deulbull.performance.domain.song.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SongCreateRequestDto(
        @NotNull(message = "곡 목록은 필수입니다.")
        @Valid
        List<SongInfo> songs
) {
    public record SongInfo(
            @NotBlank(message = "아티스트는 필수입니다.")
            String artist,

            @NotBlank(message = "제목은 필수입니다.")
            String title,

            String album,
            String releaseDate, // "YYYY-MM-DD" 형식
            String genre,
            String youtubeUrl,
            String lyrics
    ) {
    }
}
