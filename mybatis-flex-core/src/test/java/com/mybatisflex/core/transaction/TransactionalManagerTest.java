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
package com.mybatisflex.core.transaction;

import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.dialect.DbType;
import org.junit.After;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Savepoint;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author kk
 */
public class TransactionalManagerTest {

    @After
    public void cleanup() {
        DataSourceKey.forceClear();
        TransactionContext.release();
    }

    @Test
    public void shouldRollbackNestedTransactionToSavepointAndCommitOuterTransaction() {
        RecordingConnection recording = new RecordingConnection();
        FlexDataSource dataSource = createFlexDataSource("primary", recording);

        String result = TransactionalManager.exec(() -> {
            getConnection(dataSource);
            try {
                TransactionalManager.exec(() -> {
                    throw new IllegalStateException("nested failure");
                }, Propagation.NESTED, null);
                fail("嵌套事务应该继续抛出业务异常");
            } catch (TransactionException expected) {
                assertTrue(expected.getCause() instanceof IllegalStateException);
            }
            return "outer success";
        }, Propagation.REQUIRED, null);

        assertEquals("outer success", result);
        assertOrdered(recording.events, "setSavepoint:1", "rollbackTo:1", "releaseSavepoint:1", "commit", "close");
        assertFalse(recording.events.contains("rollback"));
    }

    @Test
    public void shouldCreateSavepointForConnectionsAcquiredAfterNestedTransactionStarts() {
        RecordingConnection primary = new RecordingConnection();
        RecordingConnection secondary = new RecordingConnection();
        FlexDataSource dataSource = createFlexDataSource("primary", primary);
        dataSource.addDataSource("secondary", createDataSource(secondary), DbType.MYSQL, false);

        TransactionalManager.exec(() -> {
            Boolean nestedResult = TransactionalManager.exec(() -> {
                getConnection(dataSource);
                DataSourceKey.use("secondary", () -> getConnection(dataSource));
                return false;
            }, Propagation.NESTED, (result, error) -> !result);
            assertFalse(nestedResult);
            return true;
        }, Propagation.REQUIRED, null);

        assertOrdered(primary.events, "setSavepoint:1", "rollbackTo:1", "releaseSavepoint:1", "commit");
        assertOrdered(secondary.events, "setSavepoint:1", "rollbackTo:1", "releaseSavepoint:1", "commit");
    }

    @Test
    public void shouldRespectRollbackDeciderWhenNestedSupplierThrows() {
        RecordingConnection recording = new RecordingConnection();
        FlexDataSource dataSource = createFlexDataSource("primary", recording);

        TransactionalManager.exec(() -> {
            getConnection(dataSource);
            try {
                TransactionalManager.exec(() -> {
                    throw new IllegalStateException("commit nested result");
                }, Propagation.NESTED, (result, error) -> false);
                fail("判定为不回滚时仍应继续抛出业务异常");
            } catch (TransactionException expected) {
                assertTrue(expected.getCause() instanceof IllegalStateException);
            }
            return true;
        }, Propagation.REQUIRED, null);

        assertOrdered(recording.events, "setSavepoint:1", "releaseSavepoint:1", "commit");
        assertFalse(recording.events.contains("rollbackTo:1"));
    }

    @Test
    public void shouldApplyExplicitSavepointToEveryDataSourceAndRejectItAfterCompletion() {
        RecordingConnection primary = new RecordingConnection();
        RecordingConnection secondary = new RecordingConnection();
        FlexDataSource dataSource = createFlexDataSource("primary", primary);
        dataSource.addDataSource("secondary", createDataSource(secondary), DbType.MYSQL, false);
        Object[] completedSavepoint = new Object[1];
        String[] completedXid = new String[1];

        TransactionalManager.exec(() -> {
            completedXid[0] = TransactionContext.getXID();
            getConnection(dataSource);
            completedSavepoint[0] = TransactionalManager.createSavepoint(completedXid[0]);
            DataSourceKey.use("secondary", () -> getConnection(dataSource));
            TransactionalManager.rollbackToSavepoint(completedXid[0], completedSavepoint[0]);
            TransactionalManager.releaseSavepoint(completedXid[0], completedSavepoint[0]);
            return true;
        }, Propagation.REQUIRED, null);

        assertOrdered(primary.events, "setSavepoint:1", "rollbackTo:1", "releaseSavepoint:1");
        assertOrdered(secondary.events, "setSavepoint:1", "rollbackTo:1", "releaseSavepoint:1");
        try {
            TransactionalManager.releaseSavepoint(completedXid[0], completedSavepoint[0]);
            fail("事务完成后不应保留保存点状态");
        } catch (TransactionException expected) {
            assertTrue(expected.getMessage().contains("No active transaction"));
        }
    }

