package com.mybatisflex.solon.transaction;

import com.mybatisflex.core.datasource.FlexDataSourceRouting;
import org.apache.ibatis.transaction.Transaction;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * @author noear
 * @since 1.6
 */
public class SolonManagedTransaction implements Transaction {
    private DataSource dataSource;
    private Connection connection;

    public SolonManagedTransaction(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            DataSource selected = FlexDataSourceRouting.determineCurrentTarget(dataSource);
            connection = TranUtils.getConnectionProxy(selected);
        }

        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        if (connection != null) {
            try {
                return connection.getNetworkTimeout();
            } catch (SQLFeatureNotSupportedException e) {
                //有些驱动不支持这个特性
            }
        }

        return null;
    }
}
