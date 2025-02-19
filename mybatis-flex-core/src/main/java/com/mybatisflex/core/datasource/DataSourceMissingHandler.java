package com.mybatisflex.core.datasource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 数据源缺失处理器接口。当尝试获取指定数据源但不存在时，通过此接口进行动态处理。
 *
 * <p>
 * 该接口被设计为函数式接口，可通过Lambda表达式或方法引用实现，用于在运行时动态处理缺失的数据源。<br/>
 * 常见应用场景：<br/>
 * - 多租户系统中根据租户ID动态创建并缓存数据源；<br/>
 * - 数据源缺失时的主动初始化；
 * </p>
 */
@FunctionalInterface
public interface DataSourceMissingHandler {
    /**
     * 处理缺失数据源的核心方法。
     *
     * @param dataSourceKey 当前请求的数据源键（标识符），通常用于识别目标数据源
     * @param dataSourceMap 当前已存在的数据源集合（key: 数据源键，value: 数据源实例）
     * @return 处理后的新数据源集合，通常应包含原有数据源及新增处理的数据源
     * @implSpec 实现类应通过此方法实现：<br/>
     * 1. 根据dataSourceKey识别需要补充的数据源<br/>
     * 2. 创建/配置新的DataSource实例<br/>
     * 3. 将新数据源添加到dataSourceMap中<br/>
     * 4. 返回更新后的数据源集合<br/>
     * 5. 如返回 null 或 Map 为空，后续会抛出异常。<br/>
     * @example 示例场景：
     * 当请求"tenant_123"数据源不存在时，在此方法中创建对应数据源并放入返回的Map
     */
    Map<String, DataSource> handle(String dataSourceKey, Map<String, DataSource> dataSourceMap);
}
