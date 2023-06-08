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
import org.apache.ibatis.cursor.defaults.DefaultCursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class FlexResultSetHandler extends DefaultResultSetHandler {

    public FlexResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
    }

    @Override
    public <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException {
        return new FlexCursor<>(super.handleCursorResultSets(stmt));
    }

    static class FlexCursor<T> extends DefaultCursor<T> {

        private Cursor originalCursor;

        public FlexCursor(Cursor cursor) {
            super(null, null, null, null);
            this.originalCursor = cursor;
            TransactionContext.holdCursor(cursor);
        }

        @Override
        public void close() {
            //非事务场景下，通过 releaseCursor 对 cursor 进行 close
            if (TransactionContext.getXID() == null) {
                TransactionContext.releaseCursor();
            }
            //else 在事务的场景下，由事务主动关闭
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
            try {
                return originalCursor.iterator();
            } catch (IllegalStateException e) {
                if (TransactionContext.getXID() == null) {
                    throw new IllegalStateException(e.getMessage() + " Cursor must use in transaction.");
                }
                throw e;
            }
        }
    }
}
