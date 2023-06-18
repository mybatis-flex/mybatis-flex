package com.mybatisflex.core.logicdelete;

import java.util.function.Supplier;

public class LogicDeleteManager {

    private static final ThreadLocal<Boolean> skipFlags = new ThreadLocal<>();


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
