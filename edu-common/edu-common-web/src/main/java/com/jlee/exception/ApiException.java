package com.jlee.exception;

import com.jlee.result.ResultStatus;
import org.springframework.http.HttpStatus;

/**
 * Api接口异常
 *
 * @author jlee
 */
public class ApiException extends RuntimeException {
    /**
     * 业务状态码
     */
    private final int code;
    private final Object status;

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

    public ApiException(String message, int code) {
        this(message, null, code, code);
    }

    public ApiException(String message, Throwable cause, HttpStatus status) {
        this(message, cause, status.value(), status);
    }

    public ApiException(String message, Throwable cause, ResultStatus status) {
        this(message, cause, status.getCode(), status);
    }

    public ApiException(Throwable cause, ResultStatus status) {
        this(status.getMessage(), cause, status.getCode(), status);
    }

    public ApiException(Throwable cause, HttpStatus status) {
        this(status.getReasonPhrase(), cause, status.value(), status);
    }

    public ApiException(String message, Throwable cause, int code, Object status) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }

    public int getCode() {
        return this.code;
    }

    /**
     * 获取 HttpStatus 状态
     *
     * @return HttpStatus
     */
    public HttpStatus getHttpStatus() {
        if (this.status instanceof ResultStatus) {
            return ((ResultStatus) this.status).getHttpStatus();
        } else if (this.status instanceof HttpStatus) {
            return (HttpStatus) this.status;
        } else {
            // status 保存的时int值
            return HttpStatus.valueOf((int) this.status);
        }
    }
}
