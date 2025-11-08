package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class AdminPerformanceNotFoundException extends BaseException {
    public AdminPerformanceNotFoundException() {
        super(AdminPerformanceErrorCode.ADMIN_PERFORMANCE_404_NOT_FOUND);
    }
}
