package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.web.dto.*;
import com.deulbull.performance.global.security.CustomUserDetails;
import jakarta.validation.Valid;

public interface AdminService {
    // 로그인
    public AdminLoginResponseDto login(AdminLoginRequestDto adminLoginRequestDto);

    // 회원가입
    AdminSignupResponseDto signup(@Valid AdminSignupRequestDto adminSignupRequestDto);

    // 예매 현황 전체 조회
    BookingListResponseDto getBookingList(Long adminId, int page, int size);

    // 문자 발송 대상 인원 수 조회
    AdminMessageTargetCountResponseDto getMessageTargetCount(CustomUserDetails userDetails);
}
