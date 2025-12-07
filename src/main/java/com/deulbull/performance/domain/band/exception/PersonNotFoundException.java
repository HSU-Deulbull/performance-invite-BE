package com.deulbull.performance.domain.band.exception;

import com.deulbull.performance.global.exception.BaseException;

public class PersonNotFoundException extends BaseException {
    public PersonNotFoundException() {
        super(BandErrorCode.PERSON_404_NOT_FOUND);
    }
}
