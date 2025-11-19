package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.deulbull.performance.global.constant.StaticValue.NOT_FOUND;
import static com.deulbull.performance.global.constant.StaticValue.NO_CONTENT;

@Getter
@RequiredArgsConstructor
public enum AdminPerformanceErrorCode implements BaseResponseCode {
    ADMIN_SONG_404_NOT_FOUND("ADMIN_SONG_404_NOT_FOUND", NOT_FOUND, "해당 공연에 등록된 곡이 없습니다."),
    ADMIN_PERFORMANCE_404_NOT_FOUND("ADMIN_PERFORMANCE_404_NOT_FOUND", NOT_FOUND, "해당 공연의 ID가 없습니다."),
    PERFORMANCE_NEXT_SONG_404_NOT_FOUND("PERFORMANCE_NEXT_SONG_404_NOT_FOUND", NOT_FOUND, "해당 곡이 마지막 곡입니다."),
    PERFORMANCE_PREVIOUS_SONG_404_NOT_FOUND("PERFORMANCE_PREVIOUS_SONG_404_NOT_FOUND", NOT_FOUND, "이전 곡을 찾을 수 없습니다."),
    PERFORMANCE_SONG_204_NO_CONTENT("PERFORMANCE_SONG_204_NO_CONTENT", NO_CONTENT,"아직 공연 시작 전 입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
