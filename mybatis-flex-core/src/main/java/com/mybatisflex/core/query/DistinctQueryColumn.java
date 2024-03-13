/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.core.query;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistinctQueryColumn extends QueryColumn implements HasParamsColumn {

    private List<QueryColumn> queryColumns;

    public DistinctQueryColumn(QueryColumn... queryColumns) {
        this.queryColumns = CollectionUtil.newArrayList(queryColumns);
    }

    public List<QueryColumn> getQueryColumns() {
        return queryColumns;
    }

    public void setQueryColumns(List<QueryColumn> queryColumns) {
        this.queryColumns = queryColumns;
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        if (CollectionUtil.isEmpty(queryTables)) {
            return SqlConsts.EMPTY;
        }

        return SqlConsts.DISTINCT + StringUtil.join(
            SqlConsts.DELIMITER,
            queryColumns,
            queryColumn -> queryColumn.toSelectSql(queryTables, dialect)
        );
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        if (CollectionUtil.isEmpty(queryTables)) {
            return SqlConsts.EMPTY;
        }

        String sql = SqlConsts.DISTINCT + StringUtil.join(
            SqlConsts.DELIMITER,
            queryColumns,
            queryColumn -> queryColumn.toSelectSql(queryTables, dialect)
        );

        return sql + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    public DistinctQueryColumn clone() {
        DistinctQueryColumn clone = (DistinctQueryColumn) super.clone();
        // deep clone ...
        clone.queryColumns = CollectionUtil.cloneArrayList(this.queryColumns);

        return clone;
    }

    @Override
    public Object[] getParamValues() {
        if (CollectionUtil.isEmpty(queryColumns)) {
            return FlexConsts.EMPTY_ARRAY;
        }

        List<Object> params = new ArrayList<>();

        for (QueryColumn queryColumn : queryColumns) {
            if (queryColumn instanceof HasParamsColumn) {
                Object[] paramValues = ((HasParamsColumn) queryColumn).getParamValues();

                params.addAll(Arrays.asList(paramValues));
            }
        }

        return params.toArray();
    }
}
