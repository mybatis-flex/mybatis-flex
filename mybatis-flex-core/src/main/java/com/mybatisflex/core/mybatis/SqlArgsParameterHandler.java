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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.exception.FlexExceptions;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

/**
 * 向 {@link PreparedStatement} 中的占位符设置值。
 *
 * @author michael
 * @author 王帅
 */
public class SqlArgsParameterHandler extends DefaultParameterHandler {

    private final TypeHandlerRegistry typeHandlerRegistry;

    public SqlArgsParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        try {
            doSetParameters(ps);
        } catch (SQLException e) {
            throw FlexExceptions.wrap(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void doSetParameters(PreparedStatement ps) throws SQLException {
        Object[] sqlArgs = (Object[]) ((Map) getParameterObject()).get(FlexConsts.SQL_ARGS);
        if (sqlArgs != null && sqlArgs.length > 0) {
            int index = 1;
            for (Object value : sqlArgs) {
                // 设置 NULL 值
                if (value == null) {
                    ps.setNull(index++, Types.NULL);
                    continue;
                }

                // 通过配置的 TypeHandler 去设置值
                if (value instanceof TypeHandlerObject) {
                    ((TypeHandlerObject) value).setParameter(ps, index++);
                    continue;
                }

                TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(value.getClass());
                if (typeHandler != null) {
                    // 通过对应的 TypeHandler 去设置值
                    typeHandler.setParameter(ps, index++, value, null);
                } else {
                    /*
                     * 在 MySql，Oracle 等驱动中，通过 PreparedStatement.setObject 后，驱动会自动根据 value 内容进行转换
                     * 源码可参考： {{@link com.mysql.jdbc.PreparedStatement#setObject(int, Object)}
                     */
                    ps.setObject(index++, value);
                }
            }
        } else {
            super.setParameters(ps);
        }
    }

}
