package com.deulbull.performance.global.exception;

import com.deulbull.performance.domain.booking.exception.BookingConflictException;
import com.deulbull.performance.global.response.ErrorResponse;
import com.deulbull.performance.global.response.code.GlobalErrorCode;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
        javax.validation.Valid or @Validated 으로 binding error 발생시 발생
        주로 @RequestBody, @RequestPart 어노테이션에서 발생
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY,
                e.getFieldError().getDefaultMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* binding error 발생시 BindException 발생 */
    @ExceptionHandler(BindException.class)
    private ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* enum type 일치하지 않아 binding 못할 경우 발생 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* 해당 엔드포인트에 적합한 컨트롤러가 없을 경우 발생*/
    @ExceptionHandler(NoResourceFoundException.class)
    private ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException e) {
        log.error("NoResourceFoundException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.ENDPOINT_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /* 지원하지 않은 HTTP method 호출 할 경우 발생 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.UNSUPPORTED_HTTP_METHOD);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* request 값을 읽을 수 없을 때 발생 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.BAD_REQUEST_ERROR);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* 낙관적 락 충돌 에러 */
    @ExceptionHandler(OptimisticLockException.class)
    private ResponseEntity<ErrorResponse> handleOptimisticLockException(OptimisticLockException e) {
        log.error("OptimisticLockException Error - 동시성 충돌 발생", e);
        throw new BookingConflictException();
    }

    /* 비지니스 로직 에러 */
    @ExceptionHandler(BaseException.class)
    private ResponseEntity<ErrorResponse> handleBusinessException(BaseException e) {
        log.error("BusinessError ");
        log.error(e.getErrorCode().getMessage());
        ErrorResponse error = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }


    /* 나머지 예외 처리 */
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception e) {
        // Spring Security의 인증/인가 예외는 Spring Security가 처리하도록 다시 던짐
        if (e instanceof AuthenticationException) {
            throw (AuthenticationException) e;
        }

        log.error("Exception Error ", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.SERVER_ERROR);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

}