package com.jlee.result;

import org.springframework.http.HttpStatus;

/**
 * 结果集状态接口，各业务自定义状态枚举类实现这个接口
 *
 * @author jlee
 * @since 2021/7/15
 */
public interface ResultStatus {

    /**
     * 获取结果集提示信息
     *
     * @return 提示信息
     */
    String getMessage();

    /**
     * 获取 HttpStatus 状态
     *
     * @return HttpStatus
     */
    HttpStatus getHttpStatus();

    /**
     * 获取业务结果集状态码
     *
     * @return 状态码
     */
    int getCode();

    /**
     * 获取变量名 （枚举类自动有实现这个接口）
     *
     * @return 变量名
     */
    String name();
}
