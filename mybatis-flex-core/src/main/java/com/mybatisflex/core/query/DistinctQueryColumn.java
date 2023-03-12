package com.mybatisflex.core.query;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;

public class DistinctQueryColumn extends QueryColumn {

    private List<QueryColumn> queryColumns;

    public DistinctQueryColumn(QueryColumn... queryColumns) {
        this.queryColumns = CollectionUtil.newArrayList(queryColumns);
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        if (CollectionUtil.isEmpty(queryTables)) {
            return "";
        }
        return " DISTINCT " + StringUtil.join(",", queryColumns, queryColumn ->
                queryColumn.toSelectSql(queryTables, dialect));
    }
}
