package com.jlee.configurer;

import com.jlee.config.ResponseResultProperties;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

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
 * <p>2、实现WebMvcConfigurer类，复写 addReturnValueHandlers 方法，WebMvcConfigurationSupport会自动的把我们加入的Handler添加到 requestMappingHandlerAdapter和handlerExceptionResolver中，但WebMvcConfigurer类没有提供获取HttpMessageConverter的方法，所以可以通过复写extendMessageConverters方法，将传入的HttpMessageConverter保存下来，因为传入的就是WebMvcConfigurationSupport中HttpMessageConverter List的引用，后期也没改变这个引用，所以无需担心他的变化</p>
 *
 * @author jlee
 */
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 自定义的结果集配置类
     */
    private final ResponseResultProperties responseResultProperties;

    @Nullable
    private List<HttpMessageConverter<?>> messageConverters;

    public WebMvcConfiguration(ResponseResultProperties responseResultProperties) {
        this.responseResultProperties = responseResultProperties;
    }

    /**
     * 获取让自定义处理器生效应处于的位置
     *
     * @param originalHandlers 已经添加的返回值处理器
     * @return 自定义处理器需要的位置
     */

    private int obtainValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers) {
        for (int i = 0; originalHandlers != null && i < originalHandlers.size(); i++) {
            final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
            if (RequestResponseBodyMethodProcessor.class.isAssignableFrom(valueHandler.getClass())) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        // 手动new对象的方式传递系统配置的转换器
        ReturnValueHandler returnValueHandler = new ReturnValueHandler(this.responseResultProperties, this.messageConverters);
        // 获取处理器应处于的位置，需要在RequestResponseBodyMethodProcessor之前
        // 因为如果自定义的Handler在RequestResponseBodyMethodProcessor之后就不会被调用
        // 可以在HandlerMethodReturnValueHandlerComposite类的selectHandler方法中查看Handler的调用顺序
        final int index = obtainValueHandlerPosition(handlers);
        handlers.add(index, returnValueHandler);
        // 会自动帮我们把Handle添加到requestMappingHandlerAdapter和handlerExceptionResolver中，所以只有写一次就可
    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // WebMvcConfigurer 中没有提供 获取 messageConverters 的方法，所以这里保存一份，方便后续使用
        this.messageConverters = converters;
    }


}
