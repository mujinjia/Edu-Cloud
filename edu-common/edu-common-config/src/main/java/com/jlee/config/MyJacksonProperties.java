package com.jlee.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.TimeZone;

/**
 * @author jlee
 * @date 2021/7/28 0028 22:13
 */
@Configuration
@ConfigurationProperties(prefix = "spring.jackson")
public class MyJacksonProperties {


    private static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";
    private static final String LOCAL_TIME_FORMAT = "HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String localDateFormat;
    private String localDateTimeFormat;
    private String localTimeFormat;
    private String dateFormat;
    private TimeZone timeZone;


    public String getLocalDateTimeFormat() {
        if (!StringUtils.hasText(localDateTimeFormat)) {
            return LOCAL_DATE_TIME_FORMAT;
        }
        return localDateTimeFormat;
    }

    public void setLocalDateTimeFormat(String localDateTimeFormat) {
        this.localDateTimeFormat = localDateTimeFormat;
    }

    public String getLocalDateFormat() {
        if (!StringUtils.hasText(localDateFormat)) {
            return LOCAL_DATE_FORMAT;
        }
        return localDateFormat;
    }

    public void setLocalDateFormat(String localDateFormat) {
        this.localDateFormat = localDateFormat;
    }


    public String getLocalTimeFormat() {
        if (!StringUtils.hasText(localTimeFormat)) {
            return LOCAL_TIME_FORMAT;
        }
        return localTimeFormat;
    }

    public void setLocalTimeFormat(String localTimeFormat) {
        this.localTimeFormat = localTimeFormat;
    }

    public String getDateFormat() {
        if (!StringUtils.hasText(dateFormat)) {
            return DATE_FORMAT;
        }
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
