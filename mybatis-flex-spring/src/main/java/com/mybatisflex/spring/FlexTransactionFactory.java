package com.mybatisflex.spring;

import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

/**
 * @author life
 */
public class FlexTransactionFactory extends SpringManagedTransactionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new FlexSpringTransaction(dataSource,level,autoCommit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction newTransaction(Connection conn) {
        throw new UnsupportedOperationException("New Spring transactions require a DataSource");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Properties props) {
        // not needed in this version
    }
}
