package com.mybatisflex.core.query;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexAssert;

import java.util.List;

/**
 * {@link QueryColumn} 适配器，用于将 {@link QueryCondition} 转换为 {@link QueryColumn}。
 *
 * @author 王帅
 * @since 2024-09-29
 */
public class QueryColumnAdapter extends QueryColumn implements HasParamsColumn {

    private final QueryCondition condition;

    public QueryColumnAdapter(QueryCondition condition) {
        FlexAssert.notNull(condition, "condition");
        this.condition = condition;
    }

    public QueryCondition getCondition() {
        return condition;
    }

    @Override
    public Object[] getParamValues() {
        return WrapperUtil.getValues(condition);
    }

    @Override
    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return condition.toSql(queryTables, dialect) + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return condition.toSql(queryTables, dialect);
    }

}
