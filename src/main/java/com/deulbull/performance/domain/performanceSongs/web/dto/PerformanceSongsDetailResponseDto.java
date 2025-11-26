package com.deulbull.performance.domain.performanceSongs.web.dto;

import com.deulbull.performance.domain.band.entity.enums.SessionType;

import java.io.Serializable;
import java.util.List;

public record PerformanceSongsDetailResponseDto (
        Track track,
        List<Team> team
) implements Serializable {
    public record Track(
            int likes,
            String title,
            String artist,
            String album,
            String genre,
            String releaseDate,
            String youtubeUrl,
            String lyrics,
            String albumImgUrl
    ) implements Serializable {}

    public record Team(
            SessionType session,
            String name,
            String instagramId
    ) implements Serializable {}
}
