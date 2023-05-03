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

import java.io.Serializable;
import java.util.*;

public class BaseQueryWrapper<T> implements Serializable {


    protected List<QueryTable> queryTables;
    protected String dataSource;

    protected List<QueryColumn> selectColumns;
    protected List<Join> joins;
    protected List<QueryTable> joinTables;
    protected QueryCondition whereQueryCondition;
    protected List<QueryColumn> groupByColumns;
    protected QueryCondition havingQueryCondition;
    protected List<QueryOrderBy> orderBys;

    protected List<UnionWrapper> unions;

    protected Integer limitOffset;
    protected Integer limitRows;

    protected Map<String, Object> context;

//    protected boolean ignoreBlankStrings = false;


    protected T addSelectColumn(QueryColumn queryColumn) {
        if (selectColumns == null) {
            selectColumns = new LinkedList<>();
        }

        selectColumns.add(queryColumn);
        return (T) this;
    }


    protected T AddJoin(Join join) {
        if (joins == null) {
            joins = new LinkedList<>();
        }
        joins.add(join);
        return (T) this;
    }


    protected T setWhereQueryCondition(QueryCondition queryCondition) {
        if (whereQueryCondition != null) {
            queryCondition.connect(whereQueryCondition, SqlConnector.AND);
        }

        whereQueryCondition = queryCondition;
        return (T) this;
    }


    protected T addWhereQueryCondition(QueryCondition queryCondition, SqlConnector connector) {
        if (queryCondition != null) {
            if (whereQueryCondition == null) {
                whereQueryCondition = queryCondition;
            } else {
                whereQueryCondition.connect(queryCondition, connector);
            }
        }
        return (T) this;
    }


    protected T addGroupByColumns(QueryColumn queryColumn) {
        if (groupByColumns == null) {
            groupByColumns = new LinkedList<>();
        }

        groupByColumns.add(queryColumn);
        return (T) this;
    }


    protected T addHavingQueryCondition(QueryCondition queryCondition, SqlConnector connector) {
        if (havingQueryCondition == null) {
            havingQueryCondition = queryCondition;
        } else {
            havingQueryCondition.connect(queryCondition, connector);
        }
        return (T) this;
    }


    protected T addOrderBy(QueryOrderBy queryOrderBy) {
        if (orderBys == null) {
            orderBys = new LinkedList<>();
        }
        orderBys.add(queryOrderBy);
        return (T) this;
    }


    protected void addJoinTable(QueryTable queryTable) {
        if (joinTables == null) {
            joinTables = new ArrayList<>();
        }
        joinTables.add(queryTable);
    }


    protected List<QueryTable> getQueryTables() {
        return queryTables;
    }

    protected void setQueryTables(List<QueryTable> queryTables) {
        this.queryTables = queryTables;
    }

    protected String getDataSource() {
        return dataSource;
    }

    protected void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    protected List<QueryColumn> getSelectColumns() {
        return selectColumns;
    }

    protected void setSelectColumns(List<QueryColumn> selectColumns) {
        this.selectColumns = selectColumns;
    }

    protected List<Join> getJoins() {
        return joins;
    }

    protected void setJoins(List<Join> joins) {
        this.joins = joins;
    }

    protected List<QueryTable> getJoinTables() {
        return joinTables;
    }

    protected void setJoinTables(List<QueryTable> joinTables) {
        this.joinTables = joinTables;
    }

    protected QueryCondition getWhereQueryCondition() {
        return whereQueryCondition;
    }

    protected List<QueryColumn> getGroupByColumns() {
        return groupByColumns;
    }

    protected void setGroupByColumns(List<QueryColumn> groupByColumns) {
        this.groupByColumns = groupByColumns;
    }

    protected QueryCondition getHavingQueryCondition() {
        return havingQueryCondition;
    }

    protected void setHavingQueryCondition(QueryCondition havingQueryCondition) {
        this.havingQueryCondition = havingQueryCondition;
    }

    protected List<QueryOrderBy> getOrderBys() {
        return orderBys;
    }

    protected void setOrderBys(List<QueryOrderBy> orderBys) {
        this.orderBys = orderBys;
    }

    protected List<UnionWrapper> getUnions() {
        return unions;
    }

    protected void setUnions(List<UnionWrapper> unions) {
        this.unions = unions;
    }

    protected Integer getLimitOffset() {
        return limitOffset;
    }

    protected void setLimitOffset(Integer limitOffset) {
        this.limitOffset = limitOffset;
    }

    protected Integer getLimitRows() {
        return limitRows;
    }

    protected void setLimitRows(Integer limitRows) {
        this.limitRows = limitRows;
    }

    protected Map<String, Object> getContext() {
        return context;
    }

    protected void setContext(Map<String, Object> context) {
        this.context = context;
    }

    protected void putContext(String key, Object value){
        if (context == null){
            context = new HashMap<>();
        }
        context.put(key,value);
    }

    protected <R> R getContext(String key){
        return context == null ? null : (R) context.get(key);
    }
}