package com.deulbull.performance.domain.admin.web.controller;

import com.deulbull.performance.domain.admin.service.AdminMessageService;
import com.deulbull.performance.domain.admin.service.AdminService;
import com.deulbull.performance.domain.admin.web.dto.*;
import com.deulbull.performance.domain.booking.service.BookingService;
import com.deulbull.performance.domain.booking.web.dto.BookingUpdateRequestDto;
import com.deulbull.performance.global.response.SuccessResponse;
import com.deulbull.performance.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final BookingService bookingService;
    private final AdminMessageService adminMessageService;

    // 로그인
    @PostMapping("/auth/login")
    public SuccessResponse<AdminLoginResponseDto> login(@RequestBody @Valid AdminLoginRequestDto adminLoginRequestDto) {
        AdminLoginResponseDto data = adminService.login(adminLoginRequestDto);
        return SuccessResponse.ok(data);
    }

    // 회원가입
    @PostMapping("/auth/signup")
    public SuccessResponse<AdminSignupResponseDto> signup(
            @RequestBody @Valid AdminSignupRequestDto adminSignupRequestDto) {
        AdminSignupResponseDto data = adminService.signup(adminSignupRequestDto);
        return SuccessResponse.created(data);
    }

    // 예매 현황 전체 조회
    @GetMapping("/bookings")
    public SuccessResponse<BookingListResponseDto> getBookingList(
            @AuthenticationPrincipal(errorOnInvalidType = false) CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search
    ) {
        // userDetails가 null이면 Spring Security의 AuthenticationEntryPoint가 처리
        if (userDetails == null) {
            throw new InsufficientAuthenticationException("Authentication required");
        }

        Long adminId = userDetails.getAdminId();
        BookingListResponseDto data = adminService.getBookingList(adminId, page, size, search);
        return SuccessResponse.ok(data);
    }

    // 예매 수정
    @PatchMapping("/bookings/{bookingId}")
    public SuccessResponse<Void> updateBooking(
            @AuthenticationPrincipal(errorOnInvalidType = false) CustomUserDetails userDetails,
            @PathVariable Long bookingId,
            @RequestBody @Valid BookingUpdateRequestDto requestDto
    ) {
        // userDetails가 null이면 Spring Security의 AuthenticationEntryPoint가 처리
        if (userDetails == null) {
            throw new InsufficientAuthenticationException("Authentication required");
        }

        bookingService.updateBooking(bookingId, requestDto);
        return SuccessResponse.ok(null);
    }

    // 예매 삭제
    @DeleteMapping("/bookings/{bookingId}")
    public SuccessResponse<Void> deleteBooking(
            @AuthenticationPrincipal(errorOnInvalidType = false) CustomUserDetails userDetails,
            @PathVariable Long bookingId
    ) {
        // userDetails가 null이면 Spring Security의 AuthenticationEntryPoint가 처리
        if (userDetails == null) {
            throw new InsufficientAuthenticationException("Authentication required");
        }

        bookingService.deleteBooking(bookingId);
        return SuccessResponse.ok(null);
    }

    // 문자 발송 대상 인원 수 조회
    @GetMapping("/messages/count")
    public SuccessResponse<AdminMessageTargetCountResponseDto> getMessageTargetCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new InsufficientAuthenticationException("Authentication required");
        }
        Long adminId = userDetails.getAdminId();
        AdminMessageTargetCountResponseDto response = adminMessageService.getMessageTargetCount(adminId);
        return SuccessResponse.ok(response);
    }


}
