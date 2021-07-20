package com.jlee.configurer;

import com.jlee.config.ResponseResultProperties;
import com.jlee.exception.ApiErrorViewModel;
import com.jlee.result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

/**
 * 包装返回值,针对有mapping注解的方法返回的json数据包装
 *
 * @author jlee
 */
public class ReturnValueHandler extends HttpEntityMethodProcessor implements HandlerMethodReturnValueHandler {

    private ResponseResultProperties responseResultProperties;

    /**
     * 要求构造函数需要一个和父类保持一致的，没有无参构造函数
     */
    public ReturnValueHandler(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Autowired
    public void setResponseResultProperties(ResponseResultProperties responseResultProperties) {
        this.responseResultProperties = responseResultProperties;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Method method = returnType.getMethod();
        // 如果拿不到返回的类型或者返回的类型已经是ResponseEntity则不处理，异常也不处理
        // 返回false的异常会交给其他Handel执行
        if (method == null || method.getReturnType().isAssignableFrom(ResponseEntity.class)
                || method.getReturnType().isAssignableFrom(Exception.class)) {
            return false;
        }
        return true;
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws Exception {


        ResponseResult<?> responseResult = null;

        if (returnValue instanceof ApiErrorViewModel) {
            Assert.isInstanceOf(ApiErrorViewModel.class, returnValue);
            ApiErrorViewModel errorViewModel = (ApiErrorViewModel) returnValue;
            responseResult = ResponseResult.of(errorViewModel.getStatus(), errorViewModel.getMessage(), null);
        } else if (returnValue instanceof ResponseResult) {
            Assert.isInstanceOf(ResponseResult.class, returnValue);
            responseResult = (ResponseResult<?>) returnValue;
        } else {
            // 将其他类型转成ResponseResult
            responseResult = ResponseResult.of(returnValue);
            // 这里不需要对String类型特殊处理，因为我的自定义的Handler会先被执行然后返回给前端
            // 如果是使用 实现ResponseBodyAdvice类的方式来进行统一类型转换就需要对String类型的返回值进行特殊处理
            // 因为 AbstractMessageConverterMethodProcessor的writeWithMessageConverters方法会先执行，传入都是原始的类型和数据
            // 然后 writeWithMessageConverters 会调用 beforeBodyWrite 拿去转换前的数据，但记录的类型还是beforeBodyWrite之前的类型
            // 对于 String会用StringHttpMessageConverter转换器进行转换，如果不特殊处理就无法转换
        }

        ResponseEntity<?> responseEntity = null;

        HttpHeaders headers = responseResult.getHeaders();
        if (headers == null) {
            headers = new HttpHeaders();
        }

        // 最后封装成ResponseEntity
        if (this.responseResultProperties.isEnabledHttpStatus()) {

            final String messageHeadTitle = responseResultProperties.getMessageHeadTitle();
            if (StringUtils.hasText(messageHeadTitle)) {
                // 提示信息进行URL编码，避免中文乱码
                headers.put(messageHeadTitle, Collections.singletonList(URLEncoder.encode(responseResult.getMessage(), "utf-8")));
                // message 内容 写入到响应头中
                // body 中只包含Data内容
                responseEntity = new ResponseEntity<>(responseResult.getData(), headers, responseResult.getCode());
            } else {
                // 将状态code设置到Http响应头中
                // body 中同样 包含状态和提示信息
                responseEntity = new ResponseEntity<>(responseResult, headers, responseResult.getCode());
            }

        } else {
            responseEntity = new ResponseEntity<>(responseResult, headers, HttpStatus.OK);
        }

        // 获取正在的结果集类型
        final HandlerMethod bodyMethod = new HandlerMethod(responseEntity, "getBody");
        final MethodParameter returnValueType = bodyMethod.getReturnValueType(responseEntity);

        // 使用父类去处理，然后返回
        super.handleReturnValue(responseEntity, returnValueType, mavContainer, webRequest);
    }

}