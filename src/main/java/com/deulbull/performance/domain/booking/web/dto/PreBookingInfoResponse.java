package com.deulbull.performance.domain.booking.web.dto;

public record PreBookingInfoResponse(
        String openChatUrl,
        String preSaleEndTime,
        String preSaleFee,
        String onSiteFee,
        String bankName,
        String bankAccount,
        String accountHolder,
        String kakaopayUrl,
        String naverpayUrl
) {
}
