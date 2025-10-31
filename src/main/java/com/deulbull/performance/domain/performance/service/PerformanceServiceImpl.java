package com.deulbull.performance.domain.performance.service;

import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.entity.PerformanceImage;
import com.deulbull.performance.domain.performance.exception.PerformanceNotFoundException;
import com.deulbull.performance.domain.performance.repository.PerformanceImageRepository;
import com.deulbull.performance.domain.performance.repository.PerformanceMoreLinkRepository;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import com.deulbull.performance.domain.performance.web.dto.PerformanceDetailResponseDto;
import com.deulbull.performance.domain.song.exception.SongNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final PerformanceImageRepository performanceImageRepository;
    private final PerformanceMoreLinkRepository performanceMoreLinkRepository;

    @Override
    public PerformanceDetailResponseDto getPerformanceDetail(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new); // 404: 존재하지 않는 공연

        // 공연 이미지 리스트 생성
        List<String> imageUrls = performanceImageRepository.findAllByPerformanceId(performance.getId())
                .stream()
                .map(PerformanceImage::getImageUrl)
                .toList();

        // 포스터 이미지 리스트 생성
        List<String> posterUrls = new ArrayList<>();
        posterUrls.add(performance.getPosterFrontUrl());
        posterUrls.add(performance.getPosterBackUrl());

        // 현재 곡
        String currentSongTitle = null;
        String currentSongArtist = null;
        String currentSongAlbumUrl = null;

        if (performance.getCurrentSong() != null) {
            // 404: 해당 ID의 곡 없음
            if (performance.getCurrentSong().getSong() == null) {
                throw new SongNotFoundException();
            }
            currentSongTitle = performance.getCurrentSong().getSong().getTitle();
            currentSongArtist = performance.getCurrentSong().getSong().getArtist();
            currentSongAlbumUrl = performance.getCurrentSong().getSong().getAlbumImgUrl();
        }

        // morelink 리스트 생성
        List<PerformanceDetailResponseDto.MoreLinkDto> moreLinks = performanceMoreLinkRepository.findAllByPerformanceId(performance.getId())
                .stream()
                .map(p -> new PerformanceDetailResponseDto.MoreLinkDto(
                        p.getType().toString(),
                        p.getName(),
                        p.getUrl()
                ))
                .toList();

        // 반환
        return new PerformanceDetailResponseDto(
                performance.getId(),
                performance.getWebsiteName(),
                performance.getWebsiteDescription(),
                imageUrls,
                performance.getTitle(),
                performance.getSubtitle(),
                performance.getDescription(),
                performance.formatDateTimeWithDay(performance.getDateTime()),
                performance.getVenue(),
                performance.getOpenchatUrl(),
                posterUrls,
                currentSongTitle,
                currentSongArtist,
                currentSongAlbumUrl,
                performance.getLocation(),
                moreLinks
        );
    }
}
