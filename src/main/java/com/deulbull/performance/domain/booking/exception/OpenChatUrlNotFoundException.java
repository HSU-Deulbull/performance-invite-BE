package com.deulbull.performance.domain.booking.exception;

import com.deulbull.performance.global.exception.BaseException;

public class OpenChatUrlNotFoundException extends BaseException {
    public OpenChatUrlNotFoundException() {
        super(BookingErrorCode.OPENCHAT_URL_NOT_FOUND);
    }
}
