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

import com.mybatisflex.core.table.TableDef;
import com.mybatisflex.core.util.LambdaGetter;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 抽象包装器。
 *
 * @param <T> 包装器类型
 * @author 王帅
 * @since 2023-07-21
 */
@SuppressWarnings("unchecked")
public abstract class QueryWrapperAdapter<T extends QueryWrapper> extends QueryWrapper {

    @Override
    public T select() {
        return (T) this;
    }

    @Override
    public T select(String... columns) {
        super.select(columns);
        return (T) this;
    }

    @Override
    public <E> T select(LambdaGetter<E>... lambdaGetters) {
        super.select(lambdaGetters);
        return (T) this;
    }

    @Override
    public T select(QueryColumn... queryColumns) {
        super.select(queryColumns);
        return (T) this;
    }

    @Override
    public T select(QueryColumn[]... queryColumns) {
        super.select(queryColumns);
        return (T) this;
    }

    @Override
    public T from(TableDef... tableDefs) {
        super.from(tableDefs);
        return (T) this;
    }

    @Override
    public T from(Class<?>... entityClasses) {
        super.from(entityClasses);
        return (T) this;
    }

    @Override
    public T from(String... tables) {
        super.from(tables);
        return (T) this;
    }

    @Override
    public T from(QueryTable... tables) {
        super.from(tables);
        return (T) this;
    }

    @Override
    public T from(QueryWrapper queryWrapper) {
        super.from(queryWrapper);
        return (T) this;
    }

    @Override
    public T as(String alias) {
        super.as(alias);
        return (T) this;
    }

    @Override
    public T where(QueryCondition queryCondition) {
        super.where(queryCondition);
        return (T) this;
    }

    @Override
    public T where(String sql) {
        super.where(sql);
        return (T) this;
    }

    @Override
    public T where(String sql, Object... params) {
        super.where(sql, params);
        return (T) this;
    }

    @Override
    public T where(Map<String, Object> whereConditions) {
        super.where(whereConditions);
        return (T) this;
    }

    @Override
    public T and(QueryCondition queryCondition) {
        super.and(queryCondition);
        return (T) this;
    }

    @Override
    public T and(String sql) {
        super.and(sql);
        return (T) this;
    }

    @Override
    public T and(String sql, Object... params) {
        super.and(sql, params);
        return (T) this;
    }

    @Override
    public T and(Consumer<QueryWrapper> consumer) {
        super.and(consumer);
        return (T) this;
    }

    @Override
    public T or(QueryCondition queryCondition) {
        super.or(queryCondition);
        return (T) this;
    }

    @Override
    public T or(String sql) {
        super.or(sql);
        return (T) this;
    }

    @Override
    public T or(String sql, Object... params) {
        super.or(sql, params);
        return (T) this;
    }

    @Override
    public T or(Consumer<QueryWrapper> consumer) {
        super.or(consumer);
        return (T) this;
    }

    @Override
    public T union(QueryWrapper unionQuery) {
        super.union(unionQuery);
        return (T) this;
    }

    @Override
    public T unionAll(QueryWrapper unionQuery) {
        super.unionAll(unionQuery);
        return (T) this;
    }

    @Override
    public T forUpdate() {
        super.forUpdate();
        return (T) this;
    }

    @Override
    public T forUpdateNoWait() {
        super.forUpdateNoWait();
        return (T) this;
    }

    @Override
    public T groupBy(String name) {
        super.groupBy(name);
        return (T) this;
    }

    @Override
    public T groupBy(String... names) {
        super.groupBy(names);
        return (T) this;
    }

    @Override
    public T groupBy(QueryColumn column) {
        super.groupBy(column);
        return (T) this;
    }

    @Override
    public T groupBy(QueryColumn... columns) {
        super.groupBy(columns);
        return (T) this;
    }

    @Override
    public T having(QueryCondition queryCondition) {
        super.having(queryCondition);
        return (T) this;
    }

    @Override
    public T orderBy(QueryOrderBy... orderBys) {
        super.orderBy(orderBys);
        return (T) this;
    }

    @Override
    public T orderBy(String... orderBys) {
        super.orderBy(orderBys);
        return (T) this;
    }

    @Override
    public T limit(Integer rows) {
        super.limit(rows);
        return (T) this;
    }

    @Override
    public T offset(Integer offset) {
        super.offset(offset);
        return (T) this;
    }

    @Override
    public T limit(Integer offset, Integer rows) {
        super.limit(offset, rows);
        return (T) this;
    }

    @Override
    public T datasource(String datasource) {
        super.datasource(datasource);
        return (T) this;
    }

    @Override
    public T hint(String hint) {
        super.hint(hint);
        return (T) this;
    }

}
