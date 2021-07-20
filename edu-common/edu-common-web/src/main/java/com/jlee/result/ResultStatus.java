package com.jlee.result;

/**
 * 结果集状态接口，各业务自定义状态枚举类实现这个接口
 *
 * @author lijia
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
     * 获取结果集状态码
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