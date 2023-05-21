/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.query;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * Cross Package Invoke
 * 夸包调用工具类，这么设计的原因，是需要保证 QueryWrapper 方法对于用户的纯净性
 * 而 framework 又可以通过 CPI 来调用 QueryWrapper 的其他方法
 */

public class CPI {


    public static Object[] getValueArray(QueryWrapper queryWrapper) {
        return queryWrapper.getValueArray();
    }

    public static List<QueryWrapper> getChildSelect(QueryWrapper queryWrapper) {
        return queryWrapper.getChildSelect();
    }

    public static List<QueryTable> getQueryTables(QueryWrapper queryWrapper) {
        return queryWrapper.getQueryTables();
    }

    public static void setQueryTable(QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        queryWrapper.setQueryTables(queryTables);
    }

    public static String getDataSource(QueryWrapper queryWrapper) {
        return queryWrapper.getDataSource();
    }

    public static void setDataSource(QueryWrapper queryWrapper, String datasource) {
        queryWrapper.setDataSource(datasource);
    }

    public static String getHint(QueryWrapper queryWrapper) {
        return queryWrapper.getHint();
    }

    public static void setHint(QueryWrapper queryWrapper, String hint) {
        queryWrapper.setHint(hint);
    }

    public static List<QueryColumn> getSelectColumns(QueryWrapper queryWrapper) {
        return queryWrapper.getSelectColumns();
    }

    public static void setSelectColumns(QueryWrapper queryWrapper, List<QueryColumn> selectColumns) {
        queryWrapper.setSelectColumns(selectColumns);
    }

    public static void setSelectColumnsIfNecessary(QueryWrapper queryWrapper, List<QueryColumn> selectColumns) {
        if (CollectionUtil.isEmpty(queryWrapper.getSelectColumns())
                && CollectionUtil.isNotEmpty(selectColumns)
                && CollectionUtil.isEmpty(CPI.getJoinTables(queryWrapper))
        ) {
            queryWrapper.setSelectColumns(selectColumns);
        }
    }

    public static List<Join> getJoins(QueryWrapper queryWrapper) {
        return queryWrapper.getJoins();
    }

    public static void setJoins(QueryWrapper queryWrapper, List<Join> joins) {
        queryWrapper.setJoins(joins);
    }

    public static String getJoinType(Join join){
        return join.type;
    }

    public static QueryTable getJoinQueryTable(Join join){
        return join.getQueryTable();
    }

    public static List<QueryTable> getJoinTables(QueryWrapper queryWrapper) {
        return queryWrapper.getJoinTables();
    }

    public static void setJoinTables(QueryWrapper queryWrapper, List<QueryTable> joinTables) {
        queryWrapper.setJoinTables(joinTables);
    }


    public static QueryCondition getWhereQueryCondition(QueryWrapper queryWrapper) {
        return queryWrapper.getWhereQueryCondition();
    }

    public static List<QueryColumn> getGroupByColumns(QueryWrapper queryWrapper) {
        return queryWrapper.getGroupByColumns();
    }

    public static void setGroupByColumns(QueryWrapper queryWrapper, List<QueryColumn> groupByColumns) {
        queryWrapper.setGroupByColumns(groupByColumns);
    }

    public static QueryCondition getHavingQueryCondition(QueryWrapper queryWrapper) {
        return queryWrapper.getHavingQueryCondition();
    }

    public static void setHavingQueryCondition(QueryWrapper queryWrapper, QueryCondition havingQueryCondition) {
        queryWrapper.setHavingQueryCondition(havingQueryCondition);
    }

    public static List<QueryOrderBy> getOrderBys(QueryWrapper queryWrapper) {
        return queryWrapper.getOrderBys();
    }

    public static void setOrderBys(QueryWrapper queryWrapper, List<QueryOrderBy> orderBys) {
        queryWrapper.setOrderBys(orderBys);
    }

    public static List<UnionWrapper> getUnions(QueryWrapper queryWrapper) {
        return queryWrapper.getUnions();
    }

    public static void setUnions(QueryWrapper queryWrapper, List<UnionWrapper> unions) {
        queryWrapper.setUnions(unions);
    }


    public static Integer getLimitOffset(QueryWrapper queryWrapper) {
        return queryWrapper.getLimitOffset();
    }

    public static void setLimitOffset(QueryWrapper queryWrapper, Integer limitOffset) {
        queryWrapper.setLimitOffset(limitOffset);
    }

    public static Integer getLimitRows(QueryWrapper queryWrapper) {
        return queryWrapper.getLimitRows();
    }

    public static void setLimitRows(QueryWrapper queryWrapper, Integer limitRows) {
        queryWrapper.setLimitRows(limitRows);
    }

    public static Map<String, Object> getContext(QueryWrapper queryWrapper) {
        return queryWrapper.getContext();
    }

    public static void setContext(QueryWrapper queryWrapper, Map<String, Object> context) {
        queryWrapper.setContext(context);
    }

    public static void putContext(QueryWrapper queryWrapper, String key, Object value) {
        queryWrapper.putContext(key, value);
    }


    public static <R> R getContext(QueryWrapper queryWrapper, String key) {
        return queryWrapper.getContext(key);
    }


    public static String toConditionSql(QueryColumn queryColumn, List<QueryTable> queryTables, IDialect dialect) {
        return queryColumn.toConditionSql(queryTables, dialect);
    }

    public static String toSelectSql(QueryColumn queryColumn, List<QueryTable> queryTables, IDialect dialect) {
        return queryColumn.toSelectSql(queryTables, dialect);
    }

    public static void setFromIfNecessary(QueryWrapper queryWrapper, String tableName) {
        if (StringUtil.isNotBlank(tableName)
                && CollectionUtil.isEmpty(queryWrapper.getQueryTables())) {
            queryWrapper.from(tableName);
        }
    }

    public static boolean containsTable(QueryCondition condition,String ... tables){
        return condition != null && condition.containsTable(tables);
    }
}
