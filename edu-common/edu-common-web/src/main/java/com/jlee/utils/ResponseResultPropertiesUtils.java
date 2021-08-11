package com.jlee.utils;

import com.jlee.config.ResponseResultProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 提供一个获取配置的工具类，减少 responseResultProperties 的注入
 *
 * @author jlee
 * @date 2021/8/11 0011 10:38
 */
@EnableConfigurationProperties(ResponseResultProperties.class)
public class ResponseResultPropertiesUtils {


    private static ResponseResultProperties responseResultProperties;

    private ResponseResultPropertiesUtils(ResponseResultProperties responseResultProperties) {
        ResponseResultPropertiesUtils.responseResultProperties = responseResultProperties;
    }

    public static ResponseResultProperties getResponseResultProperties() {
        return responseResultProperties;
    }
}
