package com.jlee.configurer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.jlee.exception.ApiException;
import com.jlee.exception.ErrorViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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
    public ErrorViewModel handleException(Exception e, HttpServletRequest request) {
        final ErrorViewModel errorViewModel = createErrorViewModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        doLogOut(e, errorViewModel, request);
        return errorViewModel;
    }


    /**
     * Spring MVC 参数读取异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorViewModel handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        final Throwable cause = e.getCause();
        ErrorViewModel errorViewModel;
        if (cause instanceof JsonMappingException) {
            // json 参数接收 转换异常
            final FieldError fieldError = getJsonReferenceFieldError((JsonMappingException) cause);
            errorViewModel = fieldErrorToErrorViewModel(Collections.singletonList(fieldError));
        } else {
            errorViewModel = createErrorViewModel(HttpStatus.BAD_REQUEST.value(), "参数解析失败");
        }
        doLogOut(e, errorViewModel, request);
        return errorViewModel;
    }


    /**
     * Spring MVC 非 json 参数接收 转换异常
     */
    @ExceptionHandler(BindException.class)
    public ErrorViewModel handleBindException(BindException e, HttpServletRequest request) {
        final List<FieldError> fieldErrors = e.getFieldErrors();
        final ErrorViewModel errorViewModel = fieldErrorToErrorViewModel(fieldErrors);
        doLogOut(e, errorViewModel, request);
        return errorViewModel;
    }

    /**
     * 参数无效异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorViewModel handleArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        final ErrorViewModel errorViewModel = fieldErrorToErrorViewModel(fieldErrors);
        doLogOut(e, errorViewModel, request);
        return errorViewModel;
    }

    /**
     * Api异常
     */
    @ExceptionHandler({ApiException.class})
    public ErrorViewModel handleApiException(ApiException apiException, HttpServletRequest request) {
        final ErrorViewModel errorViewModel = createErrorViewModel(apiException.getStatus(), apiException.getMessage());
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
    public ErrorViewModel createErrorViewModel(int code, String message) {
        return ErrorViewModel.builder()
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
    public ErrorViewModel createErrorViewModel(int code, String message, List<?> errors) {
        return ErrorViewModel.builder()
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
    public void doLogOut(@NonNull Exception e, ErrorViewModel errorViewModel, HttpServletRequest request) {
        String message = e.getMessage();
        List<?> errors = Collections.emptyList();
        if (errorViewModel != null) {
            message = StringUtils.hasText(errorViewModel.getMessage()) ? errorViewModel.getMessage() : errorViewModel.toString();
            errors = errorViewModel.getErrors() != null ? errorViewModel.getErrors() : Collections.emptyList();
        }
        log.error("请求的地址是：{},ApiException出现异常：{}", request.getRequestURL(), message);
        for (Object error : errors) {
            log.error(String.valueOf(error));
        }
    }


    /**
     * 将 json 转换异常信息 提取成 FieldError
     *
     * @param jsonMappingException json 异常
     * @return 提取的 FieldError
     */
    private FieldError getJsonReferenceFieldError(JsonMappingException jsonMappingException) {
        Throwable cause = jsonMappingException.getCause();
        while (cause != null && cause.getCause() != null) {
            cause = cause.getCause();
        }
        String message = jsonMappingException.getMessage();
        if (cause != null && StringUtils.hasText(message)) {
            message = cause.getMessage();
        }
        if (!StringUtils.hasText(message)) {
            message = "参数错误";
        }
        String fieldName = "body";
        final List<JsonMappingException.Reference> path = jsonMappingException.getPath();
        if (!CollectionUtils.isEmpty(path)) {
            // 取最后一条（一般也只有一条）
            final JsonMappingException.Reference reference = path.get(path.size() - 1);
            fieldName = reference.getFieldName();
        }
        return new FieldError(fieldName, fieldName, message);
    }


    /**
     * fieldError 字段的错误信息转换为 前端错误信息显示模型 ErrorViewModel
     *
     * @param fieldErrors 字段错误信息
     * @return 前端错误信息显示模型
     */
    private ErrorViewModel fieldErrorToErrorViewModel(List<FieldError> fieldErrors) {
        String message = getFieldErrorMessage(fieldErrors);
        // 把fieldErrors中，需要的部分提出出来进行返回
        List<Map<String, String>> mapList = toValidatorMsg(fieldErrors);
        return createErrorViewModel(HttpStatus.BAD_REQUEST.value(), message, mapList);
    }

    /**
     * 对验证异常进行统一处理提取需要的部分
     *
     * @param fieldErrors 字段错误信息
     * @return 简化后的错误信息
     */
    private List<Map<String, String>> toValidatorMsg(List<FieldError> fieldErrors) {
        List<Map<String, String>> mapList = new ArrayList<>();
        // 循环提取
        for (FieldError fieldError : fieldErrors) {
            Map<String, String> map = new HashMap<>();
            // 获取验证失败的属性
            map.put("field", fieldError.getField());
            // 获取验证失败的的提示信息
            map.put("msg", fieldError.getDefaultMessage());
            mapList.add(map);
        }
        return mapList;
    }

    private String getFieldErrorMessage(List<FieldError> fieldErrors) {
        String message = "参数错误";
        if (!CollectionUtils.isEmpty(fieldErrors)) {
            message = String.format("%s 参数出错", fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(",")));
        }
        return message;
    }
}
