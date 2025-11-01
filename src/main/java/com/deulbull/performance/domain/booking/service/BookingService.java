package com.deulbull.performance.domain.booking.service;

import com.deulbull.performance.domain.booking.web.dto.BookingRequestDto;

public interface BookingService {
    // 예매 생성
    void createBooking(Long performanceId, BookingRequestDto requestDto);
}
