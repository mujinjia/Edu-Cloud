package com.jlee.configurer;

import com.jlee.exception.ApiErrorViewModel;
import com.jlee.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
     * 参数无效异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorViewModel handleArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {

        // 1: 从MethodArgumentNotValidException提取验证失败的所有的信息。返回一个List<FieldError>
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        // 2: 把fieldErrors中，需要的部分提出出来进行返回
        List<Map<String, String>> mapList = toValidatorMsg(fieldErrors);
        final ApiErrorViewModel errorViewModel = createErrorViewModel(HttpStatus.BAD_REQUEST.value(), "参数错误", mapList);
        doLogOut(e, errorViewModel, request);
        return errorViewModel;
    }

    /**
     * Api异常
     */
    @ExceptionHandler({ApiException.class})
    public ApiErrorViewModel handleApiException(ApiException apiException, HttpServletRequest request) {
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
     * 构建ApiErrorViewModel
     *
     * @param code    状态值
     * @param message 错误信息
     * @param errors  粗体错误列表
     * @return ApiErrorViewModel
     */
    public ApiErrorViewModel createErrorViewModel(int code, String message, List<?> errors) {
        return ApiErrorViewModel.builder()
                .status(code)
                .message(message)
                .errors(errors)
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
        List<?> errors = Collections.emptyList();
        if (errorViewModel != null) {
            message = errorViewModel.toString();
            errors = errorViewModel.getErrors();
        }
        log.error("请求的地址是：{},ApiException出现异常：{}", request.getRequestURL(), message);
        for (Object error : errors) {
            log.error(String.valueOf(error));
        }
    }


    /**
     * 对验证异常进行统一处理提取需要的部分
     *
     * @param fieldErrorList
     * @return
     */
    private List<Map<String, String>> toValidatorMsg(List<FieldError> fieldErrorList) {
        List<Map<String, String>> mapList = new ArrayList<>();
        // 循环提取
        for (FieldError fieldError : fieldErrorList) {
            Map<String, String> map = new HashMap<>();
            // 获取验证失败的属性
            map.put("field", fieldError.getField());
            // 获取验证失败的的提示信息
            map.put("msg", fieldError.getDefaultMessage());
            mapList.add(map);
        }
        return mapList;
    }
}
