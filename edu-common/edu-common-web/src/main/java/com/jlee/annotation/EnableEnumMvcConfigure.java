package com.jlee.annotation;

import com.jlee.configurer.EnumMvcConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Spring MVC 中 对 Enum 反序列化 配置
 *
 * @author jlee
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EnumMvcConfigure.class,})
public @interface EnableEnumMvcConfigure {

}
