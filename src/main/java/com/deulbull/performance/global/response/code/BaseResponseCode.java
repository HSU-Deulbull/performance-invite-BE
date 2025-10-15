package com.deulbull.performance.global.response.code;

public interface BaseResponseCode {
    String getCode();
    String getMessage();
    int getHttpStatus();
}