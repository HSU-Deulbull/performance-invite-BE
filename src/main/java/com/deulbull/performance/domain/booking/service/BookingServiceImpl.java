package com.deulbull.performance.domain.booking.service;

import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.booking.exception.BookingDeadlinePassedException;
import com.deulbull.performance.domain.booking.repository.BookingRepository;
import com.deulbull.performance.domain.booking.web.dto.BookingRequestDto;
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
}
