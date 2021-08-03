package com.jlee.demo.configurer;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jlee
 */
@Configuration
public class MvcConfigure implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {

        // org.springframework.core.convert.support.GenericConversionService.ConvertersForPair.add
//         this.converters.addFirst(converter);
        // 所以我们自定义的会放在前面

        registry.addConverterFactory(new MyStringToEnumConverterFactory());
        registry.addConverterFactory(new MyIntegerToEnumConverterFactory());
    }

}