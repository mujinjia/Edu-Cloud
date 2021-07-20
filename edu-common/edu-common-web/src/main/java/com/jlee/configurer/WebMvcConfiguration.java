package com.jlee.configurer;

import org.springframework.util.CollectionUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

/**
 * 配置类，配置自定义返回值处理器
 *
 * @author jlee
 */
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * 自定义的返回值处理器
     */
    private final ReturnValueHandler returnValueHandler;

    public WebMvcConfiguration(ReturnValueHandler returnValueHandler, RequestMappingHandlerAdapter requestMappingHandlerAdapter, HandlerExceptionResolver handlerExceptionResolver) {
        this.returnValueHandler = returnValueHandler;
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @PostConstruct
    public void init() {
        final List<HandlerMethodReturnValueHandler> newHandlers = new LinkedList<>();
        final List<HandlerMethodReturnValueHandler> originalHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();


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
