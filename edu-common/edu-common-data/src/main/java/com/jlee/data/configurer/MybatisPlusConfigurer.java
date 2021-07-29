package com.jlee.data.configurer;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.jlee.config.MyJacksonProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ClassUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class MybatisPlusConfigurer {


    public MybatisPlusConfigurer(MyJacksonProperties jacksonProperties) {
        // 设置 mybatis-plus 中 JacksonTypeHandler 处理序列化和反序化时使用的 ObjectMapper
        JacksonTypeHandler.setObjectMapper(getMybatisPlusObjectMapper(jacksonProperties));
    }


    /**
     * 根据配置文件为 MybatisPlus 创建一个 ObjectMapper，虽然 ObjectMapper 是线程安全的，可以使用全局的 ObjectMapper，但避免高并发时 ObjectMapper 解析时出现线程长期等待的情况，还是考虑 MybatisPlus 单独使用一个，但 MybatisPlus 内置的 ObjectMapper 是没有加任何配置的，日期格式与习惯不同
     * 如果想 为 MybatisPlus 自定义 ObjectMapper  可以考虑继承改配置类
     *
     * @return ObjectMapper 对象
     */
    public ObjectMapper getMybatisPlusObjectMapper(MyJacksonProperties jacksonProperties) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.getLocalDateTimeFormat());
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(jacksonProperties.getLocalDateFormat());
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.getLocalTimeFormat());

        // 配置 Date 序列化和反序列化格式
        configureDateFormat(builder, jacksonProperties);

        // jdk8 日期 序列化
        builder.serializerByType(LocalDateTime.class,
                new LocalDateTimeSerializer(dateTimeFormatter));
        builder.serializerByType(LocalDate.class,
                new LocalDateSerializer(dateFormatter));
        builder.serializerByType(LocalTime.class,
                new LocalTimeSerializer(timeFormatter));

        // jdk8 日期 反序列化
        builder.deserializerByType(LocalDateTime.class,
                new LocalDateTimeDeserializer(dateTimeFormatter));
        builder.deserializerByType(LocalDate.class,
                new LocalDateDeserializer(dateFormatter));
        builder.deserializerByType(LocalTime.class,
                new LocalTimeDeserializer(timeFormatter));
        return builder.createXmlMapper(false).build();
    }

    private void configureDateFormat(Jackson2ObjectMapperBuilder builder, MyJacksonProperties jacksonProperties) {
        // 配置 Date 序列化和反序列化格式
        String dateFormat = jacksonProperties.getDateFormat();
        if (dateFormat != null) {
            try {
                Class<?> dateFormatClass = ClassUtils.forName(dateFormat, null);
                builder.dateFormat((DateFormat) BeanUtils.instantiateClass(dateFormatClass));
            } catch (ClassNotFoundException ex) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                // 设置时区
                TimeZone timeZone = jacksonProperties.getTimeZone();
                if (timeZone == null) {
                    timeZone = new ObjectMapper().getSerializationConfig().getTimeZone();
                }
                simpleDateFormat.setTimeZone(timeZone);
                builder.dateFormat(simpleDateFormat);
            }
        }
    }

    /**
     * 配置分页插件
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
