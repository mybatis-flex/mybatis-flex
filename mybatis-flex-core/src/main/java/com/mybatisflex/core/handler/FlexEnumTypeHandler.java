/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.core.handler;

import com.mybatisflex.core.util.EnumWrapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlexEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private EnumWrapper<E> enumWrapper;

    public FlexEnumTypeHandler(Class<E> enumClass) {
        enumWrapper = EnumWrapper.of(enumClass);
    }


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object value = enumWrapper.getEnumValue(parameter);
        if (jdbcType == null) {
            ps.setObject(i, value);
        } else {
            ps.setObject(i, value, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName, enumWrapper.getPropertyType());
        if (null == value && rs.wasNull()) {
            return null;
        }
        return enumWrapper.getEnum(value);
    }


    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex, enumWrapper.getPropertyType());
        if (null == value && rs.wasNull()) {
            return null;
        }
        return enumWrapper.getEnum(value);
    }


    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex, enumWrapper.getPropertyType());
        if (null == value && cs.wasNull()) {
            return null;
        }
        return enumWrapper.getEnum(value);
    }


}
