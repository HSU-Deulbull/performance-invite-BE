package com.deulbull.performance.domain.performance.web.dto;

import java.util.List;

public record PerformanceDetailResponseDto(
        Long performanceId,
        String websiteName, // 웹사이트 이름
        String websiteDescription, // 웹사이트 소개 문구
        List<String> imageUrls, // 공연 이미지 리스트

        String title,
        String subtitle,
        String description,
        String dateTime,
        String venue, // 공연장 이름
        String openchatUrl,

        List<String> posterUrls, // 포스터 이미지 리스트

        String currentSongTitle,
        String currentSongArtist,

        String location, // 공연장 위치

        List<MoreLinkDto> moreLinks    // 추가 링크 리스트 (type, name, url)
) {
    public record MoreLinkDto(
            String type,
            String name,
            String url
    ) {}
}
