package com.mybatisflex.core.datasource.processor;

import com.mybatisflex.core.mybatis.binding.FlexMapperProxy;

import java.lang.reflect.Method;

/**
 * 动态数据源 @UseDataSource的value值解析处理器(如表达式解析取值等),使用时推荐使用 DelegatingDataSourceProcessor{@link DelegatingDataSourceProcessor} 实例化
 * 对动态数据源注解@UseDataSource 增强处理{@link com.mybatisflex.annotation.UseDataSource}
 *
 * @author Alay
 * @since 2024-12-07 15:34
 */
public interface DataSourceProcessor {

    /**
     * 数据源key解析扩展
     *
     * @param dataSourceKey 注解UseDataSource的value 值,调用process时不会为null,可能为空字符{@link FlexMapperProxy#invoke(Object, Method, Object[])}And{@link com.mybatisflex.spring.datasource.DataSourceInterceptor#getDataSourceKey(Object, Method, Object[])}
     * @param targetOrProxy AOP对象this或Mapper代理对象(当注解@UseDataSource使用到Mapper上时为proxy)
     * @param method        Mapper当前执行的方法函数
     * @param arguments     Mapper当前执行的函数参数
     * @return 数据源名称（可能为null 为 null 时表示不符合当前处理器的处理）
     */
    String process(String dataSourceKey, Object targetOrProxy, Method method, Object[] arguments);

}
