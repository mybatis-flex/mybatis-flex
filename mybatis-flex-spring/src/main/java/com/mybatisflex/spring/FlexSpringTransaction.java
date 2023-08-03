package com.mybatisflex.spring;

import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.transaction.TransactionContext;
import com.mybatisflex.core.transaction.TransactionalManager;
import com.mybatisflex.core.util.StringUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.util.StringUtils;

/**
 * spring事务支持，解决issusehttps://gitee.com/mybatis-flex/mybatis-flex/issues/I7HJ4J
 * @author life
 */
public class FlexSpringTransaction implements Transaction {

    DataSource dataSource;

    boolean isTransaction;

    TransactionIsolationLevel level;
    boolean autoCommit;


    public FlexSpringTransaction(DataSource dataSource, TransactionIsolationLevel level,
            boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource instanceof FlexDataSource) {
            return  dataSource.getConnection();
        }else{
            throw new SQLException("The datasource must be FlextDataSource");
        }


    }

    @Override
    public void commit() throws SQLException {
        getConnection().commit();
    }

    @Override
    public void rollback() throws SQLException {
        getConnection().rollback();
    }

    @Override
    public void close() throws SQLException {
        getConnection().close();
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return getConnection().getNetworkTimeout();
    }
}
