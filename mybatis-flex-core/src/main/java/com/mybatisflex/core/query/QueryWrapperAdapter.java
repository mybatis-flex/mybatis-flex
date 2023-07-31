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

import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.table.TableDef;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.Map;
import java.util.function.Consumer;

/**
 * {@link QueryWrapper} 泛型适配器。
 *
 * @param <R> 返回值类型
 * @author michael
 * @author suomm
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class QueryWrapperAdapter<R extends QueryWrapperAdapter<R>> extends QueryWrapper {

    @Override
    public WithBuilder<R> with(String name) {
        return super.with(name);
    }

    @Override
    public WithBuilder<R> with(String name, String... params) {
        return super.with(name, params);
    }

    @Override
    public WithBuilder<R> withRecursive(String name) {
        return super.withRecursive(name);
    }

    @Override
    public WithBuilder<R> withRecursive(String name, String... params) {
        return super.withRecursive(name, params);
    }

    @Override
    public R select() {
        super.select();
        return (R) this;
    }

    @Override
    public R select(String... columns) {
        super.select(columns);
        return (R) this;
    }

    @Override
    public <T> R select(LambdaGetter<T>... lambdaGetters) {
        super.select(lambdaGetters);
        return (R) this;
    }

    @Override
    public R select(QueryColumn... queryColumns) {
        super.select(queryColumns);
        return (R) this;
    }

    @Override
    public R select(QueryColumn[]... queryColumns) {
        super.select(queryColumns);
        return (R) this;
    }

    @Override
    public R select(QueryColumn[] queryColumns, QueryColumn... queryColumns2) {
        super.select(queryColumns,queryColumns2);
        return (R) this;
    }

    @Override
    public R from(TableDef... tableDefs) {
        super.from(tableDefs);
        return (R) this;
    }

    @Override
    public R from(Class<?>... entityClasses) {
        super.from(entityClasses);
        return (R) this;
    }

    @Override
    public R from(String... tables) {
        super.from(tables);
        return (R) this;
    }

    @Override
    public R from(QueryTable... tables) {
        super.from(tables);
        return (R) this;
    }

    @Override
    public R from(QueryWrapper queryWrapper) {
        super.from(queryWrapper);
        return (R) this;
    }

    @Override
    public R as(String alias) {
        super.as(alias);
        return (R) this;
    }

    @Override
    public R where(QueryCondition queryCondition) {
        super.where(queryCondition);
        return (R) this;
    }

    @Override
    public R where(String sql) {
        super.where(sql);
        return (R) this;
    }

    @Override
    public R where(String sql, Object... params) {
        super.where(sql, params);
        return (R) this;
    }

    @Override
    public R where(Map<String, Object> whereConditions) {
        super.where(whereConditions);
        return (R) this;
    }

    @Override
    public R where(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        super.where(whereConditions, operators);
        return (R) this;
    }

    @Override
    public <T> QueryConditionBuilder<R> where(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>((R) this, LambdaUtil.getQueryColumn(fn), SqlConnector.AND);
    }

    @Override
    public R where(Consumer<QueryWrapper> consumer) {
        return and(consumer);
    }

    @Override
    public R and(QueryCondition queryCondition) {
        super.and(queryCondition);
        return (R) this;
    }

    @Override
    public R and(String sql) {
        super.and(sql);
        return (R) this;
    }

    @Override
    public R and(String sql, Object... params) {
        super.and(sql, params);
        return (R) this;
    }

    @Override
    public <T> QueryConditionBuilder<R> and(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>((R) this, LambdaUtil.getQueryColumn(fn), SqlConnector.AND);
    }

    @Override
    public R and(Consumer<QueryWrapper> consumer) {
        super.and(consumer);
        return (R) this;
    }

    @Override
    public R and(Map<String, Object> whereConditions) {
        super.and(whereConditions);
        return (R) this;
    }

    @Override
    public R and(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        super.and(whereConditions, operators);
        return (R) this;
    }

    @Override
    public R and(Map<String, Object> whereConditions, Map<String, SqlOperator> operators, SqlConnector innerConnector) {
        super.and(whereConditions, operators, innerConnector);
        return (R) this;
    }

    @Override
    public R or(QueryCondition queryCondition) {
        super.or(queryCondition);
        return (R) this;
    }

    @Override
    public R or(String sql) {
        super.or(sql);
        return (R) this;
    }

    @Override
    public R or(String sql, Object... params) {
        super.or(sql, params);
        return (R) this;
    }

    @Override
    public <T> QueryConditionBuilder<R> or(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>((R) this, LambdaUtil.getQueryColumn(fn), SqlConnector.OR);
    }

    @Override
    public R or(Consumer<QueryWrapper> consumer) {
        super.or(consumer);
        return (R) this;
    }

    @Override
    public R or(Map<String, Object> whereConditions) {
        super.or(whereConditions);
        return (R) this;
    }

    @Override
    public R or(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        super.or(whereConditions, operators);
        return (R) this;
    }

    @Override
    public R or(Map<String, Object> whereConditions, Map<String, SqlOperator> operators, SqlConnector innerConnector) {
        super.or(whereConditions, operators, innerConnector);
        return (R) this;
    }

    @Override
    public Joiner<R> leftJoin(String table) {
        return super.leftJoin(table);
    }

    @Override
    public Joiner<R> leftJoin(String table, boolean when) {
        return super.leftJoin(table, when);
    }

    @Override
    public Joiner<R> leftJoin(Class entityClass) {
        return super.leftJoin(entityClass);
    }

    @Override
    public Joiner<R> leftJoin(Class entityClass, boolean when) {
        return super.leftJoin(entityClass, when);
    }

    @Override
    public Joiner<R> leftJoin(TableDef table) {
        return super.leftJoin(table);
    }

    @Override
    public Joiner<R> leftJoin(TableDef table, boolean when) {
        return super.leftJoin(table, when);
    }

    @Override
    public Joiner<R> leftJoin(QueryWrapper table) {
        return super.leftJoin(table);
    }

    @Override
    public Joiner<R> leftJoin(QueryWrapper table, boolean when) {
        return super.leftJoin(table, when);
    }

    @Override
    public Joiner<R> rightJoin(String table) {
        return super.rightJoin(table);
    }

    @Override
    public Joiner<R> rightJoin(String table, boolean when) {
        return super.rightJoin(table, when);
    }

    @Override
    public Joiner<R> rightJoin(Class entityClass) {
        return super.rightJoin(entityClass);
    }

    @Override
    public Joiner<R> rightJoin(Class entityClass, boolean when) {
        return super.rightJoin(entityClass, when);
    }

    @Override
    public Joiner<R> rightJoin(TableDef table) {
        return super.rightJoin(table);
    }

    @Override
    public Joiner<R> rightJoin(TableDef table, boolean when) {
        return super.rightJoin(table, when);
    }

    @Override
    public Joiner<R> rightJoin(QueryWrapper table) {
        return super.rightJoin(table);
    }

    @Override
    public Joiner<R> rightJoin(QueryWrapper table, boolean when) {
        return super.rightJoin(table, when);
    }

    @Override
    public Joiner<R> innerJoin(String table) {
        return super.innerJoin(table);
    }

    @Override
    public Joiner<R> innerJoin(String table, boolean when) {
        return super.innerJoin(table, when);
    }

    @Override
    public Joiner<R> innerJoin(Class entityClass) {
        return super.innerJoin(entityClass);
    }

    @Override
    public Joiner<R> innerJoin(Class entityClass, boolean when) {
        return super.innerJoin(entityClass, when);
    }

    @Override
    public Joiner<R> innerJoin(TableDef table) {
        return super.innerJoin(table);
    }

    @Override
    public Joiner<R> innerJoin(TableDef table, boolean when) {
        return super.innerJoin(table, when);
    }

    @Override
    public Joiner<R> innerJoin(QueryWrapper table) {
        return super.innerJoin(table);
    }

    @Override
    public Joiner<R> innerJoin(QueryWrapper table, boolean when) {
        return super.innerJoin(table, when);
    }

    @Override
    public Joiner<R> fullJoin(String table) {
        return super.fullJoin(table);
    }

    @Override
    public Joiner<R> fullJoin(String table, boolean when) {
        return super.fullJoin(table, when);
    }

    @Override
    public Joiner<R> fullJoin(Class entityClass) {
        return super.fullJoin(entityClass);
    }

    @Override
    public Joiner<R> fullJoin(Class entityClass, boolean when) {
        return super.fullJoin(entityClass, when);
    }

    @Override
    public Joiner<R> fullJoin(TableDef table) {
        return super.fullJoin(table);
    }

    @Override
    public Joiner<R> fullJoin(TableDef table, boolean when) {
        return super.fullJoin(table, when);
    }

    @Override
    public Joiner<R> fullJoin(QueryWrapper table) {
        return super.fullJoin(table);
    }

    @Override
    public Joiner<R> fullJoin(QueryWrapper table, boolean when) {
        return super.fullJoin(table, when);
    }

    @Override
    public Joiner<R> crossJoin(String table) {
        return super.crossJoin(table);
    }

    @Override
    public Joiner<R> crossJoin(String table, boolean when) {
        return super.crossJoin(table, when);
    }

    @Override
    public Joiner<R> crossJoin(Class entityClass) {
        return super.crossJoin(entityClass);
    }

    @Override
    public Joiner<R> crossJoin(Class entityClass, boolean when) {
        return super.crossJoin(entityClass, when);
    }

    @Override
    public Joiner<R> crossJoin(TableDef table) {
        return super.crossJoin(table);
    }

    @Override
    public Joiner<R> crossJoin(TableDef table, boolean when) {
        return super.crossJoin(table, when);
    }

    @Override
    public Joiner<R> crossJoin(QueryWrapper table) {
        return super.crossJoin(table);
    }

    @Override
    public Joiner<R> crossJoin(QueryWrapper table, boolean when) {
        return super.crossJoin(table, when);
    }

    @Override
    public Joiner<R> join(String table) {
        return super.join(table);
    }

    @Override
    public Joiner<R> join(String table, boolean when) {
        return super.join(table, when);
    }

    @Override
    public Joiner<R> join(Class entityClass) {
        return super.join(entityClass);
    }

    @Override
    public Joiner<R> join(Class entityClass, boolean when) {
        return super.join(entityClass, when);
    }

    @Override
    public Joiner<R> join(TableDef table) {
        return super.join(table);
    }

    @Override
    public Joiner<R> join(TableDef table, boolean when) {
        return super.join(table, when);
    }

    @Override
    public Joiner<R> join(QueryWrapper table) {
        return super.join(table);
    }

    @Override
    public Joiner<R> join(QueryWrapper table, boolean when) {
        return super.join(table, when);
    }

    @Override
    public R union(QueryWrapper unionQuery) {
        super.union(unionQuery);
        return (R) this;
    }

    @Override
    public R unionAll(QueryWrapper unionQuery) {
        super.unionAll(unionQuery);
        return (R) this;
    }

    @Override
    public R forUpdate() {
        super.forUpdate();
        return (R) this;
    }

    @Override
    public R forUpdateNoWait() {
        super.forUpdateNoWait();
        return (R) this;
    }

    @Override
    public R groupBy(String name) {
        super.groupBy(name);
        return (R) this;
    }

    @Override
    public R groupBy(String... names) {
        super.groupBy(names);
        return (R) this;
    }

    @Override
    public R groupBy(QueryColumn column) {
        super.groupBy(column);
        return (R) this;
    }

    @Override
    public R groupBy(QueryColumn... columns) {
        super.groupBy(columns);
        return (R) this;
    }

    @Override
    public <T> R groupBy(LambdaGetter<T> column) {
        super.groupBy(column);
        return (R) this;
    }

    @Override
    public <T> R groupBy(LambdaGetter<T>... columns) {
        super.groupBy(columns);
        return (R) this;
    }

    @Override
    public R having(QueryCondition queryCondition) {
        super.having(queryCondition);
        return (R) this;
    }

    @Override
    public R orderBy(QueryOrderBy... orderBys) {
        super.orderBy(orderBys);
        return (R) this;
    }

    @Override
    public <T> QueryOrderByBuilder<R> orderBy(LambdaGetter<T> getter) {
        return (QueryOrderByBuilder<R>) super.orderBy(getter);
    }

    @Override
    public R orderBy(String... orderBys) {
        super.orderBy(orderBys);
        return (R) this;
    }

    @Override
    public R limit(Number rows) {
        super.limit(rows);
        return (R) this;
    }

    @Override
    public R offset(Number offset) {
        super.offset(offset);
        return (R) this;
    }

    @Override
    public R limit(Number offset, Number rows) {
        super.limit(offset, rows);
        return (R) this;
    }

    @Override
    public R datasource(String datasource) {
        super.datasource(datasource);
        return (R) this;
    }

    @Override
    public R hint(String hint) {
        super.hint(hint);
        return (R) this;
    }

    @Override
    public R clone() {
        return (R) super.clone();
    }

}
