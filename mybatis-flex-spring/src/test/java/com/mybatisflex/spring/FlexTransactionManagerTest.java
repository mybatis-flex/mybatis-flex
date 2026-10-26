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

import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.transaction.TransactionContext;
import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Savepoint;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author kk
 */
public class FlexTransactionManagerTest {

    @After
    public void cleanup() {
        TransactionContext.release();
    }

    @Test
    public void shouldEnableSpringNestedTransactionsByDefault() {
        FlexTransactionManager transactionManager = new FlexTransactionManager();
        assertTrue(transactionManager.isNestedTransactionAllowed());
        RecordingConnection recording = new RecordingConnection();
        FlexDataSource dataSource = new FlexDataSource("primary",
            new SingleConnectionDataSource(recording.connection, false), DbType.MYSQL, false);

        TransactionTemplate outer = new TransactionTemplate(transactionManager);
        TransactionTemplate nested = new TransactionTemplate(transactionManager);
        nested.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

        String result = outer.execute(status -> {
            String outerXid = TransactionContext.getXID();
            getConnection(dataSource);
            try {
                nested.execute(nestedStatus -> {
                    assertEquals(outerXid, TransactionContext.getXID());
                    throw new IllegalStateException("nested failure");
                });
                fail("嵌套事务应该继续抛出业务异常");
            } catch (IllegalStateException expected) {
                assertEquals("nested failure", expected.getMessage());
            }
            assertEquals(outerXid, TransactionContext.getXID());
            return "outer success";
        });

        assertEquals("outer success", result);
        assertNull(TransactionContext.getXID());
        assertOrdered(recording.events, "setSavepoint:1", "rollbackTo:1", "releaseSavepoint:1", "commit", "close");
        assertFalse(recording.events.contains("rollback"));
    }

    @Test
    public void shouldSupportProgrammaticTransactionStatusSavepoints() {
        FlexTransactionManager transactionManager = new FlexTransactionManager();
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        template.execute(status -> {
            Object savepoint = status.createSavepoint();
            status.rollbackToSavepoint(savepoint);
            status.releaseSavepoint(savepoint);
            return null;
        });

        assertNull(TransactionContext.getXID());
    }

    @Test
    public void shouldRejectProgrammaticSavepointsWhenNestedTransactionsAreDisabled() {
        FlexTransactionManager transactionManager = new FlexTransactionManager();
        transactionManager.setNestedTransactionAllowed(false);
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        template.execute(status -> {
            try {
                status.createSavepoint();
                fail("显式关闭嵌套事务后不应允许创建保存点");
            } catch (NestedTransactionNotSupportedException expected) {
                assertEquals("Transaction manager does not allow nested transactions", expected.getMessage());
            }
            return null;
        });

        assertNull(TransactionContext.getXID());
    }

    private static Connection getConnection(FlexDataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void assertOrdered(List<String> events, String... expected) {
        int previousIndex = -1;
        for (String event : expected) {
            int index = events.indexOf(event);
            assertTrue("事件缺失或顺序错误: " + event + ", 实际事件: " + events, index > previousIndex);
            previousIndex = index;
        }
    }

    private static class RecordingConnection implements InvocationHandler {
        private final List<String> events = new ArrayList<>();
        private final Connection connection = (Connection) Proxy.newProxyInstance(
            FlexTransactionManagerTest.class.getClassLoader(), new Class[]{Connection.class}, this);
        private int savepointSequence;
        private boolean autoCommit = true;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String methodName = method.getName();
            switch (methodName) {
                case "setAutoCommit":
                    autoCommit = (Boolean) args[0];
                    return null;
                case "getAutoCommit":
                    return autoCommit;
                case "setSavepoint":
                    RecordingSavepoint savepoint = new RecordingSavepoint(++savepointSequence);
                    events.add("setSavepoint:" + savepoint.id);
                    return savepoint;
                case "rollback":
                    if (args == null || args.length == 0) {
                        events.add("rollback");
                    } else {
                        events.add("rollbackTo:" + ((RecordingSavepoint) args[0]).id);
                    }
                    return null;
                case "releaseSavepoint":
                    events.add("releaseSavepoint:" + ((RecordingSavepoint) args[0]).id);
                    return null;
                case "commit":
                case "close":
                    events.add(methodName);
                    return null;
                case "isClosed":
                    return false;
                case "unwrap":
                    return proxy;
                case "isWrapperFor":
                    return false;
                case "toString":
                    return "RecordingConnection";
                default:
                    return defaultValue(method.getReturnType());
            }
        }

        private static Object defaultValue(Class<?> returnType) {
            if (!returnType.isPrimitive()) {
                return null;
            }
            if (returnType == boolean.class) {
                return false;
            }
            if (returnType == char.class) {
                return '\0';
            }
            return 0;
        }
    }

    private static class RecordingSavepoint implements Savepoint {
        private final int id;

        private RecordingSavepoint(int id) {
            this.id = id;
        }

        @Override
        public int getSavepointId() {
            return id;
        }

        @Override
        public String getSavepointName() {
            return "savepoint-" + id;
        }
    }
}
