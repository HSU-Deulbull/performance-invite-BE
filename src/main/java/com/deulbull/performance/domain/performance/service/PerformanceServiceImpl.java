package com.deulbull.performance.domain.performance.service;

import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.entity.PerformanceImage;
import com.deulbull.performance.domain.performance.entity.PerformanceMoreLink;
import com.deulbull.performance.domain.performance.exception.PerformanceNotFoundException;
import com.deulbull.performance.domain.performance.repository.PerformanceImageRepository;
import com.deulbull.performance.domain.performance.repository.PerformanceMoreLinkRepository;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import com.deulbull.performance.domain.performance.web.dto.PerformanceCreateRequestDto;
import com.deulbull.performance.domain.performance.web.dto.PerformanceDetailResponseDto;
import com.deulbull.performance.domain.performance.web.dto.PerformanceDetailResponseDto.MoreLinkDto;
import com.deulbull.performance.domain.performance.web.dto.PerformanceSetlistResponse;
import com.deulbull.performance.domain.performance.web.dto.PerformanceSetlistResponse.PerformanceSetListDetail;
import com.deulbull.performance.domain.performanceSongs.entity.PerformanceSong;
import com.deulbull.performance.domain.performanceSongs.repository.PerformanceSongsRepository;
import com.deulbull.performance.domain.song.entity.Song;
import com.deulbull.performance.domain.song.exception.SongNotFoundException;
import com.deulbull.performance.domain.song.repository.SongRepository;
import com.deulbull.performance.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceImageRepository performanceImageRepository;
    private final PerformanceMoreLinkRepository performanceMoreLinkRepository;
    private final PerformanceSongsRepository performanceSongsRepository;
    private final SongRepository songRepository;
    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public PerformanceDetailResponseDto createPerformance(
            PerformanceCreateRequestDto requestDto,
            MultipartFile posterFront,
            MultipartFile posterBack,
            List<MultipartFile> images) {

        // 1. Performance 엔티티 생성 및 저장
        Performance performance = Performance.builder()
                .websiteName(requestDto.websiteName())
                .websiteDescription(requestDto.websiteDescription())
                .title(requestDto.title())
                .subtitle(requestDto.subtitle())
                .description(requestDto.description())
                .location(requestDto.location())
                .venue(requestDto.venue())
                .dateTime(requestDto.dateTime())
                .preSaleFee(requestDto.preSaleFee())
                .onSiteFee(requestDto.onSiteFee())
                .preSaleEndTime(requestDto.preSaleEndTime())
                .posterFrontUrl(null)
                .posterBackUrl(null)
                .openchatUrl(requestDto.openchatUrl())
                .currentSong(null) // 초기에는 현재 곡 없음
                .build();

        performanceRepository.save(performance);

        Long performanceId = performance.getId();

        // 2. S3에 포스터 업로드
        String posterFrontUrl = null;
        String posterBackUrl = null;

        if (posterFront != null && !posterFront.isEmpty()) {
            posterFrontUrl = s3Uploader.upload(
                    posterFront,
                    "performance/" + performanceId + "/poster-front"
            );
        }
        if (posterBack != null && !posterBack.isEmpty()) {
            posterBackUrl = s3Uploader.upload(
                    posterBack,
                    "performance/" + performanceId + "/poster-back"
            );
        }

        // 3. 공연 이미지 S3 업로드 + PerformanceImage 저장
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            int index = 1;
            for (MultipartFile image : images) {
                if (image == null || image.isEmpty()) continue;

                String url = s3Uploader.upload(
                        image,
                        "performance/" + performanceId + "/image" + index++
                );
                imageUrls.add(url);
            }

            if (!imageUrls.isEmpty()) {
                List<PerformanceImage> performanceImages = imageUrls.stream()
                        .map(url -> PerformanceImage.builder()
                                .imageUrl(url)
                                .performance(performance)
                                .build())
                        .toList();
                performanceImageRepository.saveAll(performanceImages);
            }
        }

        // 4. PerformanceMoreLink 생성 및 저장
        if (requestDto.moreLinks() != null && !requestDto.moreLinks().isEmpty()) {
            List<PerformanceMoreLink> moreLinks = requestDto.moreLinks().stream()
                    .map(dto -> PerformanceMoreLink.builder()
                            .name(dto.name())
                            .type(dto.type())
                            .url(dto.url())
                            .performance(performance)
                            .build())
                    .toList();
            performanceMoreLinkRepository.saveAll(moreLinks);
        }

        // 5. Song 및 PerformanceSong 생성
        if (requestDto.setlist() != null && !requestDto.setlist().isEmpty()) {
            List<PerformanceSong> performanceSongs = requestDto.setlist().stream()
                    .map(psDto -> {
                        // 5-1. 곡 중복 체크 (title + artist)
                        Song song = songRepository.findByTitleAndArtist(
                                        psDto.song().title(),
                                        psDto.song().artist()
                                )
                                .orElseGet(() -> {
                                    // 5-2. 곡이 없으면 새로 생성
                                    Song newSong = Song.builder()
                                            .title(psDto.song().title())
                                            .artist(psDto.song().artist())
                                            .album(psDto.song().album())
                                            .releaseDate(psDto.song().releaseDate())
                                            .genre(psDto.song().genre())
                                            .youtubeUrl(psDto.song().youtubeUrl())
                                            .albumImgUrl(psDto.song().albumImgUrl())
                                            .lyrics(psDto.song().lyrics())
                                            .build();
                                    return songRepository.save(newSong);
                                });

                        // 5-3. PerformanceSong 생성
                        return PerformanceSong.builder()
                                .orderInPerformance(psDto.orderInPerformance())
                                .likes(0) // 초기 좋아요 수 0
                                .performance(performance)
                                .song(song)
                                .build();
                    })
                    .toList();

            performanceSongsRepository.saveAll(performanceSongs);
        }
        // 6. Performance 엔티티에 포스터 URL 반영
        performance.setPosterFrontUrl(posterFrontUrl);
        performance.setPosterBackUrl(posterBackUrl);

        // 7. 생성된 공연 상세 정보 반환
        return getPerformanceDetail(performance.getId());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "performanceDetail", key = "#performanceId")
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
        List<MoreLinkDto> moreLinks = performanceMoreLinkRepository.findAllByPerformanceId(performance.getId())
                .stream()
                .map(p -> new MoreLinkDto(
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

    // 공연 셋리스트 조회
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "performanceSetlist", key = "#performanceId")
    public PerformanceSetlistResponse getPerformanceSetlist(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new); // 404: 존재하지 않는 공연

        List<PerformanceSong> performanceSongs = performanceSongsRepository.findByPerformanceId(performance.getId());

        // performanceSongs를 PerformanceSetListDetail 리스트로 변환 및 정렬
        List<PerformanceSetListDetail> setList = performanceSongs.stream()
                .map(p -> new PerformanceSetListDetail(
                        p.getOrderInPerformance(),
                        p.getId(),
                        p.getSong().getTitle(),
                        p.getSong().getArtist()
                ))
                .sorted(Comparator.comparingInt(PerformanceSetListDetail::order)) // order 기준 정렬
                .toList();

        // currentSong이 null일 수 있는 경우 처리
        int currentSongId = performance.getCurrentSong() != null
                ? performance.getCurrentSong().getOrderInPerformance()
                : -1;

        return new PerformanceSetlistResponse(currentSongId, setList);
    }
}
