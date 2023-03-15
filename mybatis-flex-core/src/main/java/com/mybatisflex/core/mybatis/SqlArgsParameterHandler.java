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
package com.mybatisflex.core.mybatis;

import com.mybatisflex.core.FlexConsts;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class SqlArgsParameterHandler extends DefaultParameterHandler {

    private final Map parameterObject;


    public SqlArgsParameterHandler(MappedStatement mappedStatement, Map parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.parameterObject = parameterObject;
    }


    @Override
    public void setParameters(PreparedStatement ps) {
        try {
            doSetParameters(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void doSetParameters(PreparedStatement ps) throws SQLException {
        Object[] sqlArgs = (Object[]) ((Map<?, ?>) parameterObject).get(FlexConsts.SQL_ARGS);
        if (sqlArgs != null && sqlArgs.length > 0) {
            int index = 1;
            for (Object value : sqlArgs) {
                //通过配置的 TypeHandler 去设置内容
                if (value instanceof TypeHandlerObject) {
                    ((TypeHandlerObject) value).setParameter(ps, index++);
                }
                //在 Oracle、SqlServer 中 TIMESTAMP、DATE 类型的数据是支持 java.util.Date 给值的
                else if (value instanceof java.util.Date) {
                    setDateParameter(ps, (Date) value, index++);
                } else if (value instanceof byte[]) {
                    ps.setBytes(index++, (byte[]) value);
                } else {
                    /** 在 MySql，Oracle 等驱动中，通过 PreparedStatement.setObject 后，驱动会自动根据 value 内容进行转换
                     * 源码可参考： {{@link com.mysql.jdbc.PreparedStatement#setObject(int, Object)}
                     **/
                    ps.setObject(index++, value);
                }
            }
        } else {
            super.setParameters(ps);
        }
    }

    /**
     * Oracle、SqlServer 需要主动设置下 date 类型
     * MySql 通过 setObject 后会自动转换，具体查看 MySql 驱动源码
     *
     * @param ps    PreparedStatement
     * @param value date value
     * @param index set to index
     * @throws SQLException
     */
    private void setDateParameter(PreparedStatement ps, Date value, int index) throws SQLException {
        if (value instanceof java.sql.Date) {
            ps.setDate(index, (java.sql.Date) value);
        } else if (value instanceof java.sql.Timestamp) {
            ps.setTimestamp(index, (java.sql.Timestamp) value);
        } else {
            ps.setTimestamp(index, new java.sql.Timestamp(value.getTime()));
        }
    }
}
