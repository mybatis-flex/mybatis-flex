package com.mybatisflex.core.querywrapper;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.util.StringUtil;

/**
 * 查询的 table，
 * 实例1：用于构建 select * from (select ...) 中的第二个 select
 * 实例2：用于构建 left join (select ...) 中的 select
 */
public class SelectQueryTable extends QueryTable {

    private QueryWrapper queryWrapper;

    public SelectQueryTable(QueryWrapper queryWrapper) {
        super();
        this.queryWrapper = queryWrapper;
    }

    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    @Override
    public String toSql(IDialect dialect) {
        String sql = dialect.buildSelectSql(queryWrapper);
        if (StringUtil.isNotBlank(alias)) {
            return "(" + sql + ") AS " + dialect.wrap(alias);
        } else {
            return sql;
        }
    }
}
