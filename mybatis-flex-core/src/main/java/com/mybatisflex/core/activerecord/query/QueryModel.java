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

package com.mybatisflex.core.activerecord.query;

import com.mybatisflex.core.constant.SqlConnector;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

/**
 * <p>实体类条件查询构建模型。
 *
 * <p>该类内部维护了一个 {@link QueryWrapper} 属性，用来构建查询条件。
 * 通过实体类属性构建的查询条件都是值等于，该扩展用于非等于值构建，及一些其他方法。
 * 如果不想通过实体类直接构建查询条件，可以不继承该类。
 *
 * @param <T> 实体类类型
 * @author 王帅
 * @since 2023-07-24
 */
@SuppressWarnings({"unused", "unchecked"})
public abstract class QueryModel<T extends QueryModel<T>> {

    private QueryWrapper queryWrapper;

    protected QueryWrapper queryWrapper() {
        if (queryWrapper == null) {
            TableInfo tableInfo = TableInfoFactory.ofEntityClass(getClass());
            QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName());
            queryWrapper = QueryWrapper.create().from(queryTable);
        }
        return queryWrapper;
    }

    public T as(String alias) {
        queryWrapper().as(alias);
        return (T) this;
    }

    public T select() {
        return (T) this;
    }

    public T select(String... columns) {
        queryWrapper().select(columns);
        return (T) this;
    }

    public T select(QueryColumn... queryColumns) {
        queryWrapper().select(queryColumns);
        return (T) this;
    }

    public T select(Iterable<QueryColumn> queryColumns) {
        queryWrapper().select(queryColumns);
        return (T) this;
    }
    @SafeVarargs
    public final <E> T select(LambdaGetter<E>... columns) {
        queryWrapper().select(columns);
        return (T) this;
    }

    public T select(QueryColumn[]... queryColumns) {
        queryWrapper().select(queryColumns);
        return (T) this;
    }

    public T where(QueryCondition queryCondition) {
        queryWrapper().where(queryCondition);
        return (T) this;
    }

    public T where(String sql) {
        queryWrapper().where(sql);
        return (T) this;
    }

    public T where(String sql, Object... params) {
        queryWrapper().where(sql, params);
        return (T) this;
    }

    public <E> WhereBuilder<T> where(LambdaGetter<E> column) {
        return new WhereBuilder<>((T) this, LambdaUtil.getQueryColumn(column), SqlConnector.AND);
    }

    public T and(QueryCondition queryCondition) {
        queryWrapper().and(queryCondition);
        return (T) this;
    }

    public T and(String sql) {
        queryWrapper().and(sql);
        return (T) this;
    }

    public T and(String sql, Object... params) {
        queryWrapper().and(sql, params);
        return (T) this;
    }

    public <E> WhereBuilder<T> and(LambdaGetter<E> column) {
        return new WhereBuilder<>((T) this, LambdaUtil.getQueryColumn(column), SqlConnector.AND);
    }

    public T or(QueryCondition queryCondition) {
        queryWrapper().or(queryCondition);
        return (T) this;
    }

    public T or(String sql) {
        queryWrapper().or(sql);
        return (T) this;
    }

    public T or(String sql, Object... params) {
        queryWrapper().or(sql, params);
        return (T) this;
    }

    public <E> WhereBuilder<T> or(LambdaGetter<E> column) {
        return new WhereBuilder<>((T) this, LambdaUtil.getQueryColumn(column), SqlConnector.OR);
    }

    public JoinBuilder<T> leftJoin(String table) {
        return joins(SqlConsts.LEFT_JOIN, new QueryTable(table), true);
    }

    public JoinBuilder<T> leftJoin(String table, boolean when) {
        return joins(SqlConsts.LEFT_JOIN, new QueryTable(table), when);
    }

    public JoinBuilder<T> leftJoin(Class<?> entityClass) {
        return joins(SqlConsts.LEFT_JOIN, entityClass, true);
    }

    public JoinBuilder<T> leftJoin(Class<?> entityClass, boolean when) {
        return joins(SqlConsts.LEFT_JOIN, entityClass, when);
    }

    public JoinBuilder<T> leftJoin(QueryTable table) {
        return joins(SqlConsts.LEFT_JOIN, table, true);
    }

    public JoinBuilder<T> leftJoin(QueryTable table, boolean when) {
        return joins(SqlConsts.LEFT_JOIN, table, when);
    }

    public JoinBuilder<T> leftJoin(QueryWrapper table) {
        return joins(SqlConsts.LEFT_JOIN, table, true);
    }

    public JoinBuilder<T> leftJoin(QueryWrapper table, boolean when) {
        return joins(SqlConsts.LEFT_JOIN, table, when);
    }

    public JoinBuilder<T> rightJoin(String table) {
        return joins(SqlConsts.RIGHT_JOIN, new QueryTable(table), true);
    }

    public JoinBuilder<T> rightJoin(String table, boolean when) {
        return joins(SqlConsts.RIGHT_JOIN, new QueryTable(table), when);
    }

    public JoinBuilder<T> rightJoin(Class<?> entityClass) {
        return joins(SqlConsts.RIGHT_JOIN, entityClass, true);
    }

    public JoinBuilder<T> rightJoin(Class<?> entityClass, boolean when) {
        return joins(SqlConsts.RIGHT_JOIN, entityClass, when);
    }

    public JoinBuilder<T> rightJoin(QueryTable table) {
        return joins(SqlConsts.RIGHT_JOIN, table, true);
    }

    public JoinBuilder<T> rightJoin(QueryTable table, boolean when) {
        return joins(SqlConsts.RIGHT_JOIN, table, when);
    }

    public JoinBuilder<T> rightJoin(QueryWrapper table) {
        return joins(SqlConsts.RIGHT_JOIN, table, true);
    }

    public JoinBuilder<T> rightJoin(QueryWrapper table, boolean when) {
        return joins(SqlConsts.RIGHT_JOIN, table, when);
    }

    public JoinBuilder<T> innerJoin(String table) {
        return joins(SqlConsts.INNER_JOIN, new QueryTable(table), true);
    }

    public JoinBuilder<T> innerJoin(String table, boolean when) {
        return joins(SqlConsts.INNER_JOIN, new QueryTable(table), when);
    }

    public JoinBuilder<T> innerJoin(Class<?> entityClass) {
        return joins(SqlConsts.INNER_JOIN, entityClass, true);
    }

    public JoinBuilder<T> innerJoin(Class<?> entityClass, boolean when) {
        return joins(SqlConsts.INNER_JOIN, entityClass, when);
    }

    public JoinBuilder<T> innerJoin(QueryTable table) {
        return joins(SqlConsts.INNER_JOIN, table, true);
    }

    public JoinBuilder<T> innerJoin(QueryTable table, boolean when) {
        return joins(SqlConsts.INNER_JOIN, table, when);
    }

    public JoinBuilder<T> innerJoin(QueryWrapper table) {
        return joins(SqlConsts.INNER_JOIN, table, true);
    }

    public JoinBuilder<T> innerJoin(QueryWrapper table, boolean when) {
        return joins(SqlConsts.INNER_JOIN, table, when);
    }

    public JoinBuilder<T> fullJoin(String table) {
        return joins(SqlConsts.FULL_JOIN, new QueryTable(table), true);
    }

    public JoinBuilder<T> fullJoin(String table, boolean when) {
        return joins(SqlConsts.FULL_JOIN, new QueryTable(table), when);
    }

    public JoinBuilder<T> fullJoin(Class<?> entityClass) {
        return joins(SqlConsts.FULL_JOIN, entityClass, true);
    }

    public JoinBuilder<T> fullJoin(Class<?> entityClass, boolean when) {
        return joins(SqlConsts.FULL_JOIN, entityClass, when);
    }

    public JoinBuilder<T> fullJoin(QueryTable table) {
        return joins(SqlConsts.FULL_JOIN, table, true);
    }

    public JoinBuilder<T> fullJoin(QueryTable table, boolean when) {
        return joins(SqlConsts.FULL_JOIN, table, when);
    }

    public JoinBuilder<T> fullJoin(QueryWrapper table) {
        return joins(SqlConsts.FULL_JOIN, table, true);
    }

    public JoinBuilder<T> fullJoin(QueryWrapper table, boolean when) {
        return joins(SqlConsts.FULL_JOIN, table, when);
    }

    public JoinBuilder<T> crossJoin(String table) {
        return joins(SqlConsts.CROSS_JOIN, new QueryTable(table), true);
    }

    public JoinBuilder<T> crossJoin(String table, boolean when) {
        return joins(SqlConsts.CROSS_JOIN, new QueryTable(table), when);
    }

    public JoinBuilder<T> crossJoin(Class<?> entityClass) {
        return joins(SqlConsts.CROSS_JOIN, entityClass, true);
    }

    public JoinBuilder<T> crossJoin(Class<?> entityClass, boolean when) {
        return joins(SqlConsts.CROSS_JOIN, entityClass, when);
    }

    public JoinBuilder<T> crossJoin(QueryTable table) {
        return joins(SqlConsts.CROSS_JOIN, table, true);
    }

    public JoinBuilder<T> crossJoin(QueryTable table, boolean when) {
        return joins(SqlConsts.CROSS_JOIN, table, when);
    }

    public JoinBuilder<T> crossJoin(QueryWrapper table) {
        return joins(SqlConsts.CROSS_JOIN, table, true);
    }

    public JoinBuilder<T> crossJoin(QueryWrapper table, boolean when) {
        return joins(SqlConsts.CROSS_JOIN, table, when);
    }

    public JoinBuilder<T> join(String table) {
        return joins(SqlConsts.JOIN, new QueryTable(table), true);
    }

    public JoinBuilder<T> join(String table, boolean when) {
        return joins(SqlConsts.JOIN, new QueryTable(table), when);
    }

    public JoinBuilder<T> join(Class<?> entityClass) {
        return joins(SqlConsts.JOIN, entityClass, true);
    }

    public JoinBuilder<T> join(Class<?> entityClass, boolean when) {
        return joins(SqlConsts.JOIN, entityClass, when);
    }

    public JoinBuilder<T> join(QueryTable table) {
        return joins(SqlConsts.JOIN, table, true);
    }

    public JoinBuilder<T> join(QueryTable table, boolean when) {
        return joins(SqlConsts.JOIN, table, when);
    }

    public JoinBuilder<T> join(QueryWrapper table) {
        return joins(SqlConsts.JOIN, table, true);
    }

    public JoinBuilder<T> join(QueryWrapper table, boolean when) {
        return joins(SqlConsts.JOIN, table, when);
    }

    public T groupBy(String... names) {
        queryWrapper().groupBy(names);
        return (T) this;
    }

    public T groupBy(QueryColumn... columns) {
        queryWrapper().groupBy(columns);
        return (T) this;
    }
    @SafeVarargs
    public final  <E> T groupBy(LambdaGetter<E>... columns) {
        queryWrapper().groupBy(columns);
        return (T) this;
    }

    public T having(QueryCondition queryCondition) {
        queryWrapper().having(queryCondition);
        return (T) this;
    }

    public T orderBy(QueryOrderBy... orderBys) {
        queryWrapper().orderBy(orderBys);
        return (T) this;
    }

    public T orderBy(QueryColumn column, Boolean asc) {
        queryWrapper().orderBy(column, asc);
        return (T) this;
    }

    public T orderBy(String... orderBys) {
        queryWrapper().orderBy(orderBys);
        return (T) this;
    }

    public T orderBy(String column, Boolean asc) {
        queryWrapper().orderBy(column, asc);
        return (T) this;
    }

    public <E> OrderByBuilder<T> orderBy(LambdaGetter<E> column) {
        return new OrderByBuilder<>((T) this, column);
    }

    public <E> T orderBy(LambdaGetter<E> column, Boolean asc) {
        queryWrapper().orderBy(column, asc);
        return (T) this;
    }

    public T limit(Number rows) {
        queryWrapper().limit(rows);
        return (T) this;
    }

    public T offset(Number offset) {
        queryWrapper().offset(offset);
        return (T) this;
    }

    public T limit(Number offset, Number rows) {
        queryWrapper().limit(offset, rows);
        return (T) this;
    }

    protected JoinBuilder<T> joins(String type, QueryTable table, boolean when) {
        Join join = new Join(type, table, when);
        CPI.addJoin(queryWrapper(), join);
        return new JoinBuilder<>((T) this, join);
    }

    protected JoinBuilder<T> joins(String type, Class<?> entityClass, boolean when) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName());
        return joins(type, queryTable, when);
    }

    protected JoinBuilder<T> joins(String type, QueryWrapper queryWrapper, boolean when) {
        Join join = new Join(type, queryWrapper, when);
        CPI.addJoin(queryWrapper(), join);
        return new JoinBuilder<>((T) this, join);
    }

}
