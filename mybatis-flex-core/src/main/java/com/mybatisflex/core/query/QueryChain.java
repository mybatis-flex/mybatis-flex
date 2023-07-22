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
 * @author michael
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class QueryChain extends QueryWrapper {

    @Override
    public WithBuilder<QueryChain> with(String name) {
        return (WithBuilder<QueryChain>) super.with(name);
    }

    @Override
    public WithBuilder<QueryChain> with(String name, String... params) {
        return (WithBuilder<QueryChain>) super.with(name, params);
    }

    @Override
    public WithBuilder<QueryChain> withRecursive(String name) {
        return (WithBuilder<QueryChain>) super.withRecursive(name);
    }

    @Override
    public WithBuilder<QueryChain> withRecursive(String name, String... params) {
        return (WithBuilder<QueryChain>) super.withRecursive(name, params);
    }

    @Override
    public QueryChain select() {
        super.select();
        return this;
    }

    @Override
    public QueryChain select(String... columns) {
        super.select(columns);
        return this;
    }

    @Override
    public <T> QueryWrapper select(LambdaGetter<T>... lambdaGetters) {
        super.select(lambdaGetters);
        return this;
    }

    @Override
    public QueryChain select(QueryColumn... queryColumns) {
        super.select(queryColumns);
        return this;
    }

    @Override
    public QueryChain select(QueryColumn[]... queryColumns) {
        super.select(queryColumns);
        return this;
    }

    @Override
    public QueryChain from(TableDef... tableDefs) {
        super.from(tableDefs);
        return this;
    }

    @Override
    public QueryChain from(Class<?>... entityClasses) {
        super.from(entityClasses);
        return this;
    }

    @Override
    public QueryChain from(String... tables) {
        super.from(tables);
        return this;
    }

    @Override
    public QueryChain from(QueryTable... tables) {
        super.from(tables);
        return this;
    }

    @Override
    public QueryChain from(QueryWrapper queryWrapper) {
        super.from(queryWrapper);
        return this;
    }

    @Override
    public QueryChain as(String alias) {
        super.as(alias);
        return this;
    }

    @Override
    public QueryChain where(QueryCondition queryCondition) {
        super.where(queryCondition);
        return this;
    }

    @Override
    public QueryChain where(String sql) {
        super.where(sql);
        return this;
    }

    @Override
    public QueryChain where(String sql, Object... params) {
        super.where(sql, params);
        return this;
    }

    @Override
    public QueryChain where(Map<String, Object> whereConditions) {
        super.where(whereConditions);
        return this;
    }

    @Override
    public QueryChain where(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        super.where(whereConditions, operators);
        return this;
    }

    @Override
    public <T> QueryConditionBuilder<QueryChain> where(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>(this, LambdaUtil.getQueryColumn(fn), SqlConnector.AND);
    }

    @Override
    public QueryChain and(QueryCondition queryCondition) {
        super.and(queryCondition);
        return this;
    }

    @Override
    public QueryChain and(String sql) {
        super.and(sql);
        return this;
    }

    @Override
    public QueryChain and(String sql, Object... params) {
        super.and(sql, params);
        return this;
    }

    @Override
    public <T> QueryConditionBuilder<QueryChain> and(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>(this, LambdaUtil.getQueryColumn(fn), SqlConnector.AND);
    }

    @Override
    public QueryChain and(Consumer<QueryWrapper> consumer) {
        super.and(consumer);
        return this;
    }

    @Override
    public QueryChain and(Map<String, Object> whereConditions) {
        super.and(whereConditions);
        return this;
    }

    @Override
    public QueryChain and(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        super.and(whereConditions, operators);
        return this;
    }

    @Override
    public QueryChain or(QueryCondition queryCondition) {
        super.or(queryCondition);
        return this;
    }

    @Override
    public QueryChain or(String sql) {
        super.or(sql);
        return this;
    }

    @Override
    public QueryChain or(String sql, Object... params) {
        super.or(sql, params);
        return this;
    }

    @Override
    public <T> QueryConditionBuilder<QueryChain> or(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>(this, LambdaUtil.getQueryColumn(fn), SqlConnector.OR);
    }

    @Override
    public QueryChain or(Consumer<QueryWrapper> consumer) {
        super.or(consumer);
        return this;
    }

    @Override
    public QueryChain or(Map<String, Object> whereConditions) {
        super.or(whereConditions);
        return this;
    }

    @Override
    public QueryChain or(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        super.or(whereConditions, operators);
        return this;
    }


    @Override
    public Joiner<QueryChain> leftJoin(String table) {
        return super.leftJoin(table);
    }

    @Override
    public Joiner<QueryChain> leftJoin(String table, boolean when) {
        return super.leftJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> leftJoin(Class entityClass) {
        return super.leftJoin(entityClass);
    }

    @Override
    public Joiner<QueryChain> leftJoin(Class entityClass, boolean when) {
        return super.leftJoin(entityClass, when);
    }

    @Override
    public Joiner<QueryChain> leftJoin(TableDef table) {
        return super.leftJoin(table);
    }

    @Override
    public Joiner<QueryChain> leftJoin(TableDef table, boolean when) {
        return super.leftJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> leftJoin(QueryWrapper table) {
        return super.leftJoin(table);
    }

    @Override
    public Joiner<QueryChain> leftJoin(QueryWrapper table, boolean when) {
        return super.leftJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> rightJoin(String table) {
        return super.rightJoin(table);
    }

    @Override
    public Joiner<QueryChain> rightJoin(String table, boolean when) {
        return super.rightJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> rightJoin(Class entityClass) {
        return super.rightJoin(entityClass);
    }

    @Override
    public Joiner<QueryChain> rightJoin(Class entityClass, boolean when) {
        return super.rightJoin(entityClass, when);
    }

    @Override
    public Joiner<QueryChain> rightJoin(TableDef table) {
        return super.rightJoin(table);
    }

    @Override
    public Joiner<QueryChain> rightJoin(TableDef table, boolean when) {
        return super.rightJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> rightJoin(QueryWrapper table) {
        return super.rightJoin(table);
    }

    @Override
    public Joiner<QueryChain> rightJoin(QueryWrapper table, boolean when) {
        return super.rightJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> innerJoin(String table) {
        return super.innerJoin(table);
    }

    @Override
    public Joiner<QueryChain> innerJoin(String table, boolean when) {
        return super.innerJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> innerJoin(Class entityClass) {
        return super.innerJoin(entityClass);
    }

    @Override
    public Joiner<QueryChain> innerJoin(Class entityClass, boolean when) {
        return super.innerJoin(entityClass, when);
    }

    @Override
    public Joiner<QueryChain> innerJoin(TableDef table) {
        return super.innerJoin(table);
    }

    @Override
    public Joiner<QueryChain> innerJoin(TableDef table, boolean when) {
        return super.innerJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> innerJoin(QueryWrapper table) {
        return super.innerJoin(table);
    }

    @Override
    public Joiner<QueryChain> innerJoin(QueryWrapper table, boolean when) {
        return super.innerJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> fullJoin(String table) {
        return super.fullJoin(table);
    }

    @Override
    public Joiner<QueryChain> fullJoin(String table, boolean when) {
        return super.fullJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> fullJoin(Class entityClass) {
        return super.fullJoin(entityClass);
    }

    @Override
    public Joiner<QueryChain> fullJoin(Class entityClass, boolean when) {
        return super.fullJoin(entityClass, when);
    }

    @Override
    public Joiner<QueryChain> fullJoin(TableDef table) {
        return super.fullJoin(table);
    }

    @Override
    public Joiner<QueryChain> fullJoin(TableDef table, boolean when) {
        return super.fullJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> fullJoin(QueryWrapper table) {
        return super.fullJoin(table);
    }

    @Override
    public Joiner<QueryChain> fullJoin(QueryWrapper table, boolean when) {
        return super.fullJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> crossJoin(String table) {
        return super.crossJoin(table);
    }

    @Override
    public Joiner<QueryChain> crossJoin(String table, boolean when) {
        return super.crossJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> crossJoin(Class entityClass) {
        return super.crossJoin(entityClass);
    }

    @Override
    public Joiner<QueryChain> crossJoin(Class entityClass, boolean when) {
        return super.crossJoin(entityClass, when);
    }

    @Override
    public Joiner<QueryChain> crossJoin(TableDef table) {
        return super.crossJoin(table);
    }

    @Override
    public Joiner<QueryChain> crossJoin(TableDef table, boolean when) {
        return super.crossJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> crossJoin(QueryWrapper table) {
        return super.crossJoin(table);
    }

    @Override
    public Joiner<QueryChain> crossJoin(QueryWrapper table, boolean when) {
        return super.crossJoin(table, when);
    }

    @Override
    public Joiner<QueryChain> join(String table) {
        return super.join(table);
    }

    @Override
    public Joiner<QueryChain> join(String table, boolean when) {
        return super.join(table, when);
    }

    @Override
    public Joiner<QueryChain> join(Class entityClass) {
        return super.join(entityClass);
    }

    @Override
    public Joiner<QueryChain> join(Class entityClass, boolean when) {
        return super.join(entityClass, when);
    }

    @Override
    public Joiner<QueryChain> join(TableDef table) {
        return super.join(table);
    }

    @Override
    public Joiner<QueryChain> join(TableDef table, boolean when) {
        return super.join(table, when);
    }

    @Override
    public Joiner<QueryChain> join(QueryWrapper table) {
        return super.join(table);
    }

    @Override
    public Joiner<QueryChain> join(QueryWrapper table, boolean when) {
        return super.join(table, when);
    }

    @Override
    public QueryChain union(QueryWrapper unionQuery) {
        super.union(unionQuery);
        return this;
    }

    @Override
    public QueryChain unionAll(QueryWrapper unionQuery) {
        super.unionAll(unionQuery);
        return this;
    }

    @Override
    public QueryChain forUpdate() {
        super.forUpdate();
        return this;
    }

    @Override
    public QueryChain forUpdateNoWait() {
        super.forUpdateNoWait();
        return this;
    }

    @Override
    public QueryChain groupBy(String name) {
        super.groupBy(name);
        return this;
    }

    @Override
    public QueryChain groupBy(String... names) {
        super.groupBy(names);
        return this;
    }

    @Override
    public QueryChain groupBy(QueryColumn column) {
        super.groupBy(column);
        return this;
    }

    @Override
    public QueryChain groupBy(QueryColumn... columns) {
        super.groupBy(columns);
        return this;
    }

    @Override
    public QueryChain having(QueryCondition queryCondition) {
        super.having(queryCondition);
        return this;
    }

    @Override
    public QueryChain orderBy(QueryOrderBy... orderBys) {
        super.orderBy(orderBys);
        return this;
    }

    @Override
    public QueryChain orderBy(String... orderBys) {
        super.orderBy(orderBys);
        return this;
    }

    @Override
    public QueryChain limit(Integer rows) {
        super.limit(rows);
        return this;
    }

    @Override
    public QueryChain offset(Integer offset) {
        super.offset(offset);
        return this;
    }

    @Override
    public QueryChain limit(Integer offset, Integer rows) {
        super.limit(offset, rows);
        return this;
    }

    @Override
    public QueryChain datasource(String datasource) {
        super.datasource(datasource);
        return this;
    }

    @Override
    public QueryChain hint(String hint) {
        super.hint(hint);
        return this;
    }


}
