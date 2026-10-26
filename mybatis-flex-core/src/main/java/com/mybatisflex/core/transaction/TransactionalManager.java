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

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.sql.Connection;
import java.sql.Savepoint;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 事务管理器
 */
public class TransactionalManager {

    private TransactionalManager() {
    }

    private static final Log log = LogFactory.getLog(TransactionalManager.class);

    //<xid : <dataSourceKey : connection>>
    private static final ThreadLocal<Map<String, Map<String, Connection>>> CONNECTION_HOLDER
        = ThreadLocal.withInitial(LinkedHashMap::new);

    //<xid : savepoints>
    private static final ThreadLocal<Map<String, List<TransactionSavepoint>>> SAVEPOINT_HOLDER
        = ThreadLocal.withInitial(LinkedHashMap::new);


    public static void hold(String xid, String ds, Connection connection) {
        Map<String, Map<String, Connection>> holdMap = CONNECTION_HOLDER.get();
        Map<String, Connection> connMap = holdMap.get(xid);
        if (connMap == null) {
            connMap = new LinkedHashMap<>();
            holdMap.put(xid, connMap);
        }

        if (connMap.containsKey(ds)) {
            return;
        }

        List<TransactionSavepoint> savepoints = SAVEPOINT_HOLDER.get().get(xid);
        if (savepoints == null || savepoints.isEmpty()) {
            connMap.put(ds, connection);
            return;
        }

        List<TransactionSavepoint> initialized = new ArrayList<>();
        try {
            for (TransactionSavepoint savepoint : savepoints) {
                savepoint.savepoints.put(ds, connection.setSavepoint());
                initialized.add(savepoint);
            }
            connMap.put(ds, connection);
        } catch (SQLException e) {
            releaseInitializedSavepoints(ds, connection, initialized, e);
            throw new TransactionException("Could not create JDBC savepoint for data source: " + ds, e);
        }
    }


    //    public static <T> T exec(Supplier<T> supplier, Propagation propagation, boolean withResult) {
    public static <T> T exec(Supplier<T> supplier, Propagation propagation, RollbackDecider<T> decider) {
        //上一级事务的id，支持事务嵌套
        String currentXID = TransactionContext.getXID();
        try {
            switch (propagation) {
                //若存在当前事务，则加入当前事务，若不存在当前事务，则创建新的事务
                case REQUIRED:
                    if (currentXID != null) {
                        return supplier.get();
                    } else {
                        return execNewTransactional(supplier, decider);
                    }


                    //若存在当前事务，则加入当前事务，若不存在当前事务，则已非事务的方式运行
                case SUPPORTS:
                    return supplier.get();


                //若存在当前事务，则加入当前事务，若不存在当前事务，则抛出异常
                case MANDATORY:
                    if (currentXID != null) {
                        return supplier.get();
                    } else {
                        throw new TransactionException("No existing transaction found for transaction marked with propagation 'mandatory'");
                    }


                    //始终以新事务的方式运行，若存在当前事务，则暂停（挂起）当前事务。
                case REQUIRES_NEW:
                    return execNewTransactional(supplier, decider);


                //若存在当前事务，则通过保存点创建嵌套事务，否则创建新的事务
                case NESTED:
                    if (currentXID != null) {
                        return execNestedTransactional(currentXID, supplier, decider);
                    } else {
                        return execNewTransactional(supplier, decider);
                    }


                //以非事务的方式运行，若存在当前事务，则暂停（挂起）当前事务。
                case NOT_SUPPORTED:
                    if (currentXID != null) {
                        TransactionContext.release();
                    }
                    return supplier.get();


                //以非事务的方式运行，若存在当前事务，则抛出异常。
                case NEVER:
                    if (currentXID != null) {
                        throw new TransactionException("Existing transaction found for transaction marked with propagation 'never'");
                    }
                    return supplier.get();


                default:
                    throw new TransactionException("Unsupported transaction propagation: " + propagation);

            }
        } finally {
            //恢复上一级事务
            if (currentXID != null) {
                TransactionContext.holdXID(currentXID);
            }
        }
    }

//    private static <T> T execNewTransactional(Supplier<T> supplier, boolean withResult) {
//        String xid = startTransactional();
//        T result = null;
//        boolean isRollback = false;
//        try {
//            result = supplier.get();
//        } catch (Throwable e) {
//            isRollback = true;
//            rollback(xid);
//            throw new TransactionException(e.getMessage(), e);
//        } finally {
//            if (!isRollback) {
//                if (!withResult) {
//                    if (result instanceof Boolean && (Boolean) result) {
//                        commit(xid);
//                    }
//                    //null or false
//                    else {
//                        rollback(xid);
//                    }
//                } else {
//                    commit(xid);
//                }
//            }
//        }
//        return result;
//    }

