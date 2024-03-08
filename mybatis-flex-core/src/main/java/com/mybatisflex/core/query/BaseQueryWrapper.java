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

import com.mybatisflex.core.constant.SqlConnector;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.ObjectUtil;

import java.util.*;

@SuppressWarnings({"unchecked", "unused"})
public class BaseQueryWrapper<T extends BaseQueryWrapper<T>> implements CloneSupport<T> {


    protected With with;
    protected List<QueryTable> queryTables;
    protected String dataSource;
    protected String hint;

    protected List<QueryColumn> selectColumns;
    protected List<Join> joins;
    protected List<QueryTable> joinTables;
    protected QueryCondition whereQueryCondition;
    protected List<QueryColumn> groupByColumns;
    protected QueryCondition havingQueryCondition;
    protected List<QueryOrderBy> orderBys;

    protected List<UnionWrapper> unions;

    protected Long limitOffset;
    protected Long limitRows;

    protected List<String> endFragments;

    protected Map<String, Object> context;

//    protected boolean ignoreBlankStrings = false;

    /**
     * <p>Title: clear. </p>
     * <p>Description: Default QueryWrapper values. </p>
     * <p>Notice: When adding new attributes, it is necessary to supplement here. </p>
     *
     * @author dragon
     */
    public void clear() {
        this.with = null;
        this.queryTables = null;
        this.dataSource = null;
        this.hint = null;
        this.selectColumns = null;
        this.joins = null;
        this.joinTables = null;
        this.whereQueryCondition = null;
        this.groupByColumns = null;
        this.havingQueryCondition = null;
        this.orderBys = null;
        this.unions = null;
        this.limitOffset = null;
        this.limitRows = null;
        this.endFragments = null;
        this.context = null;
    }

    protected T addSelectColumn(QueryColumn queryColumn) {
        if (selectColumns == null) {
            selectColumns = new ArrayList<>();
        }

        selectColumns.add(queryColumn);
        return (T) this;
    }


    protected T addJoin(Join join) {
        if (joins == null) {
            joins = new ArrayList<>();
        }
        joins.add(join);
        return (T) this;
    }

    protected T setWhereQueryCondition(QueryCondition queryCondition) {
        whereQueryCondition = queryCondition;
        return (T) this;
    }

    protected T addWhereQueryCondition(QueryCondition queryCondition) {
        if (queryCondition != null) {
            if (whereQueryCondition != null) {
                queryCondition.connect(whereQueryCondition, SqlConnector.AND);
            }
            whereQueryCondition = queryCondition;
        }
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
            groupByColumns = new ArrayList<>();
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

    protected void addEndFragment(String fragment) {
        if (endFragments == null) {
            endFragments = new ArrayList<>();
        }
        endFragments.add(fragment);
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

    protected String getHint() {
        return hint;
    }

    protected void setHint(String hint) {
        this.hint = hint;
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

    protected Long getLimitOffset() {
        return limitOffset;
    }

    protected void setLimitOffset(Long limitOffset) {
        this.limitOffset = limitOffset;
    }

    protected Long getLimitRows() {
        return limitRows;
    }

    protected void setLimitRows(Long limitRows) {
        this.limitRows = limitRows;
    }

    protected List<String> getEndFragments() {
        return endFragments;
    }

    protected void setEndFragments(List<String> endFragments) {
        this.endFragments = endFragments;
    }

    protected Map<String, Object> getContext() {
        return context;
    }

    protected void setContext(Map<String, Object> context) {
        this.context = context;
    }

    protected void putContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    protected <R> R getContext(String key) {
        return context == null ? null : (R) context.get(key);
    }

    @Override
    public T clone() {
        try {
            T clone = (T) super.clone();
            // deep clone ...
            clone.with = ObjectUtil.clone(this.with);
            clone.queryTables = CollectionUtil.cloneArrayList(this.queryTables);
            clone.selectColumns = CollectionUtil.cloneArrayList(this.selectColumns);
            clone.joins = CollectionUtil.cloneArrayList(this.joins);
            clone.joinTables = CollectionUtil.cloneArrayList(this.joinTables);
            clone.whereQueryCondition = ObjectUtil.clone(this.whereQueryCondition);
            clone.groupByColumns = CollectionUtil.cloneArrayList(this.groupByColumns);
            clone.havingQueryCondition = ObjectUtil.clone(this.havingQueryCondition);
            clone.orderBys = CollectionUtil.cloneArrayList(this.orderBys);
            clone.unions = CollectionUtil.cloneArrayList(this.unions);
            // copy List if necessary ...
            if (this.endFragments != null) {
                clone.endFragments = CollectionUtil.newArrayList(this.endFragments);
            }
            // copy Map if necessary ...
            if (this.context != null) {
                clone.context = CollectionUtil.newHashMap(this.context);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }

}
