package com.jlee.configurer;

import com.jlee.exception.ApiErrorViewModel;
import com.jlee.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器，
 * 可以继承这个类，添加自己特定业务的异常处理
 *
 * @author ruoyi
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 全局异常
     */
    @ExceptionHandler(Exception.class)
    public ApiErrorViewModel handleException(Exception e, HttpServletRequest request) {
        final ApiErrorViewModel errorViewModel = createErrorViewModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        doLogOut(e, errorViewModel, request);
        return errorViewModel;
    }

    /**
     * Api异常
     */
    @ExceptionHandler({ApiException.class})
    public ApiErrorViewModel handleException(ApiException apiException, HttpServletRequest request) {
        final ApiErrorViewModel errorViewModel = createErrorViewModel(apiException.getStatus(), apiException.getMessage());
        doLogOut(apiException, errorViewModel, request);
        return errorViewModel;
    }

    /**
     * 构建ApiErrorViewModel
     *
     * @param code    状态值
     * @param message 错误信息
     * @return ApiErrorViewModel
     */
    public ApiErrorViewModel createErrorViewModel(int code, String message) {
        return ApiErrorViewModel.builder()
                .status(code)
                .message(message)
                .build();
    }

    /**
     * 异常日志输出方式，
     * 可以复写这个方法实现自定义的日志记录
     *
     * @param e              异常信息
     * @param errorViewModel 封装的ApiErrorViewModel
     * @param request        请求信息
     */
    public void doLogOut(@NonNull Exception e, ApiErrorViewModel errorViewModel, HttpServletRequest request) {
        String message = e.getMessage();
        if (errorViewModel != null) {
            message = errorViewModel.toString();
        }
        log.error("请求的地址是：{},ApiException出现异常：{}", request.getRequestURL(), message);
    }
}
