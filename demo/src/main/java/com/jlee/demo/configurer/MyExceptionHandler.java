package com.jlee.demo.configurer;

import com.jlee.configurer.GlobalExceptionHandler;
import com.jlee.exception.ApiException;
import com.jlee.exception.ErrorViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jlee
 * @since 2021/7/19
 */

//@RestControllerAdvice
public class MyExceptionHandler extends GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * Api异常
     */
    @Override
    @ExceptionHandler({ApiException.class})
    public ErrorViewModel handleApiException(ApiException apiException, HttpServletRequest request) {
        final ErrorViewModel errorViewModel = createErrorViewModel(apiException.getStatus(), apiException.getMessage());
        doLogOut(apiException, errorViewModel, request);
        return errorViewModel;
    }


    /**
     * 异常日志输出方式，
     * 可以复写这个方法实现自定义的日志记录
     *
     * @param e              异常信息
     * @param errorViewModel 封装的ApiErrorViewModel
     * @param request        请求信息
     */
    @Override
    public void doLogOut(@NonNull Exception e, ErrorViewModel errorViewModel, HttpServletRequest request) {
        String message = e.getMessage();
        if (errorViewModel != null) {
            message = errorViewModel.toString();
        }
        log.error("请求的地址是：{},ApiException出现异常：{}", request.getRequestURL(), message);
    }
}
