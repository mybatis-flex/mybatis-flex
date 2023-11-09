package com.mybatisflex.core.query;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.dialect.IDialect;

import java.util.List;

/**
 * 取相反数（{@code -column}）。
 *
 * @author 王帅
 * @since 2023-11-09
 */
public class NegativeQueryColumn extends QueryColumn implements HasParamsColumn {

    private final QueryColumn queryColumn;

    public NegativeQueryColumn(QueryColumn queryColumn) {
        this.queryColumn = queryColumn;
    }

    @Override
    public Object[] getParamValues() {
        if (queryColumn instanceof HasParamsColumn) {
            return ((HasParamsColumn) queryColumn).getParamValues();
        }
        return FlexConsts.EMPTY_ARRAY;
    }

    @Override
    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return toConditionSql(queryTables, dialect) + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return "-" + queryColumn.toConditionSql(queryTables, dialect);
    }

}
