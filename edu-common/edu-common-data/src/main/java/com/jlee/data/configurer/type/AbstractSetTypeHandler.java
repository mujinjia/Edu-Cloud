package com.jlee.data.configurer.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * set 集合 与数据库 VARCHAR 类型互转抽象处理器(`,` 分隔)，根据 Set 集合中具体的类型去继承该方法，复写 String 转 对应类型 的方法
 * 虽然 json 序列化也能将集合序列化为字符串,但格式是 “[xxx,xxx,xxx]” 该 Handler 是让序列化后的字符串格式为 “,xxx,xxx,xxx,” 这样有利于 sql 中 like “,%xxx%,” 的匹配
 *
 * @param <T> 集合中的元素类型
 */
@MappedJdbcTypes({JdbcType.VARCHAR})
public abstract class AbstractSetTypeHandler<T> extends BaseTypeHandler<Set<T>> {
    /**
     * 默认的分隔符
     */
    private final String delimiter;
    /**
     * 存入数据库的默认值
     */
    private final String defaultValue;

    protected AbstractSetTypeHandler() {
        this(",", null);
    }

    protected AbstractSetTypeHandler(String delimiter, String defaultValue) {
        this.delimiter = delimiter;
        this.defaultValue = defaultValue;
    }

    /**
     * 将java类型转换为jdbc类型向数据库中存储
     *
     * @param preparedStatement 预编译 SQL 对象
     * @param i                 在 sql 语句中预留字段的位置
     * @param sets              java类型对象
     * @param jdbcType          要转换的jdbc类型
     * @throws SQLException sql异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Set<T> sets, JdbcType jdbcType) throws SQLException {
        String value = CollectionUtils.isEmpty(sets) ? null : sets.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(this.delimiter));
        preparedStatement.setString(i, !StringUtils.hasLength(value) ? this.defaultValue : String.format("%s%s%s", this.delimiter, value, this.delimiter));
    }

    /**
     * 将jdbc类型转java类型放入实体类
     *
     * @param resultSet 从数据库中查询的结果集
     * @param s         字段名
     * @return java类型的数据
     * @throws SQLException sql异常
     */
    @Override
    public Set<T> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String value = resultSet.getString(s);
        return this.convert(value);
    }

    /**
     * 将jdbc类型转java类型放入实体类
     *
     * @param resultSet 从数据库中查询的结果集
     * @param i         字段位置
     * @return java类型的数据
     * @throws SQLException sql异常
     */
    @Override
    public Set<T> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String value = resultSet.getString(i);
        return this.convert(value);
    }

    @Override
    public Set<T> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String value = callableStatement.getString(i);
        return this.convert(value);
    }


    /**
     * 类型转换 将 数据库中的字段转 Set 集合
     *
     * @param value 数据库中获取的数据
     * @return Set 集合数据 至少会返回一个空集合,而不是 null
     */
    private Set<T> convert(String value) {
        return StringUtils.hasLength(value) ? Arrays.stream(value.split(this.delimiter)).filter(StringUtils::hasLength).map(this::parse).filter(Objects::nonNull).collect(Collectors.toSet()) : Collections.emptySet();
    }


    /**
     * String 转 java 类型
     *
     * @param var1 数据库中获取的数据
     * @return java中对应类型的数据
     */
    protected abstract T parse(String var1);
}