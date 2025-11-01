package com.deulbull.performance.domain.booking.response;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.deulbull.performance.global.constant.StaticValue.CREATED;

@AllArgsConstructor
@Getter
public enum BookingSuccessCode implements BaseResponseCode {
    BOOKING_CREATED("CREATED_201", CREATED, "예매 성공하였습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
