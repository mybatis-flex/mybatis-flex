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

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.query.*;
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

    @Column(ignore = true)
    private QueryWrapper queryWrapper;

    protected QueryWrapper getQueryWrapper() {
        if (queryWrapper == null) {
            queryWrapper = QueryWrapper.create();
        }
        return queryWrapper;
    }

    public T select() {
        return (T) this;
    }

    public T select(String... columns) {
        getQueryWrapper().select(columns);
        return (T) this;
    }

    public T select(QueryColumn... queryColumns) {
        getQueryWrapper().select(queryColumns);
        return (T) this;
    }

    public <E> T select(LambdaGetter<E>... columns) {
        getQueryWrapper().select(columns);
        return (T) this;
    }

    public T select(QueryColumn[]... queryColumns) {
        getQueryWrapper().select(queryColumns);
        return (T) this;
    }

    public T where(QueryCondition queryCondition) {
        getQueryWrapper().where(queryCondition);
        return (T) this;
    }

    public T where(String sql) {
        getQueryWrapper().where(sql);
        return (T) this;
    }

    public T where(String sql, Object... params) {
        getQueryWrapper().where(sql, params);
        return (T) this;
    }

    public <E> WhereBuilder<T> where(LambdaGetter<E> column) {
        return new WhereBuilder<>((T) this, LambdaUtil.getQueryColumn(column), SqlConnector.AND);
    }

    public T and(QueryCondition queryCondition) {
        getQueryWrapper().and(queryCondition);
        return (T) this;
    }

    public T and(String sql) {
        getQueryWrapper().and(sql);
        return (T) this;
    }

    public T and(String sql, Object... params) {
        getQueryWrapper().and(sql, params);
        return (T) this;
    }

    public <E> WhereBuilder<T> and(LambdaGetter<E> column) {
        return new WhereBuilder<>((T) this, LambdaUtil.getQueryColumn(column), SqlConnector.AND);
    }

    public T or(QueryCondition queryCondition) {
        getQueryWrapper().or(queryCondition);
        return (T) this;
    }

    public T or(String sql) {
        getQueryWrapper().or(sql);
        return (T) this;
    }

    public T or(String sql, Object... params) {
        getQueryWrapper().or(sql, params);
        return (T) this;
    }

    public <E> WhereBuilder<T> or(LambdaGetter<E> column) {
        return new WhereBuilder<>((T) this, LambdaUtil.getQueryColumn(column), SqlConnector.OR);
    }

    public T groupBy(String... names) {
        getQueryWrapper().groupBy(names);
        return (T) this;
    }

    public T groupBy(QueryColumn... columns) {
        getQueryWrapper().groupBy(columns);
        return (T) this;
    }

    public <E> T groupBy(LambdaGetter<E>... columns) {
        getQueryWrapper().groupBy(columns);
        return (T) this;
    }

    public T having(QueryCondition queryCondition) {
        getQueryWrapper().having(queryCondition);
        return (T) this;
    }

    public T orderBy(QueryOrderBy... orderBys) {
        getQueryWrapper().orderBy(orderBys);
        return (T) this;
    }

    public T orderBy(String... orderBys) {
        getQueryWrapper().orderBy(orderBys);
        return (T) this;
    }

    public <E> OrderByBuilder<T> orderBy(LambdaGetter<E> column) {
        return new OrderByBuilder<>((T) this, column);
    }

    public T limit(Integer rows) {
        getQueryWrapper().limit(rows);
        return (T) this;
    }

    public T offset(Integer offset) {
        getQueryWrapper().offset(offset);
        return (T) this;
    }

    public T limit(Integer offset, Integer rows) {
        getQueryWrapper().limit(offset, rows);
        return (T) this;
    }

}
