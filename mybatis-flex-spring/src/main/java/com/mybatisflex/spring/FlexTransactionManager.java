/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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

import com.mybatisflex.core.transaction.TransactionContext;
import com.mybatisflex.core.transaction.TransactionalManager;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * MyBatis-Flex 事务支持。
 *
 * @author michael
 */
public class FlexTransactionManager extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() throws TransactionException {
        return new TransactionObject(TransactionContext.getXID());
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        TransactionObject transactionObject = (TransactionObject) transaction;
        return StringUtil.hasText(transactionObject.prevXid);
    }

    @Override
    protected Object doSuspend(Object transaction) throws TransactionException {
        TransactionContext.release();
        TransactionObject transactionObject = (TransactionObject) transaction;
        return transactionObject.prevXid;
    }

    @Override
    protected void doResume(Object transaction, Object suspendedResources) throws TransactionException {
        String xid = (String) suspendedResources;
        TransactionContext.holdXID(xid);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        TransactionObject transactionObject = (TransactionObject) transaction;
        transactionObject.currentXid = TransactionalManager.startTransactional();

        TimeoutHolder.hold(definition);
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        TransactionObject transactionObject = (TransactionObject) status.getTransaction();
        TransactionalManager.commit(transactionObject.currentXid);
        transactionObject.clear();
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        TransactionObject transactionObject = (TransactionObject) status.getTransaction();
        TransactionalManager.rollback(transactionObject.currentXid);
        transactionObject.clear();
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        // 在多个事务嵌套时，子事务的传递方式为 REQUIRED（加入当前事务）
        // 那么，当子事务抛出异常时，会调当前方法，而不是直接调用 doRollback
        // 此时，需要标识 prevXid 进行 Rollback
        TransactionObject transactionObject = (TransactionObject) status.getTransaction();
        transactionObject.setRollbackOnly();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        TimeoutHolder.clear();
    }

    public static class TransactionObject extends JdbcTransactionObjectSupport {

        private static final ThreadLocal<String> ROLLBACK_ONLY_XIDS = new ThreadLocal<>();

        private final String prevXid;
        private String currentXid;

        public TransactionObject(String prevXid) {
            this.prevXid = prevXid;
        }

        public void setRollbackOnly() {
            ROLLBACK_ONLY_XIDS.set(prevXid);
        }

        public void clear() {
            ROLLBACK_ONLY_XIDS.remove();
        }

        @Override
        public boolean isRollbackOnly() {
            return currentXid != null && currentXid.equals(ROLLBACK_ONLY_XIDS.get());
        }
    }

}
