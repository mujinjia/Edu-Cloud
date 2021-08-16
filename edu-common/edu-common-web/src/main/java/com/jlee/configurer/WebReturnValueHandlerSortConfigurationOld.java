package com.jlee.configurer;

import org.springframework.util.CollectionUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.LinkedList;
import java.util.List;

/**
 * 配合  WebMvcConfigurationOld 类将自定义的 ReturnValueHandler 修改到前面，否则默认在最后
 *
 * @author lijia
 * @since 2021/8/16
 */
public class WebReturnValueHandlerSortConfigurationOld {

    public WebReturnValueHandlerSortConfigurationOld(RequestMappingHandlerAdapter requestMappingHandlerAdapter, HandlerExceptionResolver handlerExceptionResolver) {

        final List<HandlerMethodReturnValueHandler> newHandlers = new LinkedList<>();
        final List<HandlerMethodReturnValueHandler> originalHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        // 将自定义的returnValueHandler 添加到了正常结果返回的Handel处理集合中，
        if (null != originalHandlers) {
            // 获取处理器应处于的位置，需要在RequestResponseBodyMethodProcessor之前
            // 因为如果自定义的Handler在RequestResponseBodyMethodProcessor之后就不会被调用
            // 可以在HandlerMethodReturnValueHandlerComposite类的selectHandler方法中查看Handler的调用顺序
            newHandlers.addAll(originalHandlers);
            final int returnIndex = obtainValueHandlerPosition(newHandlers, ReturnValueHandler.class);
            final HandlerMethodReturnValueHandler returnValueHandler = newHandlers.remove(returnIndex);
            final int responseBodyIndex = obtainValueHandlerPosition(newHandlers, RequestResponseBodyMethodProcessor.class);
            newHandlers.add(responseBodyIndex, returnValueHandler);
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
                        final int returnIndex = obtainValueHandlerPosition(newExceptionHandlers, ReturnValueHandler.class);
                        final HandlerMethodReturnValueHandler returnValueHandler = newExceptionHandlers.remove(returnIndex);
                        final int responseBodyIndex = obtainValueHandlerPosition(newExceptionHandlers, RequestResponseBodyMethodProcessor.class);
                        newExceptionHandlers.add(responseBodyIndex, returnValueHandler);
                    }
                    exceptionHandlerExceptionResolver.setReturnValueHandlers(newExceptionHandlers);
                    break;
                }
            }
        }

    }

    private int obtainValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers, Class<?> handlerClass) {
        for (int i = 0; originalHandlers != null && i < originalHandlers.size(); i++) {
            final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
            if (handlerClass.isAssignableFrom(valueHandler.getClass())) {
                return i;
            }
        }
        return 0;
    }
}
