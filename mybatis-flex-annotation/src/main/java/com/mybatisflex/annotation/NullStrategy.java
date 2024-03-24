package com.mybatisflex.annotation;

/**
 * 插入或者更新时对null值的处理策略
 */
public enum NullStrategy {
    /**
     * 默认忽略字段
     */
    DEFAULT,
    /**
     * 插入时保留null
     */
    KEEP_INSERT,
    /**
     * 更新时保留null
     */
    KEEP_UPDATE,
    /**
     * 插入和更新时保留null
     */
    KEEP_INSERT_UPDATE
}
