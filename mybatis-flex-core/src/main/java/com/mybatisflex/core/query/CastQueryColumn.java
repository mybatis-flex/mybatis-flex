/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
import com.mybatisflex.core.dialect.IDialect;

import java.util.List;

/**
 * CAST函数查询列
 */
public class CastQueryColumn extends QueryColumn implements HasParamsColumn {

    private QueryColumn column;
    private final String dataType;

    public CastQueryColumn(QueryColumn column, String dataType) {
        this.column = column;
        this.dataType = dataType;
    }

    public CastQueryColumn(String column, String dataType) {
        this.column = new QueryColumn(column);
        this.dataType = dataType;
    }

    @Override
    protected String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return " CAST(" + column.toConditionSql(queryTables, dialect) + " AS " + dataType + ") ";
    }

    @Override
    protected String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return " CAST(" + column.toSelectSql(queryTables, dialect) + " AS " + dataType + ") " + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    public CastQueryColumn clone() {
        CastQueryColumn clone = (CastQueryColumn) super.clone();
        clone.column = column.clone();
        return clone;
    }

    @Override
    public String toString() {
        return "CastQueryColumn{" +
            "column=" + column +
            ", dataType='" + dataType + '\'' +
            ", alias='" + alias + '\'' +
            '}';
    }

    @Override
    public Object[] getParamValues() {
        if (column instanceof HasParamsColumn) {
            return ((HasParamsColumn) column).getParamValues();
        }
        return FlexConsts.EMPTY_ARRAY;
    }
}
