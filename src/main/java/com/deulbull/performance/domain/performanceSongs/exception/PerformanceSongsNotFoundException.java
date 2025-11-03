package com.deulbull.performance.domain.performanceSongs.exception;

import com.deulbull.performance.domain.performance.exception.PerformanceErrorCode;
import com.deulbull.performance.global.exception.BaseException;

public class PerformanceSongsNotFoundException extends BaseException {
    public PerformanceSongsNotFoundException() {
        super(PerformanceSongsErrorCode.PERFORMANCE_SONGS_404_NOT_FOUND);
    }
}
