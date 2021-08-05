package com.jlee.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.jlee.config.MyJacksonProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson2ObjectMapper 中 Jackson 序列化和反序列化时日期格式配置
 *
 * @author jlee
 */
@ConditionalOnClass(ObjectMapper.class)
public class JacksonDateFormatConfigure {

    @Bean
    @ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
    public Jackson2ObjectMapperBuilderCustomizer customizeJackson(MyJacksonProperties jacksonProperties) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.getLocalDateTimeFormat());
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(jacksonProperties.getLocalDateFormat());
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.getLocalTimeFormat());

        return builder -> {
            // DateTimeFormatter  是一个线程安全的类，Serializer和Deserializer可以共用一个

            // 序列化
            builder.serializerByType(LocalDateTime.class,
                    new LocalDateTimeSerializer(dateTimeFormatter));
            builder.serializerByType(LocalDate.class,
                    new LocalDateSerializer(dateFormatter));
            builder.serializerByType(LocalTime.class,
                    new LocalTimeSerializer(timeFormatter));

            // 反序列化
            builder.deserializerByType(LocalDateTime.class,
                    new LocalDateTimeDeserializer(dateTimeFormatter));
            builder.deserializerByType(LocalDate.class,
                    new LocalDateDeserializer(dateFormatter));
            builder.deserializerByType(LocalTime.class,
                    new LocalTimeDeserializer(timeFormatter));
        };
    }
}