package com.jlee.converter;

import com.jlee.utils.EnumUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jlee
 */
@SuppressWarnings({"unchecked"})
public final class MyIntegerToEnumConverterFactory implements ConverterFactory<Integer, Enum<?>> {

    private final ConcurrentMap<Class<? extends Enum<?>>, EnumMvcConverterHolder> holderMapper = new ConcurrentHashMap<>();


    @Override
    public <T extends Enum<?>> Converter<Integer, T> getConverter(Class<T> targetType) {
        EnumMvcConverterHolder holder = holderMapper.computeIfAbsent(targetType, EnumMvcConverterHolder::createHolder);
        return (Converter<Integer, T>) holder.converter;
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

    public static class EnumMvcConverter<T extends Enum<T>> implements Converter<Integer, T> {

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
        public T convert(Integer source) {
            try {
                if (method == null) {
                    //  没有加注解
                    //  从 int类型中取
                    return ((T[]) this.enumType.getEnumConstants())[source];

                }
                // 有加注解直接走注解方法
                return (T) method.invoke(null, String.valueOf(source));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

}
