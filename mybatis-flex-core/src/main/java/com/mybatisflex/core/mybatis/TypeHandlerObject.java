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
package com.mybatisflex.core.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TypeHandlerObject implements Serializable {

    private final TypeHandler typeHandler;
    private final Object value;
    private final JdbcType jdbcType;

    public TypeHandlerObject(TypeHandler typeHandler, Object value, JdbcType jdbcType) {
        this.typeHandler = typeHandler;
        this.value = value;
        this.jdbcType = jdbcType;
    }

    public void setParameter(PreparedStatement ps, int i) throws SQLException {
        typeHandler.setParameter(ps, i, value, jdbcType);
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TypeHandlerObject{"
            + "value=" + value
            + ", typeHandler=" + typeHandler.getClass().getSimpleName()
            + '}';
    }

}
