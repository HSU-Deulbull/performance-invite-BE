package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static com.deulbull.performance.global.constant.StaticValue.NOT_FOUND;


@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements BaseResponseCode {
    AUTH_404_ADMIN_NOT_FOUND("AUTH_404_ADMIN_NOT_FOUND", NOT_FOUND, "해당 비밀번호로 등록된 관리자가 존재하지 않습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
