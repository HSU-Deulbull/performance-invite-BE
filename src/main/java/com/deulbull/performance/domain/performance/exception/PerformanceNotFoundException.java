package com.deulbull.performance.domain.performance.exception;

import com.deulbull.performance.global.exception.BaseException;

public class PerformanceNotFoundException extends BaseException {
    public PerformanceNotFoundException() {
        super(PerformanceErrorCode.PERFORMANCE_404_NOT_FOUND);
    }
}
