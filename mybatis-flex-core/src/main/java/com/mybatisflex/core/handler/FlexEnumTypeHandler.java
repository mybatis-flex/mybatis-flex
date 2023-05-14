/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.handler;

import com.mybatisflex.annotation.EnumValue;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FlexEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<?> enumPropertyType;
    private final E[] enums;
    private Field property;
    private Method getter;

    public FlexEnumTypeHandler(Class<E> enumClass) {
        List<Field> allFields = ClassUtil.getAllFields(enumClass, field -> field.getAnnotation(EnumValue.class) != null);
        Field field = allFields.get(0);

        String fieldGetterName = "get" + StringUtil.firstCharToUpperCase(field.getName());
        List<Method> allMethods = ClassUtil.getAllMethods(enumClass, method -> {
            String methodName = method.getName();
            return methodName.equals(fieldGetterName);
        });

        enumPropertyType = ClassUtil.wrap(field.getType());
        enums = enumClass.getEnumConstants();

        if (allMethods.isEmpty()) {
            if (Modifier.isPublic(field.getModifiers())) {
                property = field;
            } else {
                throw new IllegalStateException("Can not find \"" + fieldGetterName + "()\" method in enum: " + enumClass.getName());
            }
        } else {
            getter = allMethods.get(0);
        }

    }


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object value = getValue(parameter);
        if (jdbcType == null) {
            ps.setObject(i, value);
        } else {
            ps.setObject(i, value, jdbcType.TYPE_CODE);
        }
    }


    private Object getValue(E object) {
        try {
            return getter != null
                    ? getter.invoke(object)
                    : property.get(object);
        } catch (Exception e) {
            throw FlexExceptions.wrap(e);
        }
    }


    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName, this.enumPropertyType);
        if (null == value && rs.wasNull()) {
            return null;
        }
        return convertToEnum(value);
    }


    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex, this.enumPropertyType);
        if (null == value && rs.wasNull()) {
            return null;
        }
        return convertToEnum(value);
    }


    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex, this.enumPropertyType);
        if (null == value && cs.wasNull()) {
            return null;
        }
        return convertToEnum(value);
    }


    private E convertToEnum(Object value) {
        for (E e : enums) {
            if (value.equals(getValue(e))) {
                return e;
            }
        }
        return null;
    }

}
