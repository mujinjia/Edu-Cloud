package com.jlee.result;

import com.jlee.config.ResponseResultProperties;
import com.jlee.utils.ResponseResultPropertiesUtils;
import org.springframework.http.HttpStatus;

/**
 * 公共的状态值，可以通过 ResponseResultProperties 读取配置的状态值
 *
 * @author jlee
 */
class CommonResultStatus implements ResultStatus {

    private static volatile CommonResultStatus OK;
    private static volatile CommonResultStatus NOT_FOUND;

    private final String message;
    private final int code;
    private final String name;
    private final HttpStatus httpStatus;


    private CommonResultStatus() {
        this.message = "";
        this.code = 0;
        this.name = "";
        this.httpStatus = null;
    }

    private CommonResultStatus(Integer code, String message, String name, HttpStatus httpStatus) {
        this.message = message;
        this.code = code;
        this.name = name;
        this.httpStatus = httpStatus;
    }

    public static CommonResultStatus ok() {
        if (OK == null) {
            synchronized (CommonResultStatus.class) {
                if (OK == null) {
                    ResponseResultProperties responseResultProperties = ResponseResultPropertiesUtils.getResponseResultProperties();
                    OK = new CommonResultStatus(responseResultProperties.getSuccessCode(), responseResultProperties.getSuccessMessage(), "OK", HttpStatus.OK);
                }
            }
        }
        return OK;
    }

    public static CommonResultStatus notFound() {
        if (NOT_FOUND == null) {
            synchronized (CommonResultStatus.class) {
                if (NOT_FOUND == null) {
                    ResponseResultProperties responseResultProperties = ResponseResultPropertiesUtils.getResponseResultProperties();
                    NOT_FOUND = new CommonResultStatus(responseResultProperties.getNotFoundCode(), responseResultProperties.getNotFoundMessage(), "NOT_FOUND", HttpStatus.NOT_FOUND);
                }
            }
        }
        return NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}