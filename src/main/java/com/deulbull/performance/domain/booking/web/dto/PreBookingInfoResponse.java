package com.deulbull.performance.domain.booking.web.dto;

import java.time.LocalDateTime;

public record PreBookingInfoResponse(
        String openChatUrl,
        LocalDateTime preSaleEndTime,
        Integer preSaleFee,
        Integer onSiteFee,
        String bankAccount,
        String kakaopayUrl,
        String naverpayUrl
) {
}
