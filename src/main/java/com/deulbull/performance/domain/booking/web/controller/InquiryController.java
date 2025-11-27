package com.deulbull.performance.domain.booking.web.controller;

import com.deulbull.performance.domain.booking.service.BookingService;
import com.deulbull.performance.domain.booking.web.dto.PreBookingInfoResponse;
import com.deulbull.performance.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/performances/{performanceId}")
@RequiredArgsConstructor
public class InquiryController {

    private final BookingService bookingService;

    // 사전 예매 관련 정보 조회 API
    @GetMapping("/inquiry")
    public ResponseEntity<SuccessResponse<PreBookingInfoResponse>> getPreBookingInfo(
            @PathVariable Long performanceId
    ) {
        PreBookingInfoResponse response = bookingService.getPreBookingInfo(performanceId);
        return ResponseEntity.ok(SuccessResponse.ok(response));
    }
}
