package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class AdminDuplicatePasswordException extends BaseException {
    public AdminDuplicatePasswordException() {
        super(AdminErrorCode.ADMIN_409_DUPLICATE_PASSWORD);
    }
}
