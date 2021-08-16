package com.jlee.demo.configurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置 fastjson 替代 jackson
 *
 * @author lijia
 * @since 2021/8/6
 */
//@Configuration
public class WebConfig implements WebMvcConfigurer {


    public FastJsonHttpMessageConverter fastConverter() {
        //1、定义一个convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //2、添加fastjson的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        fastJsonConfig.setDateFormat("yyyy+MM+dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNullStringAsEmpty,   //字符串null返回空字符串
                SerializerFeature.WriteNullNumberAsZero,    //数值类型为0
                SerializerFeature.WriteNullListAsEmpty,     //空字段保留
                SerializerFeature.WriteMapNullValue);
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        //2-1 处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        //3、在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.clear();
        converters.add(fastConverter());
    }
}
