package com.deulbull.performance.domain.performance.web.dto;
import java.util.List;

public record PerformanceSetlistResponse(
        int nowPlayingOrder,
        List<PerformanceSetListDetail> performanceSetListDetails
) {
    public record PerformanceSetListDetail(
        int order,
        int performanceSongId,
        String title,
        String artist
    ){
    }
}
