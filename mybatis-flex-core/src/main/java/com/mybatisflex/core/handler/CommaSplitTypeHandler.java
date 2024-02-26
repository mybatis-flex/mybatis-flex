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

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;


/**
 * @author michael
 */
public class CommaSplitTypeHandler implements TypeHandler<List<String>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null) {
            StringJoiner stringJoiner = new StringJoiner(",");
            parameter.forEach(stringJoiner::add);
            ps.setString(i, stringJoiner.toString());
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        String columnValueStr = rs.getString(columnName);
        if (columnValueStr != null) {
            return Arrays.asList(columnValueStr.split(","));
        }
        return null;
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValueStr = rs.getString(columnIndex);
        if (columnValueStr != null) {
            return Arrays.asList(columnValueStr.split(","));
        }
        return null;
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValueStr = cs.getString(columnIndex);
        if (columnValueStr != null) {
            return Arrays.asList(columnValueStr.split(","));
        }
        return null;
    }

}
