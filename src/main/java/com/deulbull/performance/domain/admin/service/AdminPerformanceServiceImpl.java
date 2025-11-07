package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.exception.AdminNotFoundException;
import com.deulbull.performance.domain.admin.repository.AdminPerformanceRepository;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.domain.admin.web.dto.AdminCurrentSongResponseDto;
import com.deulbull.performance.domain.song.projection.CurrentSongProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPerformanceServiceImpl implements AdminPerformanceService {

    private final AdminRepository adminRepository;
    private final AdminPerformanceRepository adminPerformanceRepository;

    @Override
    public AdminCurrentSongResponseDto getCurrentSong(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(AdminNotFoundException::new);
        CurrentSongProjection currentSong = adminPerformanceRepository.findCurrentSongDtoByAdminId(admin.getId()).orElseThrow(AdminNotFoundException::new);
        return new AdminCurrentSongResponseDto(currentSong.getTitle(), currentSong.getArtist(), currentSong.getAlbumImgUrl());
    }
}
