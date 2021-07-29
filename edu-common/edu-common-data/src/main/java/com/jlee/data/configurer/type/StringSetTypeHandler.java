package com.jlee.data.configurer.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Set;

/**
 * 包含 String 的 Set 集合 与数据库 VARCHAR 类型互转处理(默认以 `,` 分隔)
 */
@MappedTypes({Set.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class StringSetTypeHandler extends AbstractSetTypeHandler<String> {
    public StringSetTypeHandler() {
    }

    /**
     * 数据库中 VARCHAR 转 java String
     *
     * @param value 数据库中的数据
     * @return java 对应类型的数据
     */
    @Override
    protected String parse(String value) {
        return value;
    }
}