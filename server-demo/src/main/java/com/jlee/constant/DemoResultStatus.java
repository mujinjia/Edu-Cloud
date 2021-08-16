package com.jlee.constant;

import com.jlee.result.ResultStatus;
import org.springframework.http.HttpStatus;

/**
 * @author jlee
 * @since 2021/7/15
 */
public enum DemoResultStatus implements ResultStatus {

    /**
     * 测试失败状态
     */
    TEST_FAILURE_STATUS(901, "测试失败状态");


    private final String message;
    private final int code;
    private final HttpStatus httpStatus;

    DemoResultStatus(Integer code, String message) {
        this(code, message, HttpStatus.BAD_REQUEST);
    }

    DemoResultStatus(Integer code, String message, HttpStatus httpStatus) {
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getCode() {
        return code;
    }
}
