package com.jlee.demo.configurer;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;


final class MyIntegerToEnumConverterFactory implements ConverterFactory<Integer, Enum<?>> {

    @Override
    public <T extends Enum<?>> Converter<Integer, T> getConverter(Class<T> targetType) {
        return null;
    }


//    private static class IntegerToEnum<T extends Enum<?>> implements Converter<Integer, T> {
//
//        private final Class<T> enumType;
//
//        public IntegerToEnum(Class<T> enumType) {
//            this.enumType = enumType;
//        }
//
//        @Override
//        public T convert(Integer source) {
//            return this.enumType.getEnumConstants()[source];
//        }
//    }

}
