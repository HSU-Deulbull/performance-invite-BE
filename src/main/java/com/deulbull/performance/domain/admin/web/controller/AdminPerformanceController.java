package com.deulbull.performance.domain.admin.web.controller;

import com.deulbull.performance.domain.admin.service.AdminPerformanceService;
import com.deulbull.performance.domain.admin.web.dto.AdminCurrentSongResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import com.deulbull.performance.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminPerformanceController {

    private final AdminPerformanceService adminPerformanceService;

    @GetMapping("/performances/current")
    public SuccessResponse<AdminCurrentSongResponseDto> getCurrentSong(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long adminId = userDetails.getAdminId();
        return SuccessResponse.ok(adminPerformanceService.getCurrentSong(adminId));
    }
    @PostMapping("/performances/next")
    public SuccessResponse<AdminCurrentSongResponseDto> getNextSong(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long adminId = userDetails.getAdminId();
        return SuccessResponse.ok(adminPerformanceService.getNextSong(adminId));
    }
    @PostMapping("/performances/previous")
    public SuccessResponse<AdminCurrentSongResponseDto> getPreviousSong(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long adminId = userDetails.getAdminId();
        return SuccessResponse.ok(adminPerformanceService.getPreviousSong(adminId));
    }
}
