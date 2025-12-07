package com.deulbull.performance.domain.band.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.deulbull.performance.global.constant.StaticValue.BAD_REQUEST;

@AllArgsConstructor
@Getter
public enum BandErrorCode implements BaseResponseCode {
    BAND_404_NOT_FOUND("BAND_404_NOT_FOUND", BAD_REQUEST, "해당 ID의 밴드를 찾을 수 없습니다."),
    PERSON_404_NOT_FOUND("PERSON_404_NOT_FOUND", BAD_REQUEST, "해당 ID의 사람을 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
