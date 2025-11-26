package com.deulbull.performance.domain.performance.web.dto;
import java.io.Serializable;
import java.util.List;

public record PerformanceSetlistResponse(
        int nowPlayingOrder,
        List<PerformanceSetListDetail> setlist
) implements Serializable {
    public record PerformanceSetListDetail(
        int order,
        Long performanceSongId,
        String title,
        String artist
    ) implements Serializable {
    }
}
