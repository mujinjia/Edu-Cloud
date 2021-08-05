package com.jlee.configurer;

import com.jlee.converter.MyIntegerToEnumConverterFactory;
import com.jlee.converter.MyStringToEnumConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 中 对 Enum 反序列化 配置
 *
 * @author jlee
 */
public class EnumMvcConfigure implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // MVC在需要进行转换时会去调用 ConverterFactory 获取对应类型的 Converter，然后添加到 GenericConversionService 中，
        // 而 GenericConversionService.ConvertersForPair.add 方法使用的是  this.converters.addFirst(converter); 我们自定义的会放在前面
        registry.addConverterFactory(new MyStringToEnumConverterFactory());
        registry.addConverterFactory(new MyIntegerToEnumConverterFactory());
    }

}