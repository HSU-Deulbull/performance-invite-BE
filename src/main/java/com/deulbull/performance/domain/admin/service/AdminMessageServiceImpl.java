package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.exception.AdminNotFoundException;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.domain.admin.web.dto.AdminMessageRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminMessageTargetCountResponseDto;
import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.booking.repository.BookingRepository;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.global.response.SuccessResponse;
import com.deulbull.performance.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMessageServiceImpl implements AdminMessageService {
    private final AdminRepository adminRepository;
    private final BookingRepository bookingRepository;

    // 문자 발송 대상 인원 수 조회
    @Override
    public AdminMessageTargetCountResponseDto getMessageTargetCount(Long adminId) {
        // 404: 해당 ID의 관리자 없음
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(AdminNotFoundException::new);
        Performance performance = admin.getPerformance();

        List<Booking> bookings = bookingRepository.findAllByPerformanceId(performance.getId());

        // 예매자 수
        int smsTargetCount = bookings.size();

        // 총 예매 인원 수
        int totalBookingCount = bookings.stream()
                .mapToInt(Booking::getHeadCount)
                .sum();

        // 반환
        return new AdminMessageTargetCountResponseDto(
                smsTargetCount,
                totalBookingCount
        );
    }




}
