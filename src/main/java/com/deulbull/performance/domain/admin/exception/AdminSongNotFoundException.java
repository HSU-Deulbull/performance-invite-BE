package com.deulbull.performance.domain.admin.exception;

import com.deulbull.performance.global.exception.BaseException;

public class AdminSongNotFoundException extends BaseException {
    public AdminSongNotFoundException() {
        super(AdminPerformanceErrorCode.ADMIN_SONG_404_NOT_FOUND);
    }
}
