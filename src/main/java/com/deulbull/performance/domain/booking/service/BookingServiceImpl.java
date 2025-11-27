package com.deulbull.performance.domain.booking.service;

import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.booking.exception.BookingDeadlinePassedException;
import com.deulbull.performance.domain.booking.exception.BookingNotFoundException;
import com.deulbull.performance.domain.booking.exception.OpenChatUrlNotFoundException;
import com.deulbull.performance.domain.booking.repository.BookingRepository;
import com.deulbull.performance.domain.booking.web.dto.BookingRequestDto;
import com.deulbull.performance.domain.booking.web.dto.BookingUpdateRequestDto;
import com.deulbull.performance.domain.booking.web.dto.PreBookingInfoResponse;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.exception.PerformanceNotFoundException;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final PerformanceRepository performanceRepository;

    @Override
    @Transactional
    public void createBooking(Long performanceId, BookingRequestDto requestDto) {
        // 1. 공연 조회
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new);

        // 2. 예매 마감 기한 확인 (preSaleEndTime 기준)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime preSaleEndTime = performance.getPreSaleEndTime();

        if (preSaleEndTime != null && now.isAfter(preSaleEndTime)) {
            throw new BookingDeadlinePassedException();
        }

        // 3. 예매 생성
        Booking booking = Booking.builder()
                .name(requestDto.name())
                .phoneNumber(requestDto.phoneNumber())
                .headCount(requestDto.headCount())
                .performance(performance)
                .build();

        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PreBookingInfoResponse getPreBookingInfo(Long performanceId) {
        // 1. 공연 조회
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new);

        // 2. 오픈채팅방 URL 존재 여부 확인
        String openchatUrl = performance.getOpenchatUrl();
        if (openchatUrl == null || openchatUrl.trim().isEmpty()) {
            throw new OpenChatUrlNotFoundException();
        }

        // 3. 사전 예매 관련 정보 응답 반환
        return new PreBookingInfoResponse(
                openchatUrl,
                performance.getPreSaleEndTime(),
                performance.getPreSaleFee(),
                performance.getOnSiteFee(),
                performance.getBankAccount(),
                performance.getKakaopayUrl(),
                performance.getNaverpayUrl()
        );
    }

    @Override
    @Transactional
    public void updateBooking(Long bookingId, BookingUpdateRequestDto requestDto) {
        // 1. 예매 조회
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        // 2. 예매 정보 수정 (JPA 변경 감지로 자동 업데이트)
        booking.setName(requestDto.name());
        booking.setPhoneNumber(requestDto.phoneNumber());
        booking.setHeadCount(requestDto.headCount());
    }

    @Override
    @Transactional
    public void deleteBooking(Long bookingId) {
        // 1. 예매 조회
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        // 2. 예매 삭제
        bookingRepository.delete(booking);
    }
}
