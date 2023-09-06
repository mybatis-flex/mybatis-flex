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
package com.mybatisflex.core.logicdelete;

import com.mybatisflex.core.logicdelete.impl.DefaultLogicDeleteProcessor;

import java.util.function.Supplier;

/**
 * 逻辑删除管理器。
 */
public class LogicDeleteManager {

    private LogicDeleteManager() {
    }

    private static LogicDeleteProcessor processor = new DefaultLogicDeleteProcessor();
    private static final ThreadLocal<Boolean> skipFlags = new ThreadLocal<>();

    /**
     * 获取逻辑删除处理器。
     *
     * @return 逻辑删除处理器
     */
    public static LogicDeleteProcessor getProcessor() {
        return processor;
    }

    /**
     * 设置逻辑删除处理器。
     *
     * @param processor 逻辑删除处理器
     */
    public static void setProcessor(LogicDeleteProcessor processor) {
        LogicDeleteManager.processor = processor;
    }

    /**
     * 跳过逻辑删除字段处理，直接进行数据库物理操作。
     */
    public static <T> T execWithoutLogicDelete(Supplier<T> supplier) {
        try {
            skipLogicDelete();
            return supplier.get();
        } finally {
            restoreLogicDelete();
        }
    }

    /**
     * 跳过逻辑删除字段处理，直接进行数据库物理操作。
     */
    public static void execWithoutLogicDelete(Runnable runnable) {
        try {
            skipLogicDelete();
            runnable.run();
        } finally {
            restoreLogicDelete();
        }
    }

    /**
     * 跳过逻辑删除字段处理。
     */
    public static void skipLogicDelete() {
        skipFlags.set(Boolean.TRUE);
    }

    /**
     * 恢复逻辑删除字段处理。
     */
    public static void restoreLogicDelete() {
        skipFlags.remove();
    }

    /**
     * 获取逻辑删除列，返回 {@code null} 表示跳过逻辑删除。
     *
     * @param logicDeleteColumn 逻辑删除列
     * @return 逻辑删除列
     */
    public static String getLogicDeleteColumn(String logicDeleteColumn) {
        if (logicDeleteColumn == null) {
            return null;
        }
        Boolean skipFlag = skipFlags.get();
        if (skipFlag == null) {
            return logicDeleteColumn;
        }
        return skipFlag ? null : logicDeleteColumn;
    }

}
