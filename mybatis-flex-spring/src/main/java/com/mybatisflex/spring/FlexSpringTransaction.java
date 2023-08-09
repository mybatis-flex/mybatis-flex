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
package com.mybatisflex.spring;

import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.transaction.TransactionContext;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * spring 事务支持，解决 issues https://gitee.com/mybatis-flex/mybatis-flex/issues/I7HJ4J
 *
 * @author life
 */
public class FlexSpringTransaction implements Transaction {

    private final FlexDataSource dataSource;
    private final Map<String, Connection> connectionMap = new HashMap<>();

    private boolean isTransaction = false;
    private boolean autoCommit;


    public FlexSpringTransaction(FlexDataSource dataSource, boolean autoCommit) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String dataSourceKey = DataSourceKey.get();
        if (StringUtil.isBlank(dataSourceKey)) {
            dataSourceKey = dataSource.getDefaultDataSourceKey();
        }

        Connection connection = connectionMap.get(dataSourceKey);
        if (connection == null) {
            connection = this.dataSource.getConnection();
            connectionMap.put(dataSourceKey, connection);
        }

        this.autoCommit = connection.getAutoCommit();

        if (TransactionContext.getXID() != null) {
            this.isTransaction = true;
        }

        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (!isTransaction && !autoCommit) {
            getConnection().commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (!isTransaction && !autoCommit) {
            getConnection().rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        getConnection().close();
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
