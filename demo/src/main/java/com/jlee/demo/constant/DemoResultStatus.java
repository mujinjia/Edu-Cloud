package com.jlee.demo.constant;

import com.jlee.result.ResultStatus;
import org.springframework.http.HttpStatus;

/**
 * @author jlee
 * @since 2021/7/15
 */
public enum DemoResultStatus implements ResultStatus {

    /**
     * 密码和确认密码不一致
     */
    USER_REG_USER_PASSWORD_CONFIRM(801, "密码和确认密码不一致", HttpStatus.BAD_REQUEST);


    private final String message;
    private final int code;
    private final HttpStatus httpStatus;

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
