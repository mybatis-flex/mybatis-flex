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
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.Collection;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
    public R select(Iterable<QueryColumn> queryColumns) {
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
        super.select(queryColumns, queryColumns2);
        return (R) this;
    }

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
    public R where(Map<String, Object> whereConditions, SqlOperators operators) {
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
    public R and(Map<String, Object> whereConditions, SqlOperators operators) {
        super.and(whereConditions, operators);
        return (R) this;
    }

    @Override
    public R and(Map<String, Object> whereConditions, SqlOperators operators, SqlConnector innerConnector) {
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
    public R or(Map<String, Object> whereConditions, SqlOperators operators) {
        super.or(whereConditions, operators);
        return (R) this;
    }

    @Override
    public R or(Map<String, Object> whereConditions, SqlOperators operators, SqlConnector innerConnector) {
        super.or(whereConditions, operators, innerConnector);
        return (R) this;
    }

    @Override
    public Joiner<R> leftJoin(QueryTable table) {
        return super.leftJoin(table);
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
    public Joiner<R> leftJoin(QueryWrapper table) {
        return super.leftJoin(table);
    }

    @Override
    public Joiner<R> leftJoin(QueryWrapper table, boolean when) {
        return super.leftJoin(table, when);
    }

    @Override
    public Joiner<R> rightJoin(QueryTable table) {
        return super.rightJoin(table);
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
    public Joiner<R> rightJoin(QueryWrapper table) {
        return super.rightJoin(table);
    }

    @Override
    public Joiner<R> rightJoin(QueryWrapper table, boolean when) {
        return super.rightJoin(table, when);
    }

    @Override
    public Joiner<R> innerJoin(QueryTable table) {
        return super.innerJoin(table);
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
    public Joiner<R> innerJoin(QueryWrapper table) {
        return super.innerJoin(table);
    }

    @Override
    public Joiner<R> innerJoin(QueryWrapper table, boolean when) {
        return super.innerJoin(table, when);
    }

    @Override
    public Joiner<R> fullJoin(QueryTable table) {
        return super.fullJoin(table);
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
    public Joiner<R> fullJoin(QueryWrapper table) {
        return super.fullJoin(table);
    }

    @Override
    public Joiner<R> fullJoin(QueryWrapper table, boolean when) {
        return super.fullJoin(table, when);
    }

    @Override
    public Joiner<R> crossJoin(QueryTable table) {
        return super.crossJoin(table);
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
    public Joiner<R> crossJoin(QueryWrapper table) {
        return super.crossJoin(table);
    }

    @Override
    public Joiner<R> crossJoin(QueryWrapper table, boolean when) {
        return super.crossJoin(table, when);
    }

    @Override
    public Joiner<R> join(QueryTable table) {
        return super.join(table);
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
    public R orderBy(QueryColumn column, Boolean asc) {
        super.orderBy(column, asc);
        return (R) this;
    }

    @Override
    public R orderBy(QueryOrderBy... orderBys) {
        super.orderBy(orderBys);
        return (R) this;
    }

    @Override
    public <T> R orderBy(LambdaGetter<T> column, Boolean asc) {
        super.orderBy(column, asc);
        return (R) this;
    }

    @Override
    public <T> QueryOrderByBuilder<R> orderBy(LambdaGetter<T> getter) {
        return (QueryOrderByBuilder<R>) super.orderBy(getter);
    }

    @Override
    public R orderBy(String column, Boolean asc) {
        super.orderBy(column, asc);
        return (R) this;
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


    /////////MyBatis-Plus 兼容方法///////////////

    /**
     * 等于 {@code =}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R eq(String column, Object value) {
        and(QueryMethods.column(column).eq(value));
        return (R) this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R eq(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).eq(value));
        return (R) this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R eq(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).eq(value).when(isEffective));
        return (R) this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R eq(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).eq(value).when(isEffective));
        return (R) this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R eq(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).eq(value, isEffective));
        return (R) this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R eq(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).eq(value, isEffective));
        return (R) this;
    }


    /**
     * 不等于 {@code !=}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R ne(String column, Object value) {
        and(QueryMethods.column(column).ne(value));
        return (R) this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R ne(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).ne(value));
        return (R) this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R ne(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).ne(value).when(isEffective));
        return (R) this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R ne(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).ne(value).when(isEffective));
        return (R) this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R ne(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).ne(value, isEffective));
        return (R) this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R ne(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).ne(value, isEffective));
        return (R) this;
    }


    /**
     * 大于 {@code >}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R gt(String column, Object value) {
        and(QueryMethods.column(column).gt(value));
        return (R) this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R gt(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).gt(value));
        return (R) this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R gt(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).gt(value).when(isEffective));
        return (R) this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R gt(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).gt(value).when(isEffective));
        return (R) this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R gt(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).gt(value, isEffective));
        return (R) this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R gt(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).gt(value, isEffective));
        return (R) this;
    }


    /**
     * 大于等于 {@code >=}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R ge(String column, Object value) {
        and(QueryMethods.column(column).ge(value));
        return (R) this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R ge(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).ge(value));
        return (R) this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R ge(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).ge(value).when(isEffective));
        return (R) this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R ge(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).ge(value).when(isEffective));
        return (R) this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R ge(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).ge(value, isEffective));
        return (R) this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R ge(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).ge(value, isEffective));
        return (R) this;
    }


    /**
     * 小于 {@code <}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R lt(String column, Object value) {
        and(QueryMethods.column(column).lt(value));
        return (R) this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R lt(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).lt(value));
        return (R) this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R lt(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).lt(value).when(isEffective));
        return (R) this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R lt(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).lt(value).when(isEffective));
        return (R) this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R lt(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).lt(value, isEffective));
        return (R) this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R lt(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).lt(value, isEffective));
        return (R) this;
    }


    /**
     * 小于等于 {@code <=}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R le(String column, Object value) {
        and(QueryMethods.column(column).le(value));
        return (R) this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R le(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).le(value));
        return (R) this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R le(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).le(value).when(isEffective));
        return (R) this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R le(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).le(value).when(isEffective));
        return (R) this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R le(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).le(value, isEffective));
        return (R) this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R le(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).le(value, isEffective));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    @Override
    public R in(String column, Object... values) {
        and(QueryMethods.column(column).in(values));
        return (R) this;
    }

    /**
     * {@code IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    @Override
    public <T> R in(LambdaGetter<T> column, Object... values) {
        and(QueryMethods.column(column).in(values));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    @Override
    public R in(String column, QueryWrapper queryWrapper) {
        and(QueryMethods.column(column).in(queryWrapper));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    @Override
    public <T> R in(LambdaGetter<T> column, QueryWrapper queryWrapper) {
        and(QueryMethods.column(column).in(queryWrapper));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    @Override
    public R in(String column, Collection<?> values) {
        and(QueryMethods.column(column).in(values));
        return (R) this;
    }

    /**
     * {@code IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    @Override
    public <T> R in(LambdaGetter<T> column, Collection<?> values) {
        and(QueryMethods.column(column).in(values));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    @Override
    public R in(String column, Object[] values, boolean isEffective) {
        and(QueryMethods.column(column).in(values, isEffective));
        return (R) this;
    }

    /**
     * {@code IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    @Override
    public <T> R in(LambdaGetter<T> column, Object[] values, boolean isEffective) {
        and(QueryMethods.column(column).in(values, isEffective));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    @Override
    public R in(String column, Collection<?> values, boolean isEffective) {
        and(QueryMethods.column(column).in(values, isEffective));
        return (R) this;
    }

    /**
     * {@code IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    @Override
    public <T> R in(LambdaGetter<T> column, Collection<?> values, boolean isEffective) {
        and(QueryMethods.column(column).in(values, isEffective));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    @Override
    public R in(String column, QueryWrapper queryWrapper, boolean isEffective) {
        and(QueryMethods.column(column).in(queryWrapper, isEffective));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    @Override
    public <T> R in(LambdaGetter<T> column, QueryWrapper queryWrapper, boolean isEffective) {
        and(QueryMethods.column(column).in(queryWrapper, isEffective));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    @Override
    public R in(String column, QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).in(queryWrapper, isEffective));
        return (R) this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    @Override
    public <T> R in(LambdaGetter<T> column, QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).in(queryWrapper, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    @Override
    public R notIn(String column, Object... values) {
        and(QueryMethods.column(column).notIn(values));
        return (R) this;
    }

    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    @Override
    public <T> R notIn(LambdaGetter<T> column, Object... values) {
        and(QueryMethods.column(column).notIn(values));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    @Override
    public R notIn(String column, QueryWrapper queryWrapper) {
        and(QueryMethods.column(column).notIn(queryWrapper));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    @Override
    public <T> R notIn(LambdaGetter<T> column, QueryWrapper queryWrapper) {
        and(QueryMethods.column(column).notIn(queryWrapper));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    @Override
    public R notIn(String column, Collection<?> values) {
        and(QueryMethods.column(column).notIn(values));
        return (R) this;
    }

    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    @Override
    public <T> R notIn(LambdaGetter<T> column, Collection<?> values) {
        and(QueryMethods.column(column).notIn(values));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    @Override
    public R notIn(String column, Object[] values, boolean isEffective) {
        and(QueryMethods.column(column).notIn(values, isEffective));
        return (R) this;
    }

    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    @Override
    public <T> R notIn(LambdaGetter<T> column, Object[] values, boolean isEffective) {
        and(QueryMethods.column(column).notIn(values, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    @Override
    public R notIn(String column, Collection<?> values, boolean isEffective) {
        and(QueryMethods.column(column).notIn(values, isEffective));
        return (R) this;
    }

    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    @Override
    public <T> R notIn(LambdaGetter<T> column, Collection<?> values, boolean isEffective) {
        and(QueryMethods.column(column).notIn(values, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    @Override
    public R notIn(String column, QueryWrapper queryWrapper, boolean isEffective) {
        and(QueryMethods.column(column).notIn(queryWrapper, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    @Override
    public <T> R notIn(LambdaGetter<T> column, QueryWrapper queryWrapper, boolean isEffective) {
        and(QueryMethods.column(column).notIn(queryWrapper, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    @Override
    public R notIn(String column, QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).notIn(queryWrapper, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    @Override
    public <T> R notIn(LambdaGetter<T> column, QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).notIn(queryWrapper, isEffective));
        return (R) this;
    }


    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public R between(String column, Object start, Object end) {
        and(QueryMethods.column(column).between(start, end));
        return (R) this;
    }

    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public <T> R between(LambdaGetter<T> column, Object start, Object end) {
        and(QueryMethods.column(column).between(start, end));
        return (R) this;
    }

    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public R between(String column, Object start, Object end, boolean isEffective) {
        and(QueryMethods.column(column).between(start, end, isEffective));
        return (R) this;
    }

    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public <T> R between(LambdaGetter<T> column, Object start, Object end, boolean isEffective) {
        and(QueryMethods.column(column).between(start, end, isEffective));
        return (R) this;
    }


    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public R between(String column, Object start, Object end, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).between(start, end, isEffective));
        return (R) this;
    }

    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public <T> R between(LambdaGetter<T> column, Object start, Object end, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).between(start, end, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public R notBetween(String column, Object start, Object end) {
        and(QueryMethods.column(column).notBetween(start, end));
        return (R) this;
    }

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public <T> R notBetween(LambdaGetter<T> column, Object start, Object end) {
        and(QueryMethods.column(column).notBetween(start, end));
        return (R) this;
    }

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public R notBetween(String column, Object start, Object end, boolean isEffective) {
        and(QueryMethods.column(column).notBetween(start, end, isEffective));
        return (R) this;
    }

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public <T> R notBetween(LambdaGetter<T> column, Object start, Object end, boolean isEffective) {
        and(QueryMethods.column(column).notBetween(start, end, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public R notBetween(String column, Object start, Object end, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).notBetween(start, end, isEffective));
        return (R) this;
    }

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    @Override
    public <T> R notBetween(LambdaGetter<T> column, Object start, Object end, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).notBetween(start, end, isEffective));
        return (R) this;
    }


    /**
     * {@code LIKE %value%}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R like(String column, Object value) {
        and(QueryMethods.column(column).like(value));
        return (R) this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R like(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).like(value));
        return (R) this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R like(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).like(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R like(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).like(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R like(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).like(value, isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R like(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).like(value, isEffective));
        return (R) this;
    }


    /**
     * {@code LIKE value%}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R likeLeft(String column, Object value) {
        and(QueryMethods.column(column).likeLeft(value));
        return (R) this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R likeLeft(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).likeLeft(value));
        return (R) this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R likeLeft(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).likeLeft(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R likeLeft(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).likeLeft(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R likeLeft(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).likeLeft(value, isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R likeLeft(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).likeLeft(value, isEffective));
        return (R) this;
    }


    /**
     * {@code LIKE %value}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R likeRight(String column, Object value) {
        and(QueryMethods.column(column).likeRight(value));
        return (R) this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R likeRight(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).likeRight(value));
        return (R) this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R likeRight(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).likeRight(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R likeRight(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).likeRight(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R likeRight(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).likeRight(value, isEffective));
        return (R) this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R likeRight(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).likeRight(value, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT LIKE %value%}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R notLike(String column, Object value) {
        and(QueryMethods.column(column).notLike(value));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R notLike(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).notLike(value));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R notLike(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLike(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R notLike(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLike(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R notLike(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLike(value, isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R notLike(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLike(value, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT LIKE value%}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R notLikeLeft(String column, Object value) {
        and(QueryMethods.column(column).notLikeLeft(value));
        return (R) this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R notLikeLeft(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).notLikeLeft(value));
        return (R) this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R notLikeLeft(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLikeLeft(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R notLikeLeft(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLikeLeft(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R notLikeLeft(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLikeLeft(value, isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R notLikeLeft(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLikeLeft(value, isEffective));
        return (R) this;
    }


    /**
     * {@code NOT LIKE %value}
     *
     * @param column 列名
     * @param value  条件的值
     */
    @Override
    public R notLikeRight(String column, Object value) {
        and(QueryMethods.column(column).notLikeRight(value));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    @Override
    public <T> R notLikeRight(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).notLikeRight(value));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public R notLikeRight(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLikeRight(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T> R notLikeRight(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLikeRight(value).when(isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    @Override
    public <V> R notLikeRight(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLikeRight(value, isEffective));
        return (R) this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    @Override
    public <T, V> R notLikeRight(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLikeRight(value, isEffective));
        return (R) this;
    }


    /**
     * {@code IS NULL}
     *
     * @param column 列名
     */
    @Override
    public R isNull(String column) {
        and(QueryMethods.column(column).isNull());
        return (R) this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column 列名, lambda 展示
     */
    @Override
    public <T> R isNull(LambdaGetter<T> column) {
        and(QueryMethods.column(column).isNull());
        return (R) this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column      列名
     * @param isEffective 是否有效
     */
    @Override
    public R isNull(String column, boolean isEffective) {
        and(QueryMethods.column(column).isNull(isEffective));
        return (R) this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column      列名, lambda 展示
     * @param isEffective 是否有效
     */
    @Override
    public <T> R isNull(LambdaGetter<T> column, boolean isEffective) {
        and(QueryMethods.column(column).isNull(isEffective));
        return (R) this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column      列名
     * @param isEffective 是否有效
     */
    @Override
    public R isNull(String column, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).isNull(isEffective));
        return (R) this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column      列名, lambda 展示
     * @param isEffective 是否有效
     */
    @Override
    public <T> R isNull(LambdaGetter<T> column, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).isNull(isEffective));
        return (R) this;
    }


    /**
     * {@code IS NOT NULL}
     *
     * @param column 列名
     */
    @Override
    public R isNotNull(String column) {
        and(QueryMethods.column(column).isNotNull());
        return (R) this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column 列名, lambda 展示
     */
    @Override
    public <T> R isNotNull(LambdaGetter<T> column) {
        and(QueryMethods.column(column).isNotNull());
        return (R) this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column      列名
     * @param isEffective 是否有效
     */
    @Override
    public R isNotNull(String column, boolean isEffective) {
        and(QueryMethods.column(column).isNotNull(isEffective));
        return (R) this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column      列名, lambda 展示
     * @param isEffective 是否有效
     */
    @Override
    public <T> R isNotNull(LambdaGetter<T> column, boolean isEffective) {
        and(QueryMethods.column(column).isNotNull(isEffective));
        return (R) this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column      列名
     * @param isEffective 是否有效
     */
    @Override
    public R isNotNull(String column, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).isNotNull(isEffective));
        return (R) this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column      列名, lambda 展示
     * @param isEffective 是否有效
     */
    @Override
    public <T> R isNotNull(LambdaGetter<T> column, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).isNotNull(isEffective));
        return (R) this;
    }


    @Override
    public R clone() {
        return (R) super.clone();
    }

}
