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
package com.mybatisflex.core.datasource;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * @author michael
 */
public class DataSourceKey {

    /**
     * 通过注解设置的 key
     */
    private static ThreadLocal<String> annotationKeyThreadLocal = new ThreadLocal<>();

    /**
     * 通过手动编码指定的 key
     */
    private static ThreadLocal<String> manualKeyThreadLocal = new ThreadLocal<>();

    private DataSourceKey() {
    }

    public static void use(String dataSourceKey) {
        manualKeyThreadLocal.set(dataSourceKey.trim());
    }

    public static void useWithAnnotation(String dataSourceKey) {
        annotationKeyThreadLocal.set(dataSourceKey.trim());
    }

    public static <T> T use(String dataSourceKey, Supplier<T> supplier) {
        String prevKey = manualKeyThreadLocal.get();
        try {
            manualKeyThreadLocal.set(dataSourceKey);
            return supplier.get();
        } finally {
            if (prevKey != null) {
                manualKeyThreadLocal.set(prevKey);
            } else {
                clear();
            }
        }
    }

    public static void use(String dataSourceKey, Runnable runnable) {
        String prevKey = manualKeyThreadLocal.get();
        try {
            manualKeyThreadLocal.set(dataSourceKey);
            runnable.run();
        } finally {
            if (prevKey != null) {
                manualKeyThreadLocal.set(prevKey);
            } else {
                clear();
            }
        }
    }

    public static void clear() {
        annotationKeyThreadLocal.remove();
        manualKeyThreadLocal.remove();
    }

    public static String getByAnnotation() {
        return annotationKeyThreadLocal.get();
    }

    public static String getByManual() {
        return manualKeyThreadLocal.get();
    }

    public static String get() {
        String key = manualKeyThreadLocal.get();
        return key != null ? key : annotationKeyThreadLocal.get();
    }

    public static void setAnnotationKeyThreadLocal(ThreadLocal<String> annotationKeyThreadLocal) {
        DataSourceKey.annotationKeyThreadLocal = annotationKeyThreadLocal;
    }

    public static void setManualKeyThreadLocal(ThreadLocal<String> manualKeyThreadLocal) {
        DataSourceKey.manualKeyThreadLocal = manualKeyThreadLocal;
    }

    public static String getShardingDsKey(String dataSource, Object mapper, Method method, Object[] args) {
        String shardingDsKey = DataSourceManager.getShardingDsKey(dataSource, mapper, method, args);
        return shardingDsKey != null ? shardingDsKey : dataSource;
    }
}
