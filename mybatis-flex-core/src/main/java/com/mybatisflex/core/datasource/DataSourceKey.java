/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.core.datasource;

import com.mybatisflex.core.exception.FlexAssert;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * @author michael
 */
public class DataSourceKey {

    private static ThreadLocal<Deque<String>> lookup = ThreadLocal.withInitial(ArrayDeque::new);

    private DataSourceKey() {
    }

    public static void use(String dataSourceKey) {
        Deque<String> deque = lookup.get();
        if (deque != null) {
            deque.push(dataSourceKey);
        }
    }

    public static String get() {
        Deque<String> deque = lookup.get();
        return deque != null ? deque.peek() : null;
    }

    public static void clear() {
        Deque<String> deque = lookup.get();
        if (deque != null) {
            deque.pop();
            if (deque.isEmpty()) {
                lookup.remove();
            }
        }
    }

    public static void forceClear() {
        lookup.remove();
    }

    public static void use(String dataSourceKey, Runnable runnable) {
        try {
            use(dataSourceKey);
            runnable.run();
        } finally {
            clear();
        }
    }

    public static <T> T use(String dataSourceKey, Supplier<T> supplier) {
        try {
            use(dataSourceKey);
            return supplier.get();
        } finally {
            clear();
        }
    }

    public static void setThreadLocal(ThreadLocal<Deque<String>> threadLocal) {
        FlexAssert.notNull(threadLocal, "threadLocal");
        if (threadLocal.get() == null) {
            threadLocal.set(lookup.get());
        }
        lookup = threadLocal;
    }

    public static String processDataSourceKey(String dataSourceKey, Object targetOrProxy, Method method, Object[] arguments) {
        String dsKey = DataSourceManager.processDataSourceKey(dataSourceKey, targetOrProxy, method, arguments);
        return dsKey != null ? dsKey : dataSourceKey;
    }


    public static String getShardingDsKey(String dataSource, Object mapper, Method method, Object[] args) {
        String shardingDsKey = DataSourceManager.getShardingDsKey(dataSource, mapper, method, args);
        return shardingDsKey != null ? shardingDsKey : dataSource;
    }

    // === For Removal ===

    @Deprecated
    public static String getByManual() {
        throw new UnsupportedOperationException("使用 DataSource.get() 代替。");
    }

    @Deprecated
    public static String getByAnnotation() {
        throw new UnsupportedOperationException("使用 DataSource.get() 代替。");
    }

    @Deprecated
    public static void useWithAnnotation(String dataSourceKey) {
        throw new UnsupportedOperationException("使用 DataSource.use(String) 代替。");
    }

    @Deprecated
    public static void setAnnotationKeyThreadLocal(ThreadLocal<String> annotationKeyThreadLocal) {
        throw new UnsupportedOperationException("使用 DataSource.setThreadLocal(ThreadLocal<Deque<String>>) 代替。");
    }

    @Deprecated
    public static void setManualKeyThreadLocal(ThreadLocal<String> manualKeyThreadLocal) {
        throw new UnsupportedOperationException("使用 DataSource.setThreadLocal(ThreadLocal<Deque<String>>) 代替。");
    }

}
