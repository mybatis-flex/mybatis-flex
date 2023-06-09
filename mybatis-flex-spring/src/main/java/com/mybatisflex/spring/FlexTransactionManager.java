/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.spring;

import com.mybatisflex.core.transaction.TransactionContext;
import com.mybatisflex.core.transaction.TransactionalManager;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class FlexTransactionManager extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() throws TransactionException {
        return new TransactionObject(TransactionContext.getXID());
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        TransactionObject transactionObject = (TransactionObject) transaction;
        return StringUtil.isNotBlank(transactionObject.prevXid);
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
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        TransactionObject transactionObject = (TransactionObject) status.getTransaction();
        TransactionalManager.commit(transactionObject.currentXid);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        TransactionObject transactionObject = (TransactionObject) status.getTransaction();
        TransactionalManager.rollback(transactionObject.currentXid);
    }


    static class TransactionObject {

        private final String prevXid;
        private String currentXid;

        public TransactionObject(String prevXid) {
            this.prevXid = prevXid;
        }
    }

}
