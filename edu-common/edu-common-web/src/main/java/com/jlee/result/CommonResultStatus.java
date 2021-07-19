package com.jlee.result;

import com.jlee.config.ResponseResultProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 公共的状态值，可以通过 ResponseResultProperties 读取配置的状态值
 *
 * @author jlee
 */
@Component
@EnableConfigurationProperties(ResponseResultProperties.class)
class CommonResultStatus implements ResultStatus {

    private static volatile CommonResultStatus OK;
    private static volatile CommonResultStatus NOT_FOUND;


    private static ResponseResultProperties responseResultProperties;

    private final String message;
    private final int code;
    private final String name;

    private CommonResultStatus() {
        this.message = "";
        this.code = 0;
        this.name = "";
    }

    private CommonResultStatus(Integer code, String message, String name) {
        this.message = message;
        this.code = code;
        this.name = name;
    }

    public static CommonResultStatus ok() {
        if (OK == null) {
            synchronized (CommonResultStatus.class) {
                if (OK == null) {
                    OK = new CommonResultStatus(responseResultProperties.getSuccessCode(), responseResultProperties.getSuccessMessage(), "OK");
                }
            }
        }
        return OK;
    }

    public static CommonResultStatus notFound() {
        if (NOT_FOUND == null) {
            synchronized (CommonResultStatus.class) {
                if (NOT_FOUND == null) {
                    NOT_FOUND = new CommonResultStatus(responseResultProperties.getNotFoundCode(), responseResultProperties.getNotFoundMessage(), "NOT_FOUND");
                }
            }
        }
        return NOT_FOUND;
    }

    @Autowired
    public void setResponseResultProperties(ResponseResultProperties[] responseResultProperties) {
        CommonResultStatus.responseResultProperties = responseResultProperties[0];
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

}