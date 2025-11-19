package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class PerformanceSongNoContentException extends BaseException {
    public PerformanceSongNoContentException() {
        super(AdminPerformanceErrorCode.PERFORMANCE_SONG_204_NO_CONTENT);
    }
}
