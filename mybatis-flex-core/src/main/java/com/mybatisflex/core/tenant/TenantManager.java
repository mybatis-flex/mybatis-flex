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
package com.mybatisflex.core.tenant;

import java.util.function.Supplier;

public class TenantManager {

    private TenantManager() {
    }

    private static final ThreadLocal<Boolean> ignoreFlags = new ThreadLocal<>();

    private static TenantFactory tenantFactory;

    public static TenantFactory getTenantFactory() {
        return tenantFactory;
    }

    public static void setTenantFactory(TenantFactory tenantFactory) {
        TenantManager.tenantFactory = tenantFactory;
    }

    /**
     * 忽略 tenant 条件
     */
    public static <T> T withoutTenantCondition(Supplier<T> supplier) {
        try {
            ignoreTenantCondition();
            return supplier.get();
        } finally {
            restoreTenantCondition();
        }
    }

    /**
     * 忽略 tenant 条件
     */
    public static void withoutTenantCondition(Runnable runnable) {
        try {
            ignoreTenantCondition();
            runnable.run();
        } finally {
            restoreTenantCondition();
        }
    }


    /**
     * 忽略 tenant 条件
     */
    public static void ignoreTenantCondition() {
        ignoreFlags.set(Boolean.TRUE);
    }


    /**
     * 恢复 tenant 条件
     */
    public static void restoreTenantCondition() {
        ignoreFlags.remove();
    }

    /**
     * @deprecated 使用 {@link #getTenantIds(String)} 代替。
     */
    @Deprecated
    public static Object[] getTenantIds() {
        return getTenantIds(null);
    }

    public static Object[] getTenantIds(String tableName) {
        Boolean ignoreFlag = ignoreFlags.get();
        if (ignoreFlag != null && ignoreFlag) {
            return null;
        }
        return tenantFactory != null ? tenantFactory.getTenantIds(tableName) : null;
    }


}
