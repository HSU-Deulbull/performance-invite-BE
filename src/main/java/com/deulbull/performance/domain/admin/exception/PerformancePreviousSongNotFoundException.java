package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class PerformancePreviousSongNotFoundException extends BaseException {
    public PerformancePreviousSongNotFoundException() {
        super(AdminPerformanceErrorCode.PERFORMANCE_PREVIOUS_SONG_404_NOT_FOUND);
    }
}
