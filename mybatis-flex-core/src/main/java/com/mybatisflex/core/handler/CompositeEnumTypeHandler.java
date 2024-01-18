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

import com.mybatisflex.annotation.EnumValue;
import com.mybatisflex.core.util.ClassUtil;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeEnumTypeHandler<E extends Enum<E>> implements TypeHandler<E> {

    private final TypeHandler<E> delegate;

    public CompositeEnumTypeHandler(Class<E> enumClass) {
        boolean isNotFound = false;
        List<Field> enumDbValueFields = ClassUtil.getAllFields(enumClass, f -> f.getAnnotation(EnumValue.class) != null);
        if (enumDbValueFields.isEmpty()) {
            List<Method> enumDbValueMethods = ClassUtil.getAllMethods(enumClass, m -> m.getAnnotation(EnumValue.class) != null);
            if (enumDbValueMethods.isEmpty()) {
                isNotFound = true;
            }
        }
        if (isNotFound) {
            delegate = new EnumTypeHandler<>(enumClass);
        } else {
            delegate = new FlexEnumTypeHandler<>(enumClass);
        }
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        delegate.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getResult(ResultSet rs, String columnName) throws SQLException {
        return delegate.getResult(rs, columnName);
    }

    @Override
    public E getResult(ResultSet rs, int columnIndex) throws SQLException {
        return delegate.getResult(rs, columnIndex);
    }

    @Override
    public E getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return delegate.getResult(cs, columnIndex);
    }

}
