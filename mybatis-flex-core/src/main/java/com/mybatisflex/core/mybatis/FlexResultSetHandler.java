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

import com.mybatisflex.core.transaction.TransactionContext;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author michael
 * 用于增强对 Cursor 查询处理，以及 List<String> 的自动映射问题
 */
public class FlexResultSetHandler extends FlexDefaultResultSetHandler {

    public FlexResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler
        , ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
    }


    /**
     * 从写 handleCursorResultSets, 用于适配在事务下自动关闭 Cursor
     */
    @Override
    public <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException {
        Cursor<E> defaultCursor = super.handleCursorResultSets(stmt);

        //in transaction
        if (TransactionContext.getXID() != null) {
            return new FlexCursor<>(defaultCursor);
        }

        return defaultCursor;
    }


    /**
     * 修复当实体类中存在 List<String> 或者 List<Integer> 等自动映射出错的问题
     * 本质问题应该出现 mybatis 判断有误
     * <p>
     * https://gitee.com/mybatis-flex/mybatis-flex/issues/I7XBQS
     * https://gitee.com/mybatis-flex/mybatis-flex/issues/I7X7G7
     *
     * @param rsw
     * @param resultMap
     * @param columnPrefix
     * @throws SQLException
     */
    @Override
    protected Object createPrimitiveResultObject(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix)
        throws SQLException {
        final Class<?> resultType = resultMap.getType();
        if (!resultMap.getResultMappings().isEmpty()) {
            final List<ResultMapping> resultMappingList = resultMap.getResultMappings();
            final ResultMapping mapping = resultMappingList.get(0);
            String columnName = prependPrefix(mapping.getColumn(), columnPrefix);
            TypeHandler<?> typeHandler = mapping.getTypeHandler();

            Collection<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap, columnPrefix);
            if (columnName != null && mappedColumnNames.contains(columnName.toUpperCase(Locale.ENGLISH))) {
                return typeHandler.getResult(rsw.getResultSet(), columnName);
            }
            return null;
        } else {
            String columnName = rsw.getColumnNames().get(0);
            TypeHandler<?> typeHandler = rsw.getTypeHandler(resultType, columnName);
            return typeHandler.getResult(rsw.getResultSet(), columnName);
        }
    }


    static class FlexCursor<T> implements Cursor<T> {

        private final Cursor<T> originalCursor;

        public FlexCursor(Cursor<T> cursor) {
            this.originalCursor = cursor;
            TransactionContext.holdCursor(cursor);
        }

        @Override
        public void close() {
            // do nothing，由 TransactionContext 去关闭
        }

        @Override
        public boolean isOpen() {
            return originalCursor.isOpen();
        }

        @Override
        public boolean isConsumed() {
            return originalCursor.isConsumed();
        }

        @Override
        public int getCurrentIndex() {
            return originalCursor.getCurrentIndex();
        }

        @Override
        public Iterator<T> iterator() {
            return originalCursor.iterator();
        }

    }

}