    @Test
    public void shouldReleaseYoungerSavepointsBeforeRollingBackOlderSavepoint() {
        RecordingConnection recording = new RecordingConnection();
        FlexDataSource dataSource = createFlexDataSource("primary", recording);

        TransactionalManager.exec(() -> {
            String xid = TransactionContext.getXID();
            getConnection(dataSource);
            Object older = TransactionalManager.createSavepoint(xid);
            TransactionalManager.createSavepoint(xid);
            TransactionalManager.rollbackToSavepoint(xid, older);
            TransactionalManager.releaseSavepoint(xid, older);
            return true;
        }, Propagation.REQUIRED, null);

        assertOrdered(recording.events, "setSavepoint:1", "setSavepoint:2",
            "releaseSavepoint:2", "rollbackTo:1", "releaseSavepoint:1", "commit");
    }

    @Test
    public void shouldCloseConnectionWhenDelayedSavepointCreationFails() {
        RecordingConnection recording = new RecordingConnection();
        recording.failSavepointCreation = true;
        FlexDataSource dataSource = createFlexDataSource("primary", recording);

        TransactionalManager.exec(() -> {
            String xid = TransactionContext.getXID();
            Object savepoint = TransactionalManager.createSavepoint(xid);
            try {
                getConnection(dataSource);
                fail("保存点补建失败时应该拒绝返回连接");
            } catch (TransactionException expected) {
                assertTrue(expected.getCause() instanceof SQLException);
            }
            TransactionalManager.releaseSavepoint(xid, savepoint);
            return true;
        }, Propagation.REQUIRED, null);

        assertOrdered(recording.events, "setAutoCommit:false", "setSavepoint:failed", "setAutoCommit:true", "close");
        assertFalse(recording.events.contains("commit"));
    }

    @Test
    public void shouldIgnoreUnsupportedExplicitSavepointRelease() {
        RecordingConnection recording = new RecordingConnection();
        recording.failSavepointReleaseUnsupported = true;
        FlexDataSource dataSource = createFlexDataSource("primary", recording);

        String result = TransactionalManager.exec(() -> {
            getConnection(dataSource);
            return TransactionalManager.exec(() -> "nested success", Propagation.NESTED, null);
        }, Propagation.REQUIRED, null);

        assertEquals("nested success", result);
        assertOrdered(recording.events, "setSavepoint:1", "releaseSavepoint:unsupported", "commit", "close");
        assertFalse(recording.events.contains("rollback"));
    }

    private static FlexDataSource createFlexDataSource(String key, RecordingConnection recording) {
        return new FlexDataSource(key, createDataSource(recording), DbType.MYSQL, false);
    }

    private static DataSource createDataSource(RecordingConnection recording) {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return recording.connection;
            }

            @Override
            public Connection getConnection(String username, String password) {
                return recording.connection;
            }

            @Override
            public PrintWriter getLogWriter() {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) {
            }

            @Override
            public void setLoginTimeout(int seconds) {
            }

            @Override
            public int getLoginTimeout() {
                return 0;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                throw new SQLFeatureNotSupportedException();
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new SQLException("Not a wrapper");
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) {
                return false;
            }
        };
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
            TransactionalManagerTest.class.getClassLoader(), new Class[]{Connection.class}, this);
        private int savepointSequence;
        private boolean autoCommit = true;
        private boolean failSavepointCreation;
        private boolean failSavepointReleaseUnsupported;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            switch (methodName) {
                case "setAutoCommit":
                    autoCommit = (Boolean) args[0];
                    events.add("setAutoCommit:" + autoCommit);
                    return null;
                case "getAutoCommit":
                    return autoCommit;
                case "setSavepoint":
                    if (failSavepointCreation) {
                        events.add("setSavepoint:failed");
                        throw new SQLException("savepoint failure");
                    }
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
                    if (failSavepointReleaseUnsupported) {
                        events.add("releaseSavepoint:unsupported");
                        throw new SQLFeatureNotSupportedException("savepoint release not supported");
                    }
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
