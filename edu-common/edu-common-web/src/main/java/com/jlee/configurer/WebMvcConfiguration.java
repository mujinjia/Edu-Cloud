package com.jlee.configurer;

import com.jlee.config.ResponseResultProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 配置类，配置自定义返回值处理器
 * 如果通过继承 WebMvcConfigurationSupport 类的方式实现，spring默认配置将会失效（即使在配置文件中加入了配置）
 * 因为默认配置加载类WebMvcAutoConfiguration 添加了 @ConditionalOnMissingBean(WebMvcConfigurationSupport.class) 注解，而在WebMvcConfigurationSupport中没去取读取配置类，所以需要自己去读取
 * 为了不破环使用者的配置，选择继承WebMvcConfigurer实现
 *
 * @author jlee
 */
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 响应结果集处理的Handler
     */
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    /**
     * 异常处理的Handler
     */
    private final HandlerExceptionResolver handlerExceptionResolver;
    /**
     * 设置了默认配置的消息转换器工厂
     */
    private final ObjectProvider<HttpMessageConverters> messageConvertersProvider;
    /**
     * 自定义的结果集配置类
     */
    private final ResponseResultProperties responseResultProperties;

    public WebMvcConfiguration(ResponseResultProperties responseResultProperties, RequestMappingHandlerAdapter requestMappingHandlerAdapter, HandlerExceptionResolver handlerExceptionResolver, ObjectProvider<HttpMessageConverters> messageConvertersProvider) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.responseResultProperties = responseResultProperties;
        this.messageConvertersProvider = messageConvertersProvider;
    }

    @PostConstruct
    public void init() {
        final List<HandlerMethodReturnValueHandler> newHandlers = new LinkedList<>();
        final List<HandlerMethodReturnValueHandler> originalHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();

        final List<HttpMessageConverter<?>> converters = new ArrayList<>();
        this.messageConvertersProvider
                .ifAvailable((customConverters) -> converters.addAll(customConverters.getConverters()));
        // 手动new对象的方式传递系统配置的转换器
        ReturnValueHandler returnValueHandler = new ReturnValueHandler(this.responseResultProperties, converters);

        // 将自定义的returnValueHandler 添加到了正常结果返回的Handel处理集合中，
        if (null != originalHandlers) {
            newHandlers.addAll(originalHandlers);
            // 获取处理器应处于的位置，需要在RequestResponseBodyMethodProcessor之前
            // 因为如果自定义的Handler在RequestResponseBodyMethodProcessor之后就不会被调用
            // 可以在HandlerMethodReturnValueHandlerComposite类的selectHandler方法中查看Handler的调用顺序
            final int index = obtainValueHandlerPosition(originalHandlers);
            newHandlers.add(index, returnValueHandler);
        } else {
            newHandlers.add(returnValueHandler);
        }

        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);

        // 将自定义的returnValueHandler 添加到异常捕获处理后的Handler中，
        final List<HandlerExceptionResolver> exceptionResolvers = ((HandlerExceptionResolverComposite) handlerExceptionResolver).getExceptionResolvers();
        if (!CollectionUtils.isEmpty(exceptionResolvers)) {
            // 异常捕获后的Handler在ExceptionHandlerExceptionResolver中存放
            for (HandlerExceptionResolver exceptionResolver : exceptionResolvers) {
                if (exceptionResolver instanceof ExceptionHandlerExceptionResolver) {
                    final ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = (ExceptionHandlerExceptionResolver) exceptionResolver;
                    final List<HandlerMethodReturnValueHandler> newExceptionHandlers = new LinkedList<>();
                    final HandlerMethodReturnValueHandlerComposite returnValueHandlers = exceptionHandlerExceptionResolver.getReturnValueHandlers();
                    List<HandlerMethodReturnValueHandler> originalExceptionHandlers = null;
                    if (returnValueHandlers != null) {
                        originalExceptionHandlers = returnValueHandlers.getHandlers();
                    }

                    if (null != originalExceptionHandlers) {
                        newExceptionHandlers.addAll(originalExceptionHandlers);
                        final int index = obtainValueHandlerPosition(originalExceptionHandlers);
                        newExceptionHandlers.add(index, returnValueHandler);
                    } else {
                        newExceptionHandlers.add(returnValueHandler);
                    }
                    exceptionHandlerExceptionResolver.setReturnValueHandlers(newExceptionHandlers);
                    break;
                }
            }
        }

    }


    /**
     * 获取让自定义处理器生效应处于的位置
     *
     * @param originalHandlers 已经添加的返回值处理器
     * @return 自定义处理器需要的位置
     */

    private int obtainValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers) {
        for (int i = 0; i < originalHandlers.size(); i++) {
            final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
            if (RequestResponseBodyMethodProcessor.class.isAssignableFrom(valueHandler.getClass())) {
                return i;
            }
        }
        return -1;
    }

}
