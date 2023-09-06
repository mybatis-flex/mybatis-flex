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
package com.mybatisflex.core.transaction;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
        = ThreadLocal.withInitial(ConcurrentHashMap::new);


    public static void hold(String xid, String ds, Connection connection) {
        Map<String, Map<String, Connection>> holdMap = CONNECTION_HOLDER.get();
        Map<String, Connection> connMap = holdMap.get(xid);
        if (connMap == null) {
            connMap = new ConcurrentHashMap<>();
            holdMap.put(xid, connMap);
        }

        if (connMap.containsKey(ds)) {
            return;
        }

        connMap.put(ds, connection);
    }


    public static <T> T exec(Supplier<T> supplier, Propagation propagation, boolean withResult) {
        //上一级事务的id，支持事务嵌套
        String currentXID = TransactionContext.getXID();
        try {
            switch (propagation) {
                //若存在当前事务，则加入当前事务，若不存在当前事务，则创建新的事务
                case REQUIRED:
                    if (currentXID != null) {
                        return supplier.get();
                    } else {
                        return execNewTransactional(supplier, withResult);
                    }


                //若存在当前事务，则加入当前事务，若不存在当前事务，则已非事务的方式运行
                case SUPPORTS:
                    return supplier.get();


                //若存在当前事务，则加入当前事务，若不存在当前事务，则已非事务的方式运行
                case MANDATORY:
                    if (currentXID != null) {
                        return supplier.get();
                    } else {
                        throw new TransactionException("No existing transaction found for transaction marked with propagation 'mandatory'");
                    }


                //始终以新事务的方式运行，若存在当前事务，则暂停（挂起）当前事务。
                case REQUIRES_NEW:
                    return execNewTransactional(supplier, withResult);


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


                //暂时不支持这种事务传递方式
                //default 为 nested 方式
                default:
                    throw new TransactionException("Transaction manager does not allow nested transactions");

            }
        } finally {
            //恢复上一级事务
            if (currentXID != null) {
                TransactionContext.holdXID(currentXID);
            }
        }
    }

    private static <T> T execNewTransactional(Supplier<T> supplier, boolean withResult) {
        String xid = startTransactional();
        T result = null;
        boolean isRollback = false;
        try {
            result = supplier.get();
        } catch (Throwable e) {
            isRollback = true;
            rollback(xid);
            throw new TransactionException(e.getMessage(), e);
        } finally {
            if (!isRollback) {
                if (!withResult) {
                    if (result instanceof Boolean && (Boolean) result) {
                        commit(xid);
                    }
                    //null or false
                    else {
                        rollback(xid);
                    }
                } else {
                    commit(xid);
                }
            }
        }
        return result;
    }


    public static Connection getConnection(String xid, String ds) {
        Map<String, Connection> connections = CONNECTION_HOLDER.get().get(xid);
        return connections == null || connections.isEmpty() ? null : connections.get(ds);
    }


    public static String startTransactional() {
        String xid = UUID.randomUUID().toString();
        TransactionContext.holdXID(xid);
        return xid;
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

            if (holdMap.isEmpty()) {
                CONNECTION_HOLDER.remove();
            }
            if (exception != null) {
                log.error("TransactionalManager.release() is error. Cause: " + exception.getMessage(), exception);
            }
        }
    }

}
