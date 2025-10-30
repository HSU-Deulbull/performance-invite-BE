package com.deulbull.performance.domain.song.exception;

import com.deulbull.performance.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.deulbull.performance.global.constant.StaticValue.NOT_FOUND;

@AllArgsConstructor
@Getter
public enum SongErrorCode implements BaseResponseCode {
    SONG_404_NOT_FOUND("SONG_404_NOT_FOUND", NOT_FOUND, "해당 ID의 곡을 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
