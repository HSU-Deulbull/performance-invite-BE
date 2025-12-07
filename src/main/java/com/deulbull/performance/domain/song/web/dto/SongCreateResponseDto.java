package com.deulbull.performance.domain.song.web.dto;

import java.util.List;

public record SongCreateResponseDto(
        int totalCreated,
        List<SongDetail> songs
) {
    public record SongDetail(
            Long songId,
            String artist,
            String title,
            String album,
            String releaseDate,
            String genre,
            String youtubeUrl,
            String albumImgUrl,
            String lyrics
    ) {
    }
}
