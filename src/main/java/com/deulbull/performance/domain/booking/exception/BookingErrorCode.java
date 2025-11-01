package com.deulbull.performance.domain.booking.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.deulbull.performance.global.constant.StaticValue.BAD_REQUEST;

@AllArgsConstructor
@Getter
public enum BookingErrorCode implements BaseResponseCode {
    BOOKING_DEADLINE_PASSED("400", BAD_REQUEST, "예매가 마감되었습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}