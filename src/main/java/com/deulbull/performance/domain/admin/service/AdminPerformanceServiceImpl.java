package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.exception.AdminNotFoundException;
import com.deulbull.performance.domain.admin.exception.AdminPerformanceNotFoundException;
import com.deulbull.performance.domain.admin.exception.AdminSongNotFoundException;
import com.deulbull.performance.domain.admin.repository.AdminPerformanceRepository;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.domain.admin.web.dto.AdminCurrentSongResponseDto;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import com.deulbull.performance.domain.performanceSongs.entity.PerformanceSong;
import com.deulbull.performance.domain.performanceSongs.repository.PerformanceSongsRepository;
import com.deulbull.performance.domain.song.projection.CurrentSongProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPerformanceServiceImpl implements AdminPerformanceService {

    private final AdminRepository adminRepository;
    private final AdminPerformanceRepository adminPerformanceRepository;
    private final PerformanceSongsRepository performanceSongsRepository;
    private final PerformanceRepository performanceRepository;

    @Override
    public AdminCurrentSongResponseDto getCurrentSong(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(AdminNotFoundException::new);
        CurrentSongProjection currentSong = adminPerformanceRepository.findCurrentSongDtoByAdminId(admin.getId()).orElseThrow(AdminNotFoundException::new);
        return new AdminCurrentSongResponseDto(currentSong.getTitle(), currentSong.getArtist(), currentSong.getAlbumImgUrl());
    }

    @Override
    public AdminCurrentSongResponseDto getNextSong(Long adminId) {
        // Admin / Performance
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(AdminNotFoundException::new);
        Performance performance = admin.getPerformance();
        if (performance == null) throw new AdminPerformanceNotFoundException();

        // 현재 곡 / 공연 ID
        PerformanceSong current = performance.getCurrentSong();
        Long performanceId = performance.getId();

        // 다음 곡(없으면 첫 곡으로 순환)
        PerformanceSong nextEntity;
        if (current != null) {
            Integer currOrder = current.getOrderInPerformance();
            nextEntity = performanceSongsRepository
                    .findFirstByPerformance_IdAndOrderInPerformanceGreaterThanOrderByOrderInPerformanceAsc(
                            performanceId, currOrder
                    )
                    .orElseGet(() -> performanceSongsRepository
                            .findFirstByPerformance_IdOrderByOrderInPerformanceAsc(performanceId)
                            .orElseThrow(AdminSongNotFoundException::new)
                    );
        } else {
            nextEntity = performanceSongsRepository
                    .findFirstByPerformance_IdOrderByOrderInPerformanceAsc(performanceId)
                    .orElseThrow(AdminSongNotFoundException::new);
        }

        // current_song 갱신
        performance.setCurrentSong(nextEntity);
        performanceRepository.save(performance);

        // 프로젝션으로 응답 만들기
        CurrentSongProjection nextView = performanceSongsRepository
                .findProjectionById(nextEntity.getId())
                .orElseThrow(AdminSongNotFoundException::new);

        return new AdminCurrentSongResponseDto(
                nextView.getTitle(),
                nextView.getArtist(),
                nextView.getAlbumImgUrl()
        );
    }

    @Override
    public AdminCurrentSongResponseDto getPreviousSong(Long adminId) {
        // Admin & Performance
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(AdminNotFoundException::new);
        Performance performance = admin.getPerformance();
        if (performance == null) throw new AdminPerformanceNotFoundException();

        PerformanceSong current = performance.getCurrentSong();
        Long performanceId = performance.getId();

        // 이전 곡 선택 (없으면 마지막 곡으로 순환)
        PerformanceSong prevEntity;
        if (current != null) {
            Integer currOrder = current.getOrderInPerformance();
            prevEntity = performanceSongsRepository
                    .findFirstByPerformance_IdAndOrderInPerformanceLessThanOrderByOrderInPerformanceDesc(
                            performanceId, currOrder
                    )
                    .orElseGet(() ->
                            performanceSongsRepository
                                    .findFirstByPerformance_IdOrderByOrderInPerformanceDesc(performanceId)
                                    .orElseThrow(AdminSongNotFoundException::new)
                    );
        } else {
            // 현재곡이 비어있다면 마지막 곡을 현재곡으로 시작
            prevEntity = performanceSongsRepository
                    .findFirstByPerformance_IdOrderByOrderInPerformanceDesc(performanceId)
                    .orElseThrow(AdminSongNotFoundException::new);
        }

        // current_song 갱신
        performance.setCurrentSong(prevEntity);
        performanceRepository.save(performance);

        // 프로젝션으로 응답 생성
        CurrentSongProjection view = performanceSongsRepository
                .findProjectionById(prevEntity.getId())
                .orElseThrow(AdminSongNotFoundException::new);

        return new AdminCurrentSongResponseDto(view.getTitle(), view.getArtist(), view.getAlbumImgUrl());
    }

}
