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


import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.ObjectUtil;

import java.util.List;

/**
 * 排序字段
 */
public class QueryOrderBy implements CloneSupport<QueryOrderBy> {

    private QueryColumn queryColumn;

    private String orderType = "ASC"; //asc desc

    private boolean nullsFirst = false;
    private boolean nullsLast = false;

    protected QueryOrderBy() {
    }

    public QueryOrderBy(QueryColumn queryColumn, String orderType) {
        this.queryColumn = queryColumn;
        this.orderType = orderType;
    }


    public QueryOrderBy(QueryColumn queryColumn) {
        this.queryColumn = queryColumn;
    }


    public QueryOrderBy nullsFirst() {
        this.nullsFirst = true;
        this.nullsLast = false;
        return this;
    }


    public QueryOrderBy nullsLast() {
        this.nullsFirst = false;
        this.nullsLast = true;
        return this;
    }


    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        String sql = queryColumn.toConditionSql(queryTables, dialect) + " " + orderType;
        if (nullsFirst) {
            sql = sql + " NULLS FIRST";
        } else if (nullsLast) {
            sql = sql + " NULLS LAST";
        }
        return sql;
    }

    @Override
    public QueryOrderBy clone() {
        try {
            QueryOrderBy clone = (QueryOrderBy) super.clone();
            // deep clone ...
            clone.queryColumn = ObjectUtil.clone(this.queryColumn);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }
}
