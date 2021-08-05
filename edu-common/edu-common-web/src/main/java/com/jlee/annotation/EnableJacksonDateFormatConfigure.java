package com.jlee.annotation;

import com.jlee.configurer.JacksonDateFormatConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Jackson2ObjectMapper 中 Jackson 对 日期格式的处理
 *
 * @author jlee
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({JacksonDateFormatConfigure.class,})
public @interface EnableJacksonDateFormatConfigure {

}
