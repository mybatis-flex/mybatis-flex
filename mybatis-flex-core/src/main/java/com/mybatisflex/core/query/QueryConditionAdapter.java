package com.mybatisflex.core.query;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexAssert;

import java.util.List;

/**
 * {@link QueryCondition} 适配器，用于将 {@link QueryColumn} 转换为 {@link QueryCondition}。
 *
 * @author 王帅
 * @since 2024-09-29
 */
public class QueryConditionAdapter extends QueryCondition {

    public QueryConditionAdapter(QueryColumn column) {
        FlexAssert.notNull(column, "column");
        super.column = column;
    }

    @Override
    public Object getValue() {
        return column instanceof HasParamsColumn ? ((HasParamsColumn) column).getParamValues() : null;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        return column.toConditionSql(queryTables, dialect);
    }

}
