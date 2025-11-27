package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static com.deulbull.performance.global.constant.StaticValue.CONFLICT;
import static com.deulbull.performance.global.constant.StaticValue.NOT_FOUND;
import static com.deulbull.performance.global.constant.StaticValue.UNAUTHORIZED;


@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements BaseResponseCode {
    ADMIN_401_INVALID_PASSWORD("ADMIN_401_INVALID_PASSWORD", UNAUTHORIZED, "해당 비밀번호로 등록된 관리자가 존재하지 않습니다."),
    ADMIN_404_NOT_FOUND("ADMIN_404_NOT_FOUND", NOT_FOUND, "해당 ID의 관리자가 존재하지 않습니다."),
    ADMIN_409_DUPLICATE_PASSWORD("ADMIN_409_DUPLICATE_PASSWORD", CONFLICT, "이미 사용 중인 비밀번호입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
