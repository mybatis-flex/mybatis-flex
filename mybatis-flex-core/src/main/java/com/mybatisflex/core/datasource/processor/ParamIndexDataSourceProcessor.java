package com.mybatisflex.core.datasource.processor;

import com.mybatisflex.processor.util.StrUtil;

import java.lang.reflect.Method;

/**
 * 参数索引中取出数据源名称(针对简单类型参数快速解析读取)
 *
 * @author Alay
 * @since 2024-12-07 15:43
 */
public class ParamIndexDataSourceProcessor implements DataSourceProcessor {
    private static final String NULL_STR = "null";
    private static final String DYNAMIC_PREFIX = "#";
    private static final String INDEX_FIRST = "#first";
    private static final String INDEX_LAST = "#last";
    private static final String PARAM_INDEX = "#index";

    /**
     * 若不符合处理逻辑将返回 null 值
     */
    @Override
    public String process(String dataSourceKey, Object mapper, Method method, Object[] arguments) {
        if (StrUtil.isBlank(dataSourceKey)) return null;
        if (!dataSourceKey.startsWith(DYNAMIC_PREFIX)) return null;
        // 无效的参数
        if (arguments.length == 0) return null;

        Integer index = null;
        if (INDEX_FIRST.equals(dataSourceKey)) {
            index = 0;
        } else if (INDEX_LAST.equals(dataSourceKey)) {
            index = arguments.length - 1;
        } else if (dataSourceKey.startsWith(PARAM_INDEX)) {
            index = parseIndex(dataSourceKey);
        }

        // 没有符合约定的格式输入,则会返回 null
        if (null == index) return null;
        // 参数输入不合法(索引参数大于参数索引数)
        if (index >= arguments.length) return null;

        // 参数中按照索引取出数值
        String value = String.valueOf(arguments[index]);
        if (StrUtil.isBlank(value) || NULL_STR.equals(value)) return null;

        return value;
    }

    private static Integer parseIndex(String dsKey) {
        // 参数索引
        String indexStr = dsKey.substring(PARAM_INDEX.length());
        if (indexStr.isEmpty()) return null;
        try {
            return Integer.parseInt(indexStr);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
