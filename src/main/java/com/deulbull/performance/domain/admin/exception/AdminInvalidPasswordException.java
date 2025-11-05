package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class AdminInvalidPasswordException extends BaseException {
    public AdminInvalidPasswordException() {
        super(AdminErrorCode.ADMIN_401_INVALID_PASSWORD);
    }
}
