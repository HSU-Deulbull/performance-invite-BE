package com.deulbull.performance.domain.performance.web.dto;
import java.util.List;

public record PerformanceSetlistResponse(
        int nowPlayingOrder,
        String setListUrl,
        List<PerformanceSetListDetail> setlist
) {
    public record PerformanceSetListDetail(
        int order,
        Long performanceSongId,
        String title,
        String artist
    ){
    }
}
