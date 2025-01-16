package com.mybatisflex.core.datasource.processor;

import com.mybatisflex.core.mybatis.binding.FlexMapperProxy;

import java.lang.reflect.Method;

/**
 * 动态数据源 @UseDataSource的value值解析处理器(如表达式解析取值等),使用时推荐使用 DelegatingDataSourceProcessor{@link DelegatingDataSourceProcessor} 实例化
 * 对动态数据源注解@UseDataSource 增强处理{@link com.mybatisflex.annotation.UseDataSource}
 * <p>
 * 使用区分Spring模式 和 非Spring模式，Spring模式下，代理处理逻辑 DataSourceInterceptor{@link com.mybatisflex.spring.datasource.DataSourceInterceptor} 优先级高于 FlexMapperProxy{@link com.mybatisflex.core.mybatis.binding.FlexMapperProxy} ;
 * 所以Spring模式下仅 DataSourceInterceptor 生效(切面生效的前提下)。非Spring 模式下,仅支持注解使用到 Mapper(Dao层),使用到其他层(如Service层)不支持注解解析。
 * <p>
 * Spring模式下,不区分使用到程序的层(Controller、Service、Dao层都支持)，下层控制粒度细上层控制粒粗，使用时根据需要进行灵活应用。
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
