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


import org.apache.ibatis.cursor.Cursor;

import java.io.IOException;

/**
 * @author michael
 * 事务管理器上下文
 */
public class TransactionContext {

    private TransactionContext() {
    }

    private static final ThreadLocal<String> XID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Cursor<?>> CURSOR_HOLDER = new ThreadLocal<>();

    public static String getXID() {
        return XID_HOLDER.get();
    }

    public static void release() {
        XID_HOLDER.remove();
        closeCursor();
    }

    private static void closeCursor() {
        Cursor<?> cursor = CURSOR_HOLDER.get();
        if (cursor != null) {
            try {
                cursor.close();
            } catch (IOException e) {
                //ignore
            } finally {
                CURSOR_HOLDER.remove();
            }
        }
    }

    public static void holdXID(String xid) {
        XID_HOLDER.set(xid);
    }

    public static void holdCursor(Cursor<?> cursor) {
        CURSOR_HOLDER.set(cursor);
    }

}
