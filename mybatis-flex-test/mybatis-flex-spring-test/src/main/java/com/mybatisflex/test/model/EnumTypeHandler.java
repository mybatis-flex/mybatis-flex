package com.mybatisflex.test.model;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * 枚举类型处理器
 *
 * @author luozhan
 */
public class EnumTypeHandler<E extends Enum<E> & BaseEnum> extends BaseTypeHandler<E> {
    private final Class<E> enumClass;

    public EnumTypeHandler(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Integer value = parameter.getValue();
        ps.setObject(i, value);
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return getEnumByValue(value);
    }


    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return getEnumByValue(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return getEnumByValue(value);
    }

    /**
     * 根据枚举值获取枚举对象
     * @param value 枚举值
     * @return 枚举对象
     */
    private E getEnumByValue(int value) {
        return Stream.of(enumClass.getEnumConstants()).filter(e -> e.getValue() == value).findAny().orElse(null);
    }
}
