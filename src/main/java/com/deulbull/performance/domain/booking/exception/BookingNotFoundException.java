package com.deulbull.performance.domain.booking.exception;

import com.deulbull.performance.global.exception.BaseException;

public class BookingNotFoundException extends BaseException {
    public BookingNotFoundException() {
        super(BookingErrorCode.BOOKING_NOT_FOUND);
    }
}
