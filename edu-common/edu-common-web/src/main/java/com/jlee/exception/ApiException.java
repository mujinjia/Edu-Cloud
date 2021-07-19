package com.jlee.exception;

import com.jlee.result.ResultStatus;
import org.springframework.http.HttpStatus;

/**
 * Api接口异常
 *
 * @author jlee
 */
public class ApiException extends RuntimeException {
    final int status;

    public ApiException(String message, HttpStatus status) {
        this(message, status.value());
    }

    public ApiException(String message, ResultStatus status) {
        this(message, status.getCode());
    }

    public ApiException(ResultStatus status) {
        this(status.getMessage(), status.getCode());
    }

    public ApiException(HttpStatus status) {
        this(status.getReasonPhrase(), status.value());
    }

    public ApiException(String message, int status) {
        this(message, null, status);
    }

    public ApiException(String message, Throwable cause, HttpStatus status) {
        this(message, cause, status.value());
    }

    public ApiException(String message, Throwable cause, ResultStatus status) {
        this(message, cause, status.getCode());
    }

    public ApiException(Throwable cause, ResultStatus status) {
        this(status.getMessage(), cause, status.getCode());
    }

    public ApiException(Throwable cause, HttpStatus status) {
        this(status.getReasonPhrase(), cause, status.value());
    }

    public ApiException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
