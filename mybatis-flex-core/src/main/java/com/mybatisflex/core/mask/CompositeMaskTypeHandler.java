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
package com.mybatisflex.core.mask;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompositeMaskTypeHandler implements TypeHandler<Object> {

    private final String maskType;
    private final TypeHandler<Object> typeHandler;

    public CompositeMaskTypeHandler(String maskType, TypeHandler<Object> typeHandler) {
        this.maskType = maskType;
        this.typeHandler = typeHandler;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        typeHandler.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        return MaskManager.mask(maskType, typeHandler.getResult(rs, columnName));
    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        return MaskManager.mask(maskType, typeHandler.getResult(rs, columnIndex));
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return MaskManager.mask(maskType, typeHandler.getResult(cs, columnIndex));
    }
}
