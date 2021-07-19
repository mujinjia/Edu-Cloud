package com.jlee.demo.constant;

import com.jlee.result.ResultStatus;

/**
 * @author lijia
 * @since 2021/7/15
 */
public enum DemoResultStatus implements ResultStatus {

    /**
     * 密码和确认密码不一致
     */
    USER_REG_USER_PASSWORD_CONFIRM(801, "密码和确认密码不一致");


    private final String message;
    private final int code;

    DemoResultStatus(Integer code, String message) {
        this.message = message;
        this.code = code;
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
