package com.deulbull.performance.domain.admin.web.controller;

import com.deulbull.performance.domain.admin.service.AdminMessageService;
import com.deulbull.performance.domain.admin.web.dto.AdminMessageRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminMessageTargetCountResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import com.deulbull.performance.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 관리자 문자 발송 관련 API 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/messages")
public class AdminMessageController {

    private final AdminMessageService adminMessageService;

    // 특정 공연의 예매자들에게 단체 문자 발송
    @PostMapping
    public SuccessResponse<Void> sendBulkMessage(
            @AuthenticationPrincipal(errorOnInvalidType = false) CustomUserDetails userDetails,
            @RequestBody @Valid AdminMessageRequestDto adminMessageRequestDto
    ) {
        if (userDetails == null) {
            throw new InsufficientAuthenticationException("Authentication required");
        }

        Long adminId = userDetails.getAdminId();
        adminMessageService.sendBulkMessage(adminId, adminMessageRequestDto);
        return SuccessResponse.ok(null);
    }

    // 단체 문자 발송 대상 인원 수 조회
    @GetMapping("/performances/{performanceId}/count")
    public SuccessResponse<AdminMessageTargetCountResponseDto> getMessageTargetCount(
            @AuthenticationPrincipal(errorOnInvalidType = false) CustomUserDetails userDetails,
            @PathVariable Long performanceId
    ) {
        if (userDetails == null) {
            throw new InsufficientAuthenticationException("Authentication required");
        }

        Long adminId = userDetails.getAdminId();
        AdminMessageTargetCountResponseDto data = adminMessageService.getMessageTargetCount(adminId, performanceId);
        return SuccessResponse.ok(data);
    }
}
