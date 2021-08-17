package com.jlee.utils;

import com.jlee.result.ResultStatus;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

/**
 * 封装统一的返回结果集
 * <p>
 * 想要返回的结果集中为null的数据不显示:请在配置文件中配置:
 * spring.jackson.default-property-inclusion = non_null
 *
 * @author jlee
 */
public class ResponseResultUtils {


    private ResponseResultUtils() {

    }

    public static HttpStatus toHttpStatus(Object status) {
        if (status instanceof ResultStatus) {
            return ((ResultStatus) status).getHttpStatus();
        } else if (status instanceof HttpStatus) {
            return (HttpStatus) status;
        } else if (status instanceof Integer) {
            return Arrays.stream(HttpStatus.values()).filter((httpStatus) -> httpStatus.value() == (int) status).findAny().orElse(null);
        }
        // 其余类型返回 null
        return null;
    }
}
