package com.deulbull.performance.domain.performance.web.dto;

import com.deulbull.performance.domain.performance.entity.enums.LinkType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PerformanceCreateRequestDto(
        // Performance 기본 정보
        @NotBlank(message = "웹사이트 이름은 필수 입력 항목입니다.")
        String websiteName,

        String websiteDescription,

        @NotBlank(message = "공연 제목은 필수 입력 항목입니다.")
        String title,

        String subtitle,

        String description,

        @NotBlank(message = "주소는 필수 입력 항목입니다.")
        String location,

        @NotBlank(message = "공연장 이름은 필수 입력 항목입니다.")
        String venue,

        @NotNull(message = "공연 날짜와 시간은 필수 입력 항목입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dateTime,

        Integer preSaleFee,

        Integer onSiteFee,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime preSaleEndTime,

        String openchatUrl,

        String bankName,

        String bankAccount,

        String accountHolder,

        String kakaopayUrl,

        String naverpayUrl,

        // PerformanceMoreLink 리스트
        @Valid
        List<MoreLinkCreateDto> moreLinks,

        // PerformanceSong 리스트 (곡 정보 포함)
        @Valid
        @NotNull(message = "셋리스트는 필수 입력 항목입니다.")
        List<PerformanceSongCreateDto> setlist
) {
    // 추가 링크 DTO
    public record MoreLinkCreateDto(
            @NotBlank(message = "링크 이름은 필수 입력 항목입니다.")
            String name,

            @NotNull(message = "링크 타입은 필수 입력 항목입니다.")
            LinkType type,

            @NotBlank(message = "링크 URL은 필수 입력 항목입니다.")
            String url
    ) {}

    // 공연별 곡 정보 DTO
    public record PerformanceSongCreateDto(
            @NotNull(message = "곡 순서는 필수 입력 항목입니다.")
            Integer orderInPerformance,

            @Valid
            @NotNull(message = "곡 정보는 필수 입력 항목입니다.")
            SongCreateDto song,

            MembersDto members
    ) {}

    // 곡별 멤버 정보 DTO
    public record MembersDto(
            List<String> vocal,
            List<String> guitar1,
            List<String> guitar2,
            List<String> bass,
            List<String> drum,
            List<String> keyboard
    ) {}

    // 곡 정보 DTO
    public record SongCreateDto(
            @NotBlank(message = "곡 제목은 필수 입력 항목입니다.")
            String title,

            @NotBlank(message = "아티스트는 필수 입력 항목입니다.")
            String artist,

            String album,

            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate releaseDate,

            String genre,

            String youtubeUrl,

            String albumImgUrl,

            String lyrics
    ) {}
}
