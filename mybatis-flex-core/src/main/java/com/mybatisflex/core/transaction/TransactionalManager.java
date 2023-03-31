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
package com.mybatisflex.core.transaction;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事务管理器
 */
public class TransactionalManager {

    private static final Log log = LogFactory.getLog(TransactionalManager.class);

    //<xid : <datasource : connection>>
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

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error set AutoCommit to false.  Cause: " + e);
            }
        }
    }


    public static Connection getConnection(String xid, String ds) {
        Map<String, Connection> connections = CONNECTION_HOLDER.get().get(xid);
        return connections == null || connections.isEmpty() ? null : connections.get(ds);
    }


    public static void commit(String xid) {
        release(xid, true);
    }

    public static void rollback(String xid) {
        release(xid, false);
    }


    private static void release(String xid, boolean commit) {
        Exception exception = null;
        Map<String, Map<String, Connection>> holdMap = CONNECTION_HOLDER.get();
        try {
            if (holdMap.isEmpty()) {
                return;
            }
            Map<String, Connection> connections = holdMap.get(xid);
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
        } finally {
            holdMap.remove(xid);
            if (holdMap.isEmpty()) {
                CONNECTION_HOLDER.remove();
            }
            if (exception != null) {
                log.error("TransactionalManager.release() is error. cause: " + exception.getMessage(), exception);
            }
        }
    }
}
