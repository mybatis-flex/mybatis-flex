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
package com.mybatisflex.core.mybatis.executor;

import com.mybatisflex.core.keygen.RowKeyGenerator;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.BatchExecutorException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlexBatchExecutor extends BatchExecutor implements CacheKeyBuilder {

    private final List<Statement> statementList = new ArrayList<>();
    private final List<BatchResult> batchResultList = new ArrayList<>();
    private String currentSql;
    private MappedStatement currentStatement;


    public FlexBatchExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return buildCacheKey(super.createCacheKey(ms, parameterObject, rowBounds, boundSql), parameterObject);
    }

    @Override
    public int doUpdate(MappedStatement ms, Object parameterObject) throws SQLException {
        final Configuration configuration = ms.getConfiguration();
        final StatementHandler handler = configuration.newStatementHandler(this, ms, parameterObject, RowBounds.DEFAULT, null, null);
        final BoundSql boundSql = handler.getBoundSql();
        final String sql = boundSql.getSql();
        final Statement stmt;
        if (sql.equals(currentSql) && ms.equals(currentStatement)) {
            int last = statementList.size() - 1;
            stmt = statementList.get(last);
            applyTransactionTimeout(stmt);
            handler.parameterize(stmt);// fix Issues 322
            BatchResult batchResult = batchResultList.get(last);
            batchResult.addParameterObject(parameterObject);
        } else {
            Connection connection = getConnection(ms.getStatementLog());
            stmt = handler.prepare(connection, transaction.getTimeout());
            handler.parameterize(stmt);    // fix Issues 322
            currentSql = sql;
            currentStatement = ms;
            statementList.add(stmt);
            batchResultList.add(new BatchResult(ms, sql, parameterObject));
        }
        handler.batch(stmt);
        return BATCH_UPDATE_RETURN_VALUE;
    }


    @Override
    public List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException {
        try {
            List<BatchResult> results = new ArrayList<>();
            if (isRollback) {
                return Collections.emptyList();
            }
            for (int i = 0, n = statementList.size(); i < n; i++) {
                Statement stmt = statementList.get(i);
                applyTransactionTimeout(stmt);
                BatchResult batchResult = batchResultList.get(i);
                try {
                    batchResult.setUpdateCounts(stmt.executeBatch());
                    MappedStatement ms = batchResult.getMappedStatement();
                    List<Object> parameterObjects = batchResult.getParameterObjects();
                    KeyGenerator keyGenerator = ms.getKeyGenerator();
                    if (Jdbc3KeyGenerator.class.equals(keyGenerator.getClass())) {
                        Jdbc3KeyGenerator jdbc3KeyGenerator = (Jdbc3KeyGenerator) keyGenerator;
                        jdbc3KeyGenerator.processBatch(ms, stmt, parameterObjects);
                    }
                    // 修复批量插入并设置主键时出错
                    // fixed https://gitee.com/mybatis-flex/mybatis-flex/issues/I6Y8ZU
                    else if (RowKeyGenerator.class.equals(keyGenerator.getClass())
                        && ((RowKeyGenerator) keyGenerator).hasGeneratedKeys()) {
                        keyGenerator.processAfter(this, ms, stmt, parameterObjects);
                    }
                    // issue #141
                    else if (!NoKeyGenerator.class.equals(keyGenerator.getClass())) {
                        for (Object parameter : parameterObjects) {
                            keyGenerator.processAfter(this, ms, stmt, parameter);
                        }
                    }
                    // Close statement to close cursor #1109
                    closeStatement(stmt);
                } catch (BatchUpdateException e) {
                    StringBuilder message = new StringBuilder();
                    message.append(batchResult.getMappedStatement().getId())
                        .append(" (batch index #")
                        .append(i + 1)
                        .append(")")
                        .append(" failed.");
                    if (i > 0) {
                        message.append(" ")
                            .append(i)
                            .append(" prior sub executor(s) completed successfully, but will be rolled back.");
                    }
                    throw new BatchExecutorException(message.toString(), e, results, batchResult);
                }
                results.add(batchResult);
            }
            return results;
        } finally {
            for (Statement stmt : statementList) {
                closeStatement(stmt);
            }
            currentSql = null;
            statementList.clear();
            batchResultList.clear();
        }
    }

}