    private static <T> T execNewTransactional(Supplier<T> supplier, RollbackDecider<T> decider) {
        String xid = startTransactional();
        T result = null;
        Throwable error = null;

        try {
            result = supplier.get();
        } catch (Throwable e) {
            error = e;
        }

        boolean rollback;
        try {
            if (decider != null) {
                rollback = decider.shouldRollback(result, error);
            } else {
                // 默认行为（兼容旧逻辑）
                rollback = error != null;
            }
        } catch (Throwable deciderEx) {
            // 判官自己出问题 -> 强制回滚
            rollback = true;
            log.error("Transaction decider error", deciderEx);
        }

        if (rollback) {
            rollback(xid);
        } else {
            commit(xid);
        }

        // 异常继续抛
        if (error != null) {
            throw new TransactionException(error.getMessage(), error);
        }

        return result;
    }

    private static <T> T execNestedTransactional(String xid, Supplier<T> supplier, RollbackDecider<T> decider) {
        Object savepoint = createSavepoint(xid);
        T result = null;
        Throwable error = null;

        try {
            result = supplier.get();
        } catch (Throwable e) {
            error = e;
        }

        boolean rollback = shouldRollback(result, error, decider);
        Throwable completionError = null;
        try {
            if (rollback) {
                rollbackToSavepoint(xid, savepoint);
            }
        } catch (Throwable e) {
            completionError = e;
        }

        try {
            releaseSavepoint(xid, savepoint);
        } catch (Throwable e) {
            if (completionError == null) {
                completionError = e;
            } else {
                completionError.addSuppressed(e);
            }
        }

        if (error != null) {
            if (completionError != null) {
                error.addSuppressed(completionError);
            }
            throw new TransactionException(error.getMessage(), error);
        }
        if (completionError != null) {
            throw new TransactionException("Could not complete nested transaction", completionError);
        }
        return result;
    }

    private static <T> boolean shouldRollback(T result, Throwable error, RollbackDecider<T> decider) {
        if (decider == null) {
            return error != null;
        }
        try {
            return decider.shouldRollback(result, error);
        } catch (Throwable deciderEx) {
            log.error("Transaction decider error", deciderEx);
            return true;
        }
    }


    public static Connection getConnection(String xid, String ds) {
        Map<String, Connection> connections = CONNECTION_HOLDER.get().get(xid);
        return connections == null || connections.isEmpty() ? null : connections.get(ds);
    }


    public static String startTransactional() {
        String xid = UUID.randomUUID().toString();
        CONNECTION_HOLDER.get().put(xid, new LinkedHashMap<>());
        SAVEPOINT_HOLDER.get().put(xid, new ArrayList<>());
        TransactionContext.holdXID(xid);
        return xid;
    }

