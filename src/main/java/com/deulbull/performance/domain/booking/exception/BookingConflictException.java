package com.deulbull.performance.domain.booking.exception;

import com.deulbull.performance.global.exception.BaseException;

public class BookingConflictException extends BaseException {
    public BookingConflictException() {
        super(BookingErrorCode.BOOKING_CONFLICT);
    }
}
