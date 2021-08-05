package com.jlee.converter;

import com.fasterxml.jackson.annotation.JsonValue;
import com.jlee.utils.EnumUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * springMVC 枚举类的转换器
 * 如果枚举类中有工厂方法(静态方法)被标记为@{@link JsonValue },则调用该方法转为枚举对象
 *
 * @author jlee
 */
@SuppressWarnings({"unchecked"})
public class MyStringToEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    private final ConcurrentMap<Class<? extends Enum<?>>, EnumMvcConverterHolder> holderMapper = new ConcurrentHashMap<>();


    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        EnumMvcConverterHolder holder = holderMapper.computeIfAbsent(targetType, EnumMvcConverterHolder::createHolder);
        return (Converter<String, T>) holder.converter;
    }


    public static class EnumMvcConverterHolder {
        @Nullable
        final EnumMvcConverter<?> converter;

        public EnumMvcConverterHolder(@Nullable EnumMvcConverter<?> converter) {
            this.converter = converter;
        }

        static <T extends Enum<?>> EnumMvcConverterHolder createHolder(Class<T> targetType) {
            Method method = EnumUtils.getJsonCreatorMethod(targetType);
            return new EnumMvcConverterHolder(new EnumMvcConverter<>(targetType, method));
        }

    }

    public static class EnumMvcConverter<T extends Enum<T>> implements Converter<String, T> {

        private final Method method;
        private final Class<?> enumType;

        public EnumMvcConverter(Class<?> enumType, Method method) {
            this.enumType = enumType;
            this.method = method;
            if (method != null) {
                this.method.setAccessible(true);
            }
        }

        @Override
        public T convert(String source) {
            if (source.isEmpty()) {
                // reset the enum value to null.
                return null;
            }
            try {
                if (method == null) {
                    //  没有加注解
                    Enum<?>[] enumValues = (Enum<?>[]) this.enumType.getEnumConstants();
                    for (Enum<?> enumValue : enumValues) {
                        if (enumValue.name().equals(source)) {
                            return (T) enumValue;
                        }
                    }
                    // source 值与 name不匹配时尝试 从 int类型中取
                    return ((T[]) enumValues)[Integer.parseInt(source)];
                }
                // 有加注解直接走注解方法
                return (T) method.invoke(null, source);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

    }


}