package com.deulbull.performance.domain.booking.web.controller;

import com.deulbull.performance.domain.booking.service.BookingService;
import com.deulbull.performance.domain.booking.web.dto.OpenChatUrlResponse;
import com.deulbull.performance.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/performances/{performanceId}")
@RequiredArgsConstructor
public class InquiryController {

    private final BookingService bookingService;

    // 공연 문의링크 조회 API
    @GetMapping("/inquiry")
    public ResponseEntity<SuccessResponse<OpenChatUrlResponse>> getOpenChatUrl(
            @PathVariable Long performanceId
    ) {
        OpenChatUrlResponse response = bookingService.getOpenChatUrl(performanceId);
        return ResponseEntity.ok(SuccessResponse.ok(response));
    }
}
