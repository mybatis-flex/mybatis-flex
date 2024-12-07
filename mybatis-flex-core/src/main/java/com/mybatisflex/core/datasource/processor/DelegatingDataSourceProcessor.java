package com.mybatisflex.core.datasource.processor;

import com.mybatisflex.core.exception.FlexAssert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DataSourceProcessor 委托扩展类,对 DataSourceProcessor 结构进行扩大和增强
 *
 * @author Alay
 * @since 2024-12-07 15:38
 */
public class DelegatingDataSourceProcessor implements DataSourceProcessor {

    private final List<DataSourceProcessor> delegates;


    private DelegatingDataSourceProcessor(List<DataSourceProcessor> delegates) {
        this.delegates = delegates;
    }

    /**
     * @param processors 使用时请注意 DataSourceProcessor 的顺序
     */
    public static DelegatingDataSourceProcessor with(DataSourceProcessor... processors) {
        FlexAssert.notEmpty(processors, "datasource processors");
        List<DataSourceProcessor> dataSourceProcessors = new ArrayList<>(Arrays.asList(processors));
        return new DelegatingDataSourceProcessor(dataSourceProcessors);
    }

    /**
     * @param processors 使用时请注意 DataSourceProcessor 的顺序
     */
    public static DelegatingDataSourceProcessor with(List<DataSourceProcessor> processors) {
        FlexAssert.notEmpty(processors, "datasource processors");
        return new DelegatingDataSourceProcessor(processors);
    }


    @Override
    public String process(String dataSourceKey, Object mapper, Method method, Object[] arguments) {
        for (DataSourceProcessor delegate : delegates) {
            // 使用时请注意 DataSourceProcessor 的顺序,一旦匹配到处理器将进行中断处理并返回处理结果
            String returnKey = delegate.process(dataSourceKey, mapper, method, arguments);
            if (null != returnKey) return returnKey;
        }
        // 无可用的处理器策略,返回原始值
        return dataSourceKey;
    }

}
