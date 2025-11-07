package com.deulbull.performance.global.jwt.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.deulbull.performance.global.constant.StaticValue.BAD_REQUEST;

@AllArgsConstructor
@Getter
public enum JwtErrorCode implements BaseResponseCode {
    JWT_403_UNAUTHORIZED("JWT_403_UNAUTHORIZED", BAD_REQUEST, "인증되지 않은 사용자입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
