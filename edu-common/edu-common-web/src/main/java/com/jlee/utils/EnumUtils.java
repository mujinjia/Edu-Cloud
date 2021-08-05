package com.jlee.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author jlee
 * @since 2021/8/5
 */
public class EnumUtils {

    /**
     * 获取枚举类中 标记了 JsonCreator 注解的一个方法
     *
     * @param targetType 枚举类 class
     * @param <T>        枚举类型
     * @return 方法，如果没有 JsonCreator 注解返回 null
     */
    public static <T extends Enum<?>> Method getJsonCreatorMethod(Class<T> targetType) {
        List<Method> methodList = BeanUtils.getMethodsListWithAnnotation(targetType, JsonCreator.class, true);
        Method method = null;
        if (!CollectionUtils.isEmpty(methodList)) {
            Assert.isTrue(methodList.size() == 1, "@JsonValue 只能标记在一个工厂方法(静态方法)上");
            method = methodList.get(0);
            Assert.isTrue(Modifier.isStatic(method.getModifiers()), "@JsonValue 只能标记在工厂方法(静态方法)上");
        }
        return method;
    }
}
