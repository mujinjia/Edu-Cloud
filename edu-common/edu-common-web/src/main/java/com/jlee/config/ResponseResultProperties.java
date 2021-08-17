package com.jlee.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * @author jlee
 * @since 2021/7/16
 */
@Configuration
@ConfigurationProperties("result")
public class ResponseResultProperties {

    /**
     * 成功状态的默认code值
     */
    private static final int DEFAULT_SUCCESS_CODE = 200;
    /**
     * 成功状态的默认提示信息
     */
    private static final String DEFAULT_SUCCESS_MESSAGE = "成功";
    /**
     * 找不到资源时状态的默认code值
     */
    private static final int DEFAULT_NOT_FOUND_CODE = 404;
    /**
     * 资源时状态的默认提示信息
     */
    private static final String DEFAULT_NOT_FOUND_MESSAGE = "未找到";
    /**
     * 结果集字段名
     */
    private static final String DEFAULT_RESULT_FIELD_NAME = "result";
    /**
     * 状态值字段名
     */
    private static final String DEFAULT_STATUS_FIELD_NAME = "code";
    /**
     * 消息值字段名
     */
    private static final String DEFAULT_MESSAGE_FIELD_NAME = "message";
    /**
     * message 在 响应头中标题
     */
    private static final String DEFAULT_MESSAGE_HEAD_TITLE = "xx-message";


    //========================

    /**
     * 统一结果集ResultStatus成功状态的code值
     */
    private int successCode;
    /**
     * 统一结果集ResultStatus成功状态的提示信息
     */
    private String successMessage;
    /**
     * 统一结果集ResultStatus找不到资源时状态的code值
     */
    private int notFoundCode;
    /**
     * 统一结果集ResultStatus找不到资源时状态的提示信息
     */
    private String notFoundMessage;

    /**
     * 默认失败时的 HttpStatus  抛异常时 如果没有设置HttpStatus 取 HttpStatus.BAD_REQUEST enabledHttpStatus字段为true时才起作用
     */
    private HttpStatus failHttpStatus;

    /**
     * 结果集字段名
     */
    private String resultFieldName;
    /**
     * 状态值字段名
     */
    private String codeFieldName;
    /**
     * 消息值字段名
     */
    private String messageFieldName;

    /**
     * 启用Http状态，即将设置的状态code放到Http响应头中
     */
    private boolean enabledHttpStatus;

    /**
     * 业务状态值 code 在 响应头中标题
     * 如果配置了该字段，会将提示信息放入Http响应同中，此时 响应体中中包含data内容，否则 响应体中只包含status、message、和data内容
     * enabledHttpStatus字段为true时才起作用
     */
    private String codeHeadTitle;

    /**
     * message 在 响应头中标题
     * 默认值为 xx-message
     * enabledHttpStatus字段为true且 codeHeadTitle有值时才起作用
     */
    private String messageHeadTitle;

    public String getResultFieldName() {
        if (!StringUtils.hasText(resultFieldName)) {
            resultFieldName = DEFAULT_RESULT_FIELD_NAME;
        }
        return resultFieldName;
    }

    public void setResultFieldName(String resultFieldName) {
        this.resultFieldName = resultFieldName;
    }

    public int getSuccessCode() {
        if (successCode == 0) {
            successCode = DEFAULT_SUCCESS_CODE;
        }
        return successCode;
    }

    public void setSuccessCode(int successCode) {
        this.successCode = successCode;
    }

    public HttpStatus getFailHttpStatus() {
        if (failHttpStatus == null) {
            failHttpStatus = HttpStatus.BAD_REQUEST;
        }
        return failHttpStatus;
    }

    public void setFailHttpStatus(HttpStatus failHttpStatus) {
        this.failHttpStatus = failHttpStatus;
    }

    public String getCodeFieldName() {
        if (!StringUtils.hasText(codeFieldName)) {
            codeFieldName = DEFAULT_STATUS_FIELD_NAME;
        }
        return codeFieldName;
    }

    public void setCodeFieldName(String codeFieldName) {
        this.codeFieldName = codeFieldName;
    }

    public String getMessageFieldName() {
        if (!StringUtils.hasText(messageFieldName)) {
            messageFieldName = DEFAULT_MESSAGE_FIELD_NAME;
        }
        return messageFieldName;
    }

    public void setMessageFieldName(String messageFieldName) {
        this.messageFieldName = messageFieldName;
    }

    public String getSuccessMessage() {
        if (!StringUtils.hasText(successMessage)) {
            successMessage = DEFAULT_SUCCESS_MESSAGE;
        }
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public int getNotFoundCode() {
        if (notFoundCode == 0) {
            notFoundCode = DEFAULT_NOT_FOUND_CODE;
        }
        return notFoundCode;
    }

    public void setNotFoundCode(int notFoundCode) {
        this.notFoundCode = notFoundCode;
    }

    public String getNotFoundMessage() {
        if (!StringUtils.hasText(notFoundMessage)) {
            notFoundMessage = DEFAULT_NOT_FOUND_MESSAGE;
        }
        return notFoundMessage;
    }

    public void setNotFoundMessage(String notFoundMessage) {
        this.notFoundMessage = notFoundMessage;
    }

    public boolean isEnabledHttpStatus() {
        return enabledHttpStatus;
    }

    public void setEnabledHttpStatus(boolean enabledHttpStatus) {
        this.enabledHttpStatus = enabledHttpStatus;
    }

    public String getCodeHeadTitle() {
        return codeHeadTitle;
    }

    public void setCodeHeadTitle(String codeHeadTitle) {
        this.codeHeadTitle = codeHeadTitle;
    }

    public String getMessageHeadTitle() {
        if (!StringUtils.hasText(messageHeadTitle)) {
            messageHeadTitle = DEFAULT_MESSAGE_HEAD_TITLE;
        }
        return messageHeadTitle;
    }

    public void setMessageHeadTitle(String messageHeadTitle) {
        this.messageHeadTitle = messageHeadTitle;
    }
}
