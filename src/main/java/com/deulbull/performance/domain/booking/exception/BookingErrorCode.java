package com.deulbull.performance.domain.booking.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.deulbull.performance.global.constant.StaticValue.BAD_REQUEST;
import static com.deulbull.performance.global.constant.StaticValue.NOT_FOUND;

@AllArgsConstructor
@Getter
public enum BookingErrorCode implements BaseResponseCode {
    BOOKING_DEADLINE_PASSED("400", BAD_REQUEST, "예매가 마감되었습니다."),
    OPENCHAT_URL_NOT_FOUND("404", NOT_FOUND, "문의 링크가 등록되지 않았습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}