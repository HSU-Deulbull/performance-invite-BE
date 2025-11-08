package com.deulbull.performance.domain.booking.service;

import com.deulbull.performance.domain.booking.web.dto.BookingRequestDto;
import com.deulbull.performance.domain.booking.web.dto.BookingUpdateRequestDto;
import com.deulbull.performance.domain.booking.web.dto.OpenChatUrlResponse;

public interface BookingService {
    // 예매 생성
    void createBooking(Long performanceId, BookingRequestDto requestDto);

    // 공연 문의링크 조회
    OpenChatUrlResponse getOpenChatUrl(Long performanceId);

    // 예매 정보 수정
    void updateBooking(Long bookingId, BookingUpdateRequestDto requestDto);

    // 예매 삭제
    void deleteBooking(Long bookingId);
}
