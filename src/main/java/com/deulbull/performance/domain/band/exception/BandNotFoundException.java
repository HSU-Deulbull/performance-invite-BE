package com.deulbull.performance.domain.band.exception;

import com.deulbull.performance.global.exception.BaseException;

public class BandNotFoundException extends BaseException {
    public BandNotFoundException() {
        super(BandErrorCode.BAND_404_NOT_FOUND);
    }
}
