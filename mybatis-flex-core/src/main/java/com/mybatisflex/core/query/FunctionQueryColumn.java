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
import com.mybatisflex.core.util.SqlUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据库 聚合函数，例如 count(id) ，max(account.age) 等等
 */
public class FunctionQueryColumn extends QueryColumn implements HasParamsColumn {

    protected String fnName;
    protected List<QueryColumn> columns;

    public FunctionQueryColumn(String fnName) {
        SqlUtil.keepColumnSafely(fnName);
        this.fnName = fnName;
        this.columns = new ArrayList<>();
    }

    public FunctionQueryColumn(String fnName, String... columns) {
        this(fnName);
        for (String column : columns) {
            // SqlUtil.keepColumnSafely(column)
            this.columns.add(new QueryColumn(column));
        }
    }

    public FunctionQueryColumn(String fnName, QueryColumn... columns) {
        this(fnName);
        this.columns.addAll(Arrays.asList(columns));
    }

    public String getFnName() {
        return fnName;
    }

    public void setFnName(String fnName) {
        this.fnName = fnName;
    }

    public List<QueryColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<QueryColumn> columns) {
        this.columns = columns;
    }

    @Override
    public Object[] getParamValues() {
        if (CollectionUtil.isEmpty(columns)) {
            return FlexConsts.EMPTY_ARRAY;
        }

        List<Object> params = new ArrayList<>();

        for (QueryColumn queryColumn : columns) {
            if (queryColumn instanceof HasParamsColumn) {
                Object[] paramValues = ((HasParamsColumn) queryColumn).getParamValues();
                params.addAll(Arrays.asList(paramValues));
            }
        }

        return params.toArray();
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        String sql = getSql(queryTables, dialect);
        if (StringUtil.isBlank(alias)) {
            return fnName + WrapperUtil.withBracket(sql);
        }
        return fnName + WrapperUtil.withAlias(sql, alias, dialect);
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        String sql = getSql(queryTables, dialect);
        return fnName + WrapperUtil.withBracket(sql);
    }

    /**
     * <p>获取函数括号里面的 SQL 内容。
     *
     * <p>如果函数括号里面没有内容，就返回空字符串，这样构建出来就是函数名加括号。
     *
     * <p>例如，NOW() 函数的构建：
     * <pre>{@code
     * FunctionQueryColumn c1 = new FunctionQueryColumn("NOW");
     * FunctionQueryColumn c2 = new FunctionQueryColumn("NOW", new StringQueryColumn(""));
     * }</pre>
     */
    private String getSql(List<QueryTable> queryTables, IDialect dialect) {
        if (CollectionUtil.isEmpty(columns)) {
            return SqlConsts.EMPTY;
        }

        String sql = columns.stream()
            .filter(Objects::nonNull)
            .map(c -> c.toSelectSql(queryTables, dialect))
            .collect(Collectors.joining(SqlConsts.DELIMITER));

        if (StringUtil.isBlank(sql)) {
            return SqlConsts.EMPTY;
        }

        return sql;
    }


    @Override
    public String toString() {
        return "FunctionQueryColumn{" +
            "fnName='" + fnName + '\'' +
            ", columns=" + columns +
            '}';
    }

    @Override
    public FunctionQueryColumn clone() {
        FunctionQueryColumn clone = (FunctionQueryColumn) super.clone();
        // deep clone ...
        clone.columns = CollectionUtil.cloneArrayList(this.columns);
        return clone;
    }


}
