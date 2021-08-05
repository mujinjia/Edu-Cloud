package com.jlee.utils;

import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jlee
 * @since 2021/8/5
 */
public class BeanUtils {


    /**
     * 获取给定类的所有使用给定注释进行注释的方法。
     *
     * @param cls           要查询的 {@link Class}
     * @param annotationCls 必须存在于要匹配的方法上的 {@link Annotation}
     * @param ignoreAccess  确定是否应该考虑非公共方法
     * @return 方法列表（可能为空列表）。
     * @throws NullPointerException 如果类或注释类为 {@code null} 将抛出异常
     */
    public static List<Method> getMethodsListWithAnnotation(final Class<?> cls,
                                                            final Class<? extends Annotation> annotationCls,
                                                            final boolean ignoreAccess) {

        Assert.notNull(cls, "cls");
        Assert.notNull(annotationCls, "annotationCls");
        final List<Class<?>> classes = new ArrayList<>();
        classes.add(0, cls);
        final List<Method> annotatedMethods = new ArrayList<>();
        for (final Class<?> aCls : classes) {
            final Method[] methods = (ignoreAccess ? aCls.getDeclaredMethods() : aCls.getMethods());
            for (final Method method : methods) {
                if (method.getAnnotation(annotationCls) != null) {
                    annotatedMethods.add(method);
                }
            }
        }
        return annotatedMethods;
    }
}
