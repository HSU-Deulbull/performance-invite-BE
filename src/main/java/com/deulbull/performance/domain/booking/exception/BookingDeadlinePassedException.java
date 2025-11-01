package com.deulbull.performance.domain.booking.exception;

import com.deulbull.performance.global.exception.BaseException;

public class BookingDeadlinePassedException extends BaseException {
    public BookingDeadlinePassedException() {
        super(BookingErrorCode.BOOKING_DEADLINE_PASSED);
    }
}