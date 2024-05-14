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


import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.SqlUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * 查询列，描述的是一张表的字段
 */
public class QueryColumn implements CloneSupport<QueryColumn>, Conditional<QueryCondition> {

    protected QueryTable table;
    protected String name;
    protected String alias;

    private boolean returnCopyByAsMethod = false;


    public QueryColumn() {
    }

    public QueryColumn(String name) {
        SqlUtil.keepColumnSafely(name);
        this.name = StringUtil.tryTrim(name);
    }

    public QueryColumn(String tableName, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = new QueryTable(tableName);
        this.name = StringUtil.tryTrim(name);
    }

    public QueryColumn(String schema, String tableName, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = new QueryTable(schema, tableName);
        this.name = StringUtil.tryTrim(name);
    }

    public QueryColumn(String schema, String tableName, String name, String alias) {
        SqlUtil.keepColumnSafely(name);
        this.returnCopyByAsMethod = true;
        this.table = new QueryTable(schema, tableName);
        this.name = StringUtil.tryTrim(name);
        this.alias = StringUtil.tryTrim(alias);
    }

    public QueryColumn(QueryTable queryTable, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = queryTable;
        this.name = StringUtil.tryTrim(name);
        this.returnCopyByAsMethod = true;
    }

    public QueryColumn(QueryTable queryTable, String name, String alias) {
        SqlUtil.keepColumnSafely(name);
        this.returnCopyByAsMethod = true;
        this.table = queryTable;
        this.name = StringUtil.tryTrim(name);
        this.alias = StringUtil.tryTrim(alias);
    }

    public QueryTable getTable() {
        return table;
    }

    public void setTable(QueryTable table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public <T> QueryColumn as(LambdaGetter<T> fn) {
        return as(fn, false);
    }

    public <T> QueryColumn as(LambdaGetter<T> fn, boolean withPrefix) {
        return as(LambdaUtil.getAliasName(fn, withPrefix));
    }

    public QueryColumn as(String alias) {
        SqlUtil.keepColumnSafely(alias);
        if (returnCopyByAsMethod) {
            QueryColumn newColumn = new QueryColumn();
            newColumn.table = this.table;
            newColumn.name = this.name;
            newColumn.alias = alias;
            return newColumn;
        } else {
            this.alias = alias;
            return this;
        }
    }


    // query methods ///////

    @Override
    public QueryCondition eq(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value));
    }

    @Override
    public QueryCondition eq(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value).when(isEffective));
    }

    @Override
    public QueryCondition eq(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value).when(isEffective)));
    }

    @Override
    public <T> QueryCondition eq(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value).when(isEffective.test(value)));
    }

    @Override
    public QueryCondition ne(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value));
    }

    @Override
    public QueryCondition ne(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value).when(isEffective));
    }

    @Override
    public QueryCondition ne(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value).when(isEffective));
    }

    @Override
    public <T> QueryCondition ne(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value).when(isEffective.test(value)));
    }

    @Override
    public QueryCondition gt(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GT, value));
    }

    @Override
    public QueryCondition gt(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GT, value).when(isEffective));
    }

    @Override
    public QueryCondition gt(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GT, value).when(isEffective));
    }

    @Override
    public <T> QueryCondition gt(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GT, value).when(isEffective.test(value)));
    }

    @Override
    public QueryCondition ge(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GE, value));
    }

    @Override
    public QueryCondition ge(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GE, value).when(isEffective));
    }

    @Override
    public QueryCondition ge(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GE, value).when(isEffective));
    }

    @Override
    public <T> QueryCondition ge(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GE, value).when(isEffective.test(value)));
    }

    @Override
    public QueryCondition lt(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LT, value));
    }

    @Override
    public QueryCondition lt(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LT, value).when(isEffective));
    }

    @Override
    public QueryCondition lt(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LT, value).when(isEffective));
    }

    @Override
    public <T> QueryCondition lt(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LT, value).when(isEffective.test(value)));
    }

    @Override
    public QueryCondition le(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LE, value));
    }

    @Override
    public QueryCondition le(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LE, value).when(isEffective));
    }

    @Override
    public QueryCondition le(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LE, value).when(isEffective));
    }

    @Override
    public <T> QueryCondition le(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LE, value).when(isEffective.test(value)));
    }

    @Override
    public QueryCondition in(Object... value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value) || value.length == 0) {
            return QueryCondition.createEmpty();
        }
        // IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.shouldIgnoreValue(value[0])) {
                return QueryCondition.createEmpty();
            }
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value[0]));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.IN, value));
    }

    @Override
    public QueryCondition in(Object[] value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value) || value.length == 0) {
            return QueryCondition.createEmpty();
        }
        // IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.shouldIgnoreValue(value[0])) {
                return QueryCondition.createEmpty();
            }
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value[0]).when(isEffective));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.IN, value).when(isEffective));
    }

    @Override
    public QueryCondition in(Object[] value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value) || value.length == 0) {
            return QueryCondition.createEmpty();
        }
        // IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.shouldIgnoreValue(value[0])) {
                return QueryCondition.createEmpty();
            }
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value[0]).when(isEffective));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.IN, value).when(isEffective));
    }

    @Override
    public <T> QueryCondition in(T[] value, Predicate<T[]> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value) || value.length == 0) {
            return QueryCondition.createEmpty();
        }
        // IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.shouldIgnoreValue(value[0])) {
                return QueryCondition.createEmpty();
            }
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value[0]).when(isEffective.test(value)));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.IN, value).when(isEffective.test(value)));
    }

    @Override
    public QueryCondition in(Collection<?> value) {
        if (value == null || value.isEmpty()) {
            return QueryCondition.createEmpty();
        }
        return in(value.toArray());
    }

    @Override
    public QueryCondition in(Collection<?> value, boolean isEffective) {
        if (value == null || value.isEmpty()) {
            return QueryCondition.createEmpty();
        }
        return in(value.toArray()).when(isEffective);
    }

    @Override
    public QueryCondition in(Collection<?> value, BooleanSupplier isEffective) {
        if (value == null || value.isEmpty()) {
            return QueryCondition.createEmpty();
        }
        return in(value.toArray()).when(isEffective);
    }

    @Override
    public <T extends Collection<?>> QueryCondition in(T value, Predicate<T> isEffective) {
        if (value == null || value.isEmpty()) {
            return QueryCondition.createEmpty();
        }
        return in(value.toArray()).when(isEffective.test(value));
    }

    @Override
    public QueryCondition in(QueryWrapper queryWrapper) {
        if (queryWrapper == null) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.IN, queryWrapper));
    }

    @Override
    public QueryCondition in(QueryWrapper queryWrapper, boolean isEffective) {
        if (queryWrapper == null) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.IN, queryWrapper).when(isEffective));
    }

    @Override
    public QueryCondition in(QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        if (queryWrapper == null) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.IN, queryWrapper).when(isEffective));
    }

    @Override
    public QueryCondition notIn(Object... value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value) || value.length == 0) {
            return QueryCondition.createEmpty();
        }
        // NOT IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.shouldIgnoreValue(value[0])) {
                return QueryCondition.createEmpty();
            }
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value[0]));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_IN, value));
    }

    @Override
    public QueryCondition notIn(Object[] value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value) || value.length == 0) {
            return QueryCondition.createEmpty();
        }
        // NOT IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.shouldIgnoreValue(value[0])) {
                return QueryCondition.createEmpty();
            }
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value[0]).when(isEffective));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_IN, value).when(isEffective));
    }

    @Override
    public QueryCondition notIn(Object[] value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value) || value.length == 0) {
            return QueryCondition.createEmpty();
        }
        // NOT IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.shouldIgnoreValue(value[0])) {
                return QueryCondition.createEmpty();
            }
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value[0]).when(isEffective));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_IN, value).when(isEffective));
    }

    @Override
    public <T> QueryCondition notIn(T[] value, Predicate<T[]> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value) || value.length == 0) {
            return QueryCondition.createEmpty();
        }
        // NOT IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.shouldIgnoreValue(value[0])) {
                return QueryCondition.createEmpty();
            }
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value[0]).when(isEffective.test(value)));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_IN, value).when(isEffective.test(value)));
    }

    @Override
    public QueryCondition notIn(Collection<?> value) {
        if (value == null || value.isEmpty()) {
            return QueryCondition.createEmpty();
        }
        return notIn(value.toArray());
    }

    @Override
    public QueryCondition notIn(Collection<?> value, boolean isEffective) {
        if (value == null || value.isEmpty()) {
            return QueryCondition.createEmpty();
        }
        return notIn(value.toArray()).when(isEffective);
    }

    @Override
    public QueryCondition notIn(Collection<?> value, BooleanSupplier isEffective) {
        if (value == null || value.isEmpty()) {
            return QueryCondition.createEmpty();
        }
        return notIn(value.toArray()).when(isEffective);
    }

    @Override
    public <T extends Collection<?>> QueryCondition notIn(T value, Predicate<T> isEffective) {
        if (value == null || value.isEmpty()) {
            return QueryCondition.createEmpty();
        }
        return notIn(value.toArray()).when(isEffective.test(value));
    }

    @Override
    public QueryCondition notIn(QueryWrapper queryWrapper) {
        if (queryWrapper == null) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_IN, queryWrapper));
    }

    @Override
    public QueryCondition notIn(QueryWrapper queryWrapper, boolean isEffective) {
        if (queryWrapper == null) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_IN, queryWrapper).when(isEffective));
    }

    @Override
    public QueryCondition notIn(QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        if (queryWrapper == null) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_IN, queryWrapper).when(isEffective));
    }

    @Override
    public QueryCondition between(Object[] values) {
        if (QueryColumnBehavior.shouldIgnoreValue(values) || values.length < 2) {
            return QueryCondition.createEmpty();
        }

       return between(values[0], values[values.length - 1]);
    }

    @Override
    public QueryCondition between(Object[] values, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(values) || values.length < 2) {
            return QueryCondition.createEmpty();
        }

        return between(values[0], values[values.length - 1], isEffective);
    }

    @Override
    public QueryCondition between(Object start, Object end) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.BETWEEN, new Object[]{start, end}));
    }

    @Override
    public QueryCondition between(Object start, Object end, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.BETWEEN, new Object[]{start, end}).when(isEffective));
    }

    @Override
    public QueryCondition between(Object start, Object end, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.BETWEEN, new Object[]{start, end}).when(isEffective));
    }

    @Override
    public <S, E> QueryCondition between(S start, E end, BiPredicate<S, E> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.BETWEEN, new Object[]{start, end}).when(isEffective.test(start, end)));
    }

    @Override
    public QueryCondition notBetween(Object[] values) {
        if (QueryColumnBehavior.shouldIgnoreValue(values) || values.length < 2) {
            return QueryCondition.createEmpty();
        }

        return notBetween(values[0], values[values.length - 1]);
    }

    @Override
    public QueryCondition notBetween(Object[] values, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(values) || values.length < 2) {
            return QueryCondition.createEmpty();
        }

        return notBetween(values[0], values[values.length - 1], isEffective);
    }

    @Override
    public QueryCondition notBetween(Object start, Object end) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_BETWEEN, new Object[]{start, end}));
    }

    @Override
    public QueryCondition notBetween(Object start, Object end, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_BETWEEN, new Object[]{start, end}).when(isEffective));
    }

    @Override
    public QueryCondition notBetween(Object start, Object end, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_BETWEEN, new Object[]{start, end}).when(isEffective));
    }

    @Override
    public <S, E> QueryCondition notBetween(S start, E end, BiPredicate<S, E> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_BETWEEN, new Object[]{start, end}).when(isEffective.test(start, end)));
    }

    @Override
    public QueryCondition like(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value + "%"));
    }

    @Override
    public QueryCondition like(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value + "%").when(isEffective));
    }

    @Override
    public QueryCondition like(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value + "%").when(isEffective));
    }

    @Override
    public <T> QueryCondition like(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value + "%").when(isEffective.test(value)));
    }

    @Override
    public QueryCondition likeLeft(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, value + "%"));
    }

    @Override
    public QueryCondition likeLeft(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, value + "%").when(isEffective));
    }

    @Override
    public QueryCondition likeLeft(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, value + "%").when(isEffective));
    }

    @Override
    public <T> QueryCondition likeLeft(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, value + "%").when(isEffective.test(value)));
    }

    @Override
    public QueryCondition likeRight(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value));
    }

    @Override
    public QueryCondition likeRight(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value).when(isEffective));
    }

    @Override
    public QueryCondition likeRight(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value).when(isEffective));
    }

    @Override
    public <T> QueryCondition likeRight(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value).when(isEffective.test(value)));
    }

    /**
     * {@code LIKE value}
     */
    public QueryCondition likeRaw(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeRaw(value, true);
    }

    /**
     * {@code LIKE value}
     */
    public QueryCondition likeRaw(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, value).when(isEffective));
    }

    /**
     * {@code LIKE value}
     */
    public QueryCondition likeRaw(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeRaw(value, isEffective.getAsBoolean());
    }

    /**
     * {@code LIKE value}
     */
    public <T> QueryCondition likeRaw(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeRaw(value, isEffective.test(value));
    }

    @Override
    public QueryCondition notLike(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value + "%"));
    }

    @Override
    public QueryCondition notLike(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value + "%").when(isEffective));
    }

    @Override
    public QueryCondition notLike(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value + "%").when(isEffective));
    }

    @Override
    public <T> QueryCondition notLike(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value + "%").when(isEffective.test(value)));
    }

    @Override
    public QueryCondition notLikeLeft(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, value + "%"));
    }

    @Override
    public QueryCondition notLikeLeft(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, value + "%").when(isEffective));
    }

    @Override
    public QueryCondition notLikeLeft(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, value + "%").when(isEffective));
    }

    @Override
    public <T> QueryCondition notLikeLeft(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, value + "%").when(isEffective.test(value)));
    }

    @Override
    public QueryCondition notLikeRight(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value));
    }

    @Override
    public QueryCondition notLikeRight(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value).when(isEffective));
    }

    @Override
    public QueryCondition notLikeRight(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value).when(isEffective));
    }

    @Override
    public <T> QueryCondition notLikeRight(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value).when(isEffective.test(value)));
    }

    /**
     * {@code NOT LIKE value}
     */
    public QueryCondition notLikeRaw(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeRaw(value, true);
    }

    /**
     * {@code NOT LIKE value}
     */
    public QueryCondition notLikeRaw(Object value, boolean isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, value).when(isEffective));
    }

    /**
     * {@code NOT LIKE value}
     */
    public QueryCondition notLikeRaw(Object value, BooleanSupplier isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeRaw(value, isEffective.getAsBoolean());
    }

    /**
     * {@code NOT LIKE value}
     */
    public <T> QueryCondition notLikeRaw(T value, Predicate<T> isEffective) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeRaw(value, isEffective.test(value));
    }

    @Override
    public QueryCondition isNull(boolean isEffective) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.IS_NULL, null).when(isEffective));
    }

    @Override
    public QueryCondition isNotNull(boolean isEffective) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.IS_NOT_NULL, null).when(isEffective));
    }


    ////order by ////
    public QueryOrderBy asc() {
        return new QueryOrderBy(this, SqlConsts.ASC);
    }


    public QueryOrderBy desc() {
        return new QueryOrderBy(this, SqlConsts.DESC);
    }


    // 运算 加减乘除 + - * /
    public QueryColumn add(QueryColumn queryColumn) {
        return new ArithmeticQueryColumn(this).add(queryColumn);
    }

    public QueryColumn add(Number number) {
        return new ArithmeticQueryColumn(this).add(number);
    }

    public QueryColumn subtract(QueryColumn queryColumn) {
        return new ArithmeticQueryColumn(this).subtract(queryColumn);
    }

    public QueryColumn subtract(Number number) {
        return new ArithmeticQueryColumn(this).subtract(number);
    }

    public QueryColumn multiply(QueryColumn queryColumn) {
        return new ArithmeticQueryColumn(this).multiply(queryColumn);
    }

    public QueryColumn multiply(Number number) {
        return new ArithmeticQueryColumn(this).multiply(number);
    }

    public QueryColumn divide(QueryColumn queryColumn) {
        return new ArithmeticQueryColumn(this).divide(queryColumn);
    }

    public QueryColumn divide(Number number) {
        return new ArithmeticQueryColumn(this).divide(number);
    }


    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        QueryTable selectTable = getSelectTable(queryTables, table);
        if (selectTable == null) {
            return dialect.wrap(name);
        } else {
            if (StringUtil.isNotBlank(selectTable.alias)) {
                return dialect.wrap(selectTable.alias) + SqlConsts.REFERENCE + dialect.wrap(name);
            } else if (StringUtil.isNotBlank(selectTable.getSchema()) && StringUtil.isNotBlank(selectTable.getName())) {
                String realTable = dialect.getRealTable(selectTable.getName(), OperateType.SELECT);
                return dialect.wrap(dialect.getRealSchema(selectTable.schema, realTable, OperateType.SELECT)) + SqlConsts.REFERENCE + dialect.wrap(realTable)
                    + SqlConsts.REFERENCE + dialect.wrap(name);
            } else if (StringUtil.isNotBlank(selectTable.getName())) {
                return dialect.wrap(dialect.getRealTable(selectTable.getName(), OperateType.SELECT)) + SqlConsts.REFERENCE + dialect.wrap(name);
            } else {
                return dialect.wrap(name);
            }
        }
    }


    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return toConditionSql(queryTables, dialect) + WrapperUtil.buildColumnAlias(alias, dialect);
    }


    QueryTable getSelectTable(List<QueryTable> queryTables, QueryTable selfTable) {
        // 未查询任何表
        if (queryTables == null || queryTables.isEmpty()) {
            return null;
        }

        if (selfTable != null && StringUtil.isNotBlank(selfTable.alias)) {
            return selfTable;
        }

        if (queryTables.size() == 1 && queryTables.get(0).isSameTable(selfTable)) {
            // ignore table
            return null;
        }

        if (CollectionUtil.isEmpty(queryTables)) {
            return selfTable;
        }

        if (selfTable == null && queryTables.size() == 1) {
            return queryTables.get(0);
        }

        for (QueryTable table : queryTables) {
            if (table.isSameTable(selfTable)) {
                return table;
            }
        }
        return selfTable;
    }


    @Override
    public String toString() {
        return "QueryColumn{" +
            "table=" + table +
            ", name='" + name + '\'' +
            ", alias='" + alias + '\'' +
            '}';
    }


    @Override
    public QueryColumn clone() {
        try {
            QueryColumn clone = (QueryColumn) super.clone();
            // deep clone ...
            clone.table = ObjectUtil.clone(this.table);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }

}
