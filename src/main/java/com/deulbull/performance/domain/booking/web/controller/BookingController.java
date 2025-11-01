package com.deulbull.performance.domain.booking.web.controller;

import com.deulbull.performance.domain.booking.response.BookingSuccessCode;
import com.deulbull.performance.domain.booking.service.BookingService;
import com.deulbull.performance.domain.booking.web.dto.BookingRequestDto;
import com.deulbull.performance.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/performances/{performanceId}/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 예매 생성 API
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createBooking(
            @PathVariable Long performanceId,
            @Valid @RequestBody BookingRequestDto requestDto
    ) {
        bookingService.createBooking(performanceId, requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse<>(null, BookingSuccessCode.BOOKING_CREATED));
    }
}
