package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class PerformanceNextSongNotFoundException extends BaseException {
    public PerformanceNextSongNotFoundException() {
        super(AdminPerformanceErrorCode.PERFORMANCE_NEXT_SONG_404_NOT_FOUND);
    }
}
