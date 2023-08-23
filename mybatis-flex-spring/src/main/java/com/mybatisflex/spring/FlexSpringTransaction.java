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

import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.transaction.TransactionContext;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * spring 事务支持，解决 issues: https://gitee.com/mybatis-flex/mybatis-flex/issues/I7HJ4J
 *
 * @author life
 * @author michael
 */
public class FlexSpringTransaction implements Transaction {

    private final FlexDataSource dataSource;
    private Boolean isConnectionTransactional;
    private Boolean autoCommit;
    private Connection connection;

    public FlexSpringTransaction(FlexDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (isConnectionTransactional == null) {
            connection = dataSource.getConnection();
            isConnectionTransactional = StringUtil.isNotBlank(TransactionContext.getXID());
            autoCommit = connection.getAutoCommit();
            return connection;
        }
        // 在事务中，通过 FlexDataSource 去获取
        // FlexDataSource 内部会进行 connection 缓存以及多数据源下的 key 判断
        else if (isConnectionTransactional) {
            return dataSource.getConnection();
        }
        // 非事务，返回当前链接
        else {
            return connection;
        }
    }

    @Override
    public void commit() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            this.connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            this.connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional) {
            connection.close();
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
