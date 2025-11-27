package com.deulbull.performance.domain.booking.service;

import com.deulbull.performance.domain.booking.web.dto.BookingRequestDto;
import com.deulbull.performance.domain.booking.web.dto.BookingUpdateRequestDto;
import com.deulbull.performance.domain.booking.web.dto.PreBookingInfoResponse;

public interface BookingService {
    // 예매 생성
    void createBooking(Long performanceId, BookingRequestDto requestDto);

    // 사전 예매 관련 정보 조회
    PreBookingInfoResponse getPreBookingInfo(Long performanceId);

    // 예매 정보 수정
    void updateBooking(Long bookingId, BookingUpdateRequestDto requestDto);

    // 예매 삭제
    void deleteBooking(Long bookingId);
}
