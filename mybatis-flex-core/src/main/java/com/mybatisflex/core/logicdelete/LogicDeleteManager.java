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

import java.util.function.Supplier;

public class LogicDeleteManager {

    private static LogicDeleteProcessor processor = new DefaultLogicDeleteProcessorImpl();
    private static final ThreadLocal<Boolean> skipFlags = new ThreadLocal<>();

    public static LogicDeleteProcessor getProcessor() {
        return processor;
    }

    public static void setProcessor(LogicDeleteProcessor processor) {
        LogicDeleteManager.processor = processor;
    }

    /**
     * 跳过逻辑删除字段处理，直接进行数据库物理操作
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
     * 跳过逻辑删除字段处理
     */
    public static void skipLogicDelete() {
        skipFlags.set(Boolean.TRUE);
    }


    /**
     * 恢复逻辑删除字段处理
     */
    public static void restoreLogicDelete() {
        skipFlags.remove();
    }


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
