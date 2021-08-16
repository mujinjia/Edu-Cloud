package com.jlee.configurer;

import com.jlee.config.ResponseResultProperties;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;


/**
 * 配置自定义返回值处理器
 * <p>1、处理器是在 WebMvcConfigurationSupport 中才被加载，如果我们自己实现 WebMvcConfigurationSupport类可在此类提供的接口中添加自己的处理器</p>
 * <p>2、但是一般一个应用就只有一个WebMvcConfigurationSupport，如果我们自己实现了就不会去加载默认的WebMvcConfigurationSupport，作为一个jar包所以不能去实现WebMvcConfigurationSupport</p>
 * <p>3、默认的WebMvcConfigurationSupport有两个，DelegatingWebMvcConfiguration 和 WebMvcAutoConfiguration.EnableWebMvcConfiguration，如果加入@EnableWebMvc 会加载前者，如果项目中一个WebMvcConfigurationSupport都没有就会加载后者</p>
 * <p>4、默认只有在WebMvcAutoConfiguration中才会去加载配置文件，DelegatingWebMvcConfiguration没有加，用户自己写的WebMvcConfigurationSupport也可能没有加载，我们返回结果集内部使用的类型转换器HttpMessageConverter应该与用户选择一致</p>
 * <p>5、默认的两个WebMvcConfigurationSupport都会去调用其他的WebMvcConfigurer配置中的方法，但是要等所有的WebMvcConfigurer创建才会加载他们,所以如果某个WebMvcConfigurer因为在构建时依赖了WebMvcConfigurationSupport中才会放入Spring容器中的Bean，就会导致所有的WebMvcConfigurer很晚才会注入到WebMvcConfigurationSupport中，造成一些方法没有被调用，出现bug</p>
 * <p>所以为了不破环用户的选择，避免一些bug，不应该实现WebMvcConfigurationSupport来添加配置文件</p>
 * 有两种方法来添加我们的配置：
 * <p>1、依赖注入requestMappingHandlerAdapter和handlerExceptionResolver，这会造成此类很晚才会被创建，所以不去实现WebMvcConfigurer类，避免在内部拿到的HttpMessageConverter是原始的，而不是加载了配置文件或者用户设置后的，</p>
 * <p>2、实现 WebMvcConfigurer类，复写 addReturnValueHandlers 方法，WebMvcConfigurationSupport 会自动的把我们加入的Handler添加到 requestMappingHandlerAdapter和handlerExceptionResolver中，但WebMvcConfigurer类没有提供获取HttpMessageConverter的方法，所以可以通过复写extendMessageConverters方法，将传入的HttpMessageConverter保存下来，因为传入的就是WebMvcConfigurationSupport中HttpMessageConverter List的引用，后期也没改变这个引用，所以无需担心他的变化，此方法还存在一个问题 那就是 addReturnValueHandlers 会将自定义的 Handel 放到默认的之后，所以还需要一个配置类进行修改排序</p>
 * <p>
 * 该类是第一中方法，作为历史记录留存，后续会删除
 *
 * @author jlee
 */
public class WebMvcConfiguration {

    /**
     * 响应结果集处理的Handler
     */
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    /**
     * 异常处理的Handler
     */
    private final HandlerExceptionResolver handlerExceptionResolver;
    /**
     * 自定义的结果集配置类
     */
    private final ResponseResultProperties responseResultProperties;

    public WebMvcConfiguration(ResponseResultProperties responseResultProperties, RequestMappingHandlerAdapter requestMappingHandlerAdapter, HandlerExceptionResolver handlerExceptionResolver) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.responseResultProperties = responseResultProperties;
    }

    @PostConstruct
    public void init() {
        final List<HandlerMethodReturnValueHandler> newHandlers = new LinkedList<>();
        final List<HandlerMethodReturnValueHandler> originalHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();

        // 获取 converters 对象，因为requestMappingHandlerAdapter是被依赖注入的，并且没有实现WebMvcConfigurer类，所以这里拿到的converters是最终的
        final List<HttpMessageConverter<?>> converters = requestMappingHandlerAdapter.getMessageConverters();

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
        return 0;
    }

}
