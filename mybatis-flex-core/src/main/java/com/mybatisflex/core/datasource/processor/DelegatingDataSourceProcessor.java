package com.mybatisflex.core.datasource.processor;

import com.mybatisflex.core.exception.FlexAssert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DataSourceProcessor 委托扩展类,对 DataSourceProcessor 结构进行扩大和增强
 * 如果多个实例化，建议通过 DelegatingDataSourceProcessor.with(多个解析处理器实例) 方式进行实例化。
 * 需要注意的是委托解析处理器之间有先后顺序，一旦排列前面的解析处理器正常处理后，将直接返回处理值，不再往下传递处理
 *
 * @author Alay
 * @since 2024-12-07 15:38
 */
public class DelegatingDataSourceProcessor implements DataSourceProcessor {
    /**
     * 多个处理器委托集合(使用时请注意 DataSourceProcessor 的顺序)
     */
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
            // 使用时请注意 DataSourceProcessor 的顺序,一旦匹配到处理器将进行终止处理链，并返回当前处理结果
            String returnKey = delegate.process(dataSourceKey, mapper, method, arguments);
            if (null != returnKey) return returnKey;
        }
        // 无可用的处理器策略,返回原始值
        return dataSourceKey;
    }

}
