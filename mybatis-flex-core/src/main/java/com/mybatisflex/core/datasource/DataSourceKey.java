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

import java.util.function.Supplier;

/**
 * @author michael
 */
public class DataSourceKey {

    /**
     * 通过注解设置的 key
     */
    private static final ThreadLocal<String> annotationKeyThreadLocal = new ThreadLocal<>();

    /**
     * 通过手动编码指定的 key
     */
    private static final ThreadLocal<String> manualKeyThreadLocal = new ThreadLocal<>();

    public static String manualKey;

    private DataSourceKey() {
    }

    public static void use(String dataSourceKey) {
        manualKeyThreadLocal.set(dataSourceKey.trim());
    }

    public static void useWithAnnotation(String dataSourceKey) {
        annotationKeyThreadLocal.set(dataSourceKey.trim());
    }

    public static <T> T use(String dataSourceKey, Supplier<T> supplier) {
        try {
            use(dataSourceKey);
            return supplier.get();
        } finally {
            clear();
        }
    }

    public static void use(String dataSourceKey, Runnable runnable) {
        try {
            use(dataSourceKey);
            runnable.run();
        } finally {
            clear();
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

}
