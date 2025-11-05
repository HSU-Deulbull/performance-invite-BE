package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class AdminNotFoundException extends BaseException {
    public AdminNotFoundException() {
        super(AdminErrorCode.AUTH_404_ADMIN_NOT_FOUND);
    }
}