    /**
     * 为当前事务创建保存点。已持有的每个数据源连接都会创建对应的 JDBC 保存点。
     *
     * @param xid 事务 ID
     * @return 事务级保存点
     */
    public static Object createSavepoint(String xid) {
        assertActiveTransaction(xid);
        TransactionSavepoint savepoint = new TransactionSavepoint();
        Map<String, Connection> connections = CONNECTION_HOLDER.get().get(xid);
        try {
            for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                savepoint.savepoints.put(entry.getKey(), entry.getValue().setSavepoint());
            }
            SAVEPOINT_HOLDER.get().get(xid).add(savepoint);
            return savepoint;
        } catch (SQLException e) {
            releaseCreatedSavepoints(connections, savepoint, e);
            throw new TransactionException("Could not create JDBC savepoint", e);
        }
    }

    /**
     * 将当前事务中的所有数据源连接回滚到指定保存点。
     *
     * @param xid 事务 ID
     * @param savepoint {@link #createSavepoint(String)} 返回的保存点
     */
    public static void rollbackToSavepoint(String xid, Object savepoint) {
        int savepointIndex = findSavepointIndex(xid, savepoint);
        List<TransactionSavepoint> savepoints = SAVEPOINT_HOLDER.get().get(xid);
        TransactionSavepoint target = savepoints.get(savepointIndex);
        Map<String, Connection> connections = CONNECTION_HOLDER.get().get(xid);
        SQLException exception = null;

        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            for (int i = savepoints.size() - 1; i > savepointIndex; i--) {
                Savepoint nestedSavepoint = savepoints.get(i).savepoints.get(entry.getKey());
                if (nestedSavepoint == null) {
                    continue;
                }
                try {
                    entry.getValue().releaseSavepoint(nestedSavepoint);
                } catch (SQLException e) {
                    exception = mergeException(exception, e);
                }
            }

            Savepoint jdbcSavepoint = target.savepoints.get(entry.getKey());
            if (jdbcSavepoint == null) {
                continue;
            }
            try {
                entry.getValue().rollback(jdbcSavepoint);
            } catch (SQLException e) {
                exception = mergeException(exception, e);
            }
        }

        removeSavepointsAfter(savepoints, savepointIndex);
        if (exception != null) {
            throw new TransactionException("Could not roll back to JDBC savepoint", exception);
        }
    }

    /**
     * 释放当前事务中的指定保存点。若指定保存点后还存在子保存点，则一并释放。
     *
     * @param xid 事务 ID
     * @param savepoint {@link #createSavepoint(String)} 返回的保存点
     */
    public static void releaseSavepoint(String xid, Object savepoint) {
        int savepointIndex = findSavepointIndex(xid, savepoint);
        List<TransactionSavepoint> savepoints = SAVEPOINT_HOLDER.get().get(xid);
        Map<String, Connection> connections = CONNECTION_HOLDER.get().get(xid);
        SQLException exception = null;

        for (int i = savepoints.size() - 1; i >= savepointIndex; i--) {
            TransactionSavepoint current = savepoints.get(i);
            for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                Savepoint jdbcSavepoint = current.savepoints.get(entry.getKey());
                if (jdbcSavepoint == null) {
                    continue;
                }
                try {
                    entry.getValue().releaseSavepoint(jdbcSavepoint);
                } catch (SQLException e) {
                    if (!isIgnorableSavepointReleaseException(e)) {
                        exception = mergeException(exception, e);
                    }
                }
            }
        }

        removeSavepointsFrom(savepoints, savepointIndex);
        if (exception != null) {
            throw new TransactionException("Could not release JDBC savepoint", exception);
        }
    }

    public static void commit(String xid) {
        release(xid, true);
    }

    public static void rollback(String xid) {
        release(xid, false);
    }

    private static void release(String xid, boolean commit) {
        //先release，才能正常的进行 commit 或者 rollback.
        TransactionContext.release();

        Exception exception = null;
        Map<String, Map<String, Connection>> holdMap = CONNECTION_HOLDER.get();
        try {
            if (holdMap.isEmpty()) {
                return;
            }
            Map<String, Connection> connections = holdMap.get(xid);
            if (connections != null) {
                for (Connection conn : connections.values()) {
                    try {
                        if (commit) {
                            conn.commit();
                        } else {
                            conn.rollback();
                        }
                    } catch (SQLException e) {
                        exception = e;
                    } finally {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            //ignore
                        }
                    }
                }
            }
        } finally {
            holdMap.remove(xid);
            Map<String, List<TransactionSavepoint>> savepointMap = SAVEPOINT_HOLDER.get();
            savepointMap.remove(xid);

            if (holdMap.isEmpty()) {
                CONNECTION_HOLDER.remove();
            }
            if (savepointMap.isEmpty()) {
                SAVEPOINT_HOLDER.remove();
            }
            if (exception != null) {
                log.error("TransactionalManager.release() is error. Cause: " + exception.getMessage(), exception);
            }
        }
    }

    private static void assertActiveTransaction(String xid) {
        if (xid == null || !CONNECTION_HOLDER.get().containsKey(xid)) {
            throw new TransactionException("No active transaction found for xid: " + xid);
        }
    }

    private static int findSavepointIndex(String xid, Object savepoint) {
        assertActiveTransaction(xid);
        List<TransactionSavepoint> savepoints = SAVEPOINT_HOLDER.get().get(xid);
        for (int i = 0; i < savepoints.size(); i++) {
            if (savepoints.get(i) == savepoint) {
                return i;
            }
        }
        throw new TransactionException("Savepoint does not belong to transaction: " + xid);
    }

    private static void removeSavepointsAfter(List<TransactionSavepoint> savepoints, int index) {
        removeSavepointsFrom(savepoints, index + 1);
    }

    private static void removeSavepointsFrom(List<TransactionSavepoint> savepoints, int index) {
        while (savepoints.size() > index) {
            savepoints.remove(savepoints.size() - 1);
        }
    }

    private static void releaseInitializedSavepoints(String ds, Connection connection,
                                                     List<TransactionSavepoint> initialized, SQLException cause) {
        for (int i = initialized.size() - 1; i >= 0; i--) {
            Savepoint savepoint = initialized.get(i).savepoints.remove(ds);
            try {
                connection.releaseSavepoint(savepoint);
            } catch (SQLException releaseException) {
                if (!isIgnorableSavepointReleaseException(releaseException)) {
                    cause.addSuppressed(releaseException);
                }
            }
        }
    }

    private static void releaseCreatedSavepoints(Map<String, Connection> connections,
                                                 TransactionSavepoint savepoint, SQLException cause) {
        for (Map.Entry<String, Savepoint> entry : savepoint.savepoints.entrySet()) {
            try {
                connections.get(entry.getKey()).releaseSavepoint(entry.getValue());
            } catch (SQLException releaseException) {
                if (!isIgnorableSavepointReleaseException(releaseException)) {
                    cause.addSuppressed(releaseException);
                }
            }
        }
    }

    private static boolean isIgnorableSavepointReleaseException(SQLException exception) {
        if (exception instanceof SQLFeatureNotSupportedException || "3B001".equals(exception.getSQLState())) {
            return true;
        }
        String message = exception.getMessage();
        return message != null && (message.contains("not supported") || message.contains("3B001"));
    }

    private static SQLException mergeException(SQLException current, SQLException next) {
        if (current == null) {
            return next;
        }
        current.addSuppressed(next);
        return current;
    }

    private static class TransactionSavepoint {
        private final Map<String, Savepoint> savepoints = new LinkedHashMap<>();
    }

}
