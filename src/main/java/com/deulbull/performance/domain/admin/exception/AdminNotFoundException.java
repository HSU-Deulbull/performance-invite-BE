package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class AdminNotFoundException extends BaseException {
    public AdminNotFoundException() {
        super(AdminErrorCode.ADMIN_404_NOT_FOUND);
    }
}