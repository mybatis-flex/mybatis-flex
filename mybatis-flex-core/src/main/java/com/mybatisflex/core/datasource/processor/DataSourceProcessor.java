package com.mybatisflex.core.datasource.processor;

import java.lang.reflect.Method;

/**
 * 动态数据源 @UseDataSource key 解析处理器,使用时推荐注入 DelegatingDataSourceProcessor{@link DelegatingDataSourceProcessor} 类
 * 对动态数据源注解@UseDataSource 增强处理{@link com.mybatisflex.annotation.UseDataSource}
 *
 * @author Alay
 * @since 2024-12-07 15:34
 */
public interface DataSourceProcessor {

    /**
     * 数据源key解析扩展
     *
     * @param dataSourceKey 注解UseDataSource的value 值,调用process时不会为null,可能会空字符串
     * @param mapper        Mapper对象(代理对象)
     * @param method        Mapper当前执行的方法函数
     * @param arguments     Mapper当前执行的函数参数
     * @return 数据源名称（可能为null 为 null 时表示不符合当前处理器的处理）
     */
    String process(String dataSourceKey, Object mapper, Method method, Object[] arguments);

}
