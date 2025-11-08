package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.deulbull.performance.global.constant.StaticValue.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum AdminPerformanceErrorCode implements BaseResponseCode {
    ADMIN_SONG_404_NOT_FOUND("ADMIN_SONG_404_NOT_FOUND", NOT_FOUND, "해당 공연에 등록된 곡이 없습니다."),
    ADMIN_PERFORMANCE_404_NOT_FOUND("ADMIN_PERFORMANCE_404_NOT_FOUND", NOT_FOUND, "해당 공연의 ID가 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
