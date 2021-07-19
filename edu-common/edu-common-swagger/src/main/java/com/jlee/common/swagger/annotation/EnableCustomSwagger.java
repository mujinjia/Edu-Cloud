package com.jlee.common.swagger.annotation;

import com.jlee.common.swagger.config.SwaggerAutoConfiguration;
import com.jlee.common.swagger.config.SwaggerWebConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * swagger 配置开关类，启动器加上该注解启动自定义swagger
 *
 * @author jlee
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SwaggerAutoConfiguration.class, SwaggerWebConfiguration.class})
public @interface EnableCustomSwagger {

}
