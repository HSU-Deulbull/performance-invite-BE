package com.deulbull.performance.domain.booking.web.dto;

public record PreBookingInfoResponse(
        String openChatUrl,
        String entryStartTime, //입장 시작 시간
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
