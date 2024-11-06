/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.SqlUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
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
    QueryCondition eq_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value));
    }

    @Override
    public QueryCondition eq(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return eq_(value);
    }

    @Override
    public QueryCondition eq(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return eq_(value);
    }

    @Override
    public QueryCondition eq(Object value, BooleanSupplier isEffective) {
        return eq(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition eq(T value, Predicate<T> isEffective) {
        return eq(value, isEffective.test(value));
    }


    QueryCondition ne_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value));
    }

    @Override
    public QueryCondition ne(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return ne_(value);
    }

    @Override
    public QueryCondition ne(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return ne_(value);
    }

    @Override
    public QueryCondition ne(Object value, BooleanSupplier isEffective) {
        return ne(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition ne(T value, Predicate<T> isEffective) {
        return ne(value, isEffective.test(value));
    }


    QueryCondition gt_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GT, value));
    }

    @Override
    public QueryCondition gt(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return gt_(value);
    }

    @Override
    public QueryCondition gt(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return gt_(value);
    }

    @Override
    public QueryCondition gt(Object value, BooleanSupplier isEffective) {
        return gt(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition gt(T value, Predicate<T> isEffective) {
        return gt(value, isEffective.test(value));
    }


    QueryCondition ge_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.GE, value));
    }

    @Override
    public QueryCondition ge(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return ge_(value);
    }

    @Override
    public QueryCondition ge(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return ge_(value);
    }

    @Override
    public QueryCondition ge(Object value, BooleanSupplier isEffective) {
        return ge(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition ge(T value, Predicate<T> isEffective) {
        return ge(value, isEffective.test(value));
    }


    QueryCondition lt_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LT, value));
    }

    @Override
    public QueryCondition lt(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return lt_(value);
    }

    @Override
    public QueryCondition lt(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return lt_(value);
    }

    @Override
    public QueryCondition lt(Object value, BooleanSupplier isEffective) {
        return lt(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition lt(T value, Predicate<T> isEffective) {
        return lt(value, isEffective.test(value));
    }


    QueryCondition le_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LE, value));
    }

    @Override
    public QueryCondition le(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return le_(value);
    }

    @Override
    public QueryCondition le(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return le_(value);
    }

    @Override
    public QueryCondition le(Object value, BooleanSupplier isEffective) {
        return le(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition le(T value, Predicate<T> isEffective) {
        return le(value, isEffective.test(value));
    }


    QueryCondition in_(Object... value) {
        // IN 里面只有一个值的情况
        if (value.length == 1) {
            if (QueryColumnBehavior.isSmartConvertInToEquals()) {
                return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.EQUALS, value[0]));
            }
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.IN, value));
    }


    @Override
    public QueryCondition in(Object... value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return in_(value);
    }

    @Override
    public QueryCondition in(Object[] value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return in_(value);
    }

    @Override
    public QueryCondition in(Object[] value, BooleanSupplier isEffective) {
        return in(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition in(T[] value, Predicate<T[]> isEffective) {
        return in(value, isEffective.test(value));
    }

    QueryCondition in_(Collection<?> value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.IN, value));
    }

    @Override
    public QueryCondition in(Collection<?> value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return in_(value);
    }

    @Override
    public QueryCondition in(Collection<?> value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return in_(value);
    }

    @Override
    public QueryCondition in(Collection<?> value, BooleanSupplier isEffective) {
        return in(value, isEffective.getAsBoolean());
    }

    @Override
    public <T extends Collection<?>> QueryCondition in(T value, Predicate<T> isEffective) {
        return in(value, isEffective.test(value));
    }

    QueryCondition in_(QueryWrapper queryWrapper) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.IN, queryWrapper));
    }

    @Override
    public QueryCondition in(QueryWrapper queryWrapper) {
        if (QueryColumnBehavior.shouldIgnoreValue(queryWrapper)) {
            return QueryCondition.createEmpty();
        }
        return in_(queryWrapper);
    }

    @Override
    public QueryCondition in(QueryWrapper queryWrapper, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return in_(queryWrapper);
    }

    @Override
    public QueryCondition in(QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        return in(queryWrapper, isEffective.getAsBoolean());
    }

    QueryCondition notIn_(Object... value) {
        // NOT IN 里面只有一个值的情况
        if (value.length == 1 && QueryColumnBehavior.isSmartConvertInToEquals()) {
            return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, value[0]));
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_IN, value));
    }

    @Override
    public QueryCondition notIn(Object... value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return notIn_(value);
    }

    @Override
    public QueryCondition notIn(Object[] value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notIn_(value);
    }

    @Override
    public QueryCondition notIn(Object[] value, BooleanSupplier isEffective) {
        return notIn(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition notIn(T[] value, Predicate<T[]> isEffective) {
        return notIn(value, isEffective.test(value));
    }

    QueryCondition notIn_(Collection<?> value) {
        // NOT IN 里面只有一个值的情况
        if (value.size() == 1 && QueryColumnBehavior.isSmartConvertInToEquals()) {
            Object next = value.iterator().next();
            return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_EQUALS, next));
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_IN, value));
    }

    @Override
    public QueryCondition notIn(Collection<?> value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return notIn_(value);
    }

    @Override
    public QueryCondition notIn(Collection<?> value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notIn_(value);
    }

    @Override
    public QueryCondition notIn(Collection<?> value, BooleanSupplier isEffective) {
        return notIn(value, isEffective.getAsBoolean());
    }

    @Override
    public <T extends Collection<?>> QueryCondition notIn(T value, Predicate<T> isEffective) {
        return notIn(value, isEffective.test(value));
    }

    QueryCondition notIn_(QueryWrapper queryWrapper) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_IN, queryWrapper));
    }

    @Override
    public QueryCondition notIn(QueryWrapper queryWrapper) {
        if (QueryColumnBehavior.shouldIgnoreValue(queryWrapper)) {
            return QueryCondition.createEmpty();
        }
        return notIn_(queryWrapper);
    }

    @Override
    public QueryCondition notIn(QueryWrapper queryWrapper, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notIn_(queryWrapper);
    }

    @Override
    public QueryCondition notIn(QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        return notIn(queryWrapper, isEffective.getAsBoolean());
    }

    QueryCondition between_(Object[] values) {
        if (values == null || values.length != 2) {
            throw new IllegalArgumentException("values is null or length is not 2");
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.BETWEEN, values));
    }

    QueryCondition between_(Object start, Object end) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.BETWEEN, new Object[]{start, end}));
    }

    @Override
    public QueryCondition between(Object[] values) {
        if (QueryColumnBehavior.shouldIgnoreValue(values)) {
            return QueryCondition.createEmpty();
        }
        return between_(values);
    }

    @Override
    public QueryCondition between(Object[] values, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return between_(values);
    }

    @Override
    public QueryCondition between(Object start, Object end) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return between_(start, end);
    }

    @Override
    public QueryCondition between(Object start, Object end, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return between_(start, end);
    }

    @Override
    public QueryCondition between(Object start, Object end, BooleanSupplier isEffective) {
        return between(start, end, isEffective.getAsBoolean());
    }

    @Override
    public <S, E> QueryCondition between(S start, E end, BiPredicate<S, E> isEffective) {
        return between(start, end, isEffective.test(start, end));
    }

    QueryCondition notBetween_(Object[] values) {
        if (values == null || values.length != 2) {
            throw new IllegalArgumentException("values is null or length is not 2");
        }

        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_BETWEEN, values));
    }

    QueryCondition notBetween_(Object start, Object end) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlConsts.NOT_BETWEEN, new Object[]{start, end}));
    }

    @Override
    public QueryCondition notBetween(Object[] values) {
        if (QueryColumnBehavior.shouldIgnoreValue(values)) {
            return QueryCondition.createEmpty();
        }
        return notBetween_(values);
    }

    @Override
    public QueryCondition notBetween(Object[] values, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notBetween_(values);
    }

    @Override
    public QueryCondition notBetween(Object start, Object end) {
        if (QueryColumnBehavior.shouldIgnoreValue(start) || QueryColumnBehavior.shouldIgnoreValue(end)) {
            return QueryCondition.createEmpty();
        }
        return notBetween_(start, end);
    }

    @Override
    public QueryCondition notBetween(Object start, Object end, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notBetween_(start, end);
    }

    @Override
    public QueryCondition notBetween(Object start, Object end, BooleanSupplier isEffective) {
        return notBetween(start, end, isEffective.getAsBoolean());
    }

    @Override
    public <S, E> QueryCondition notBetween(S start, E end, BiPredicate<S, E> isEffective) {
        return notBetween(start, end, isEffective.test(start, end));
    }

    QueryCondition like_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value + "%"));
    }

    @Override
    public QueryCondition like(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return like_(value);
    }

    @Override
    public QueryCondition like(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return like_(value);
    }

    @Override
    public QueryCondition like(Object value, BooleanSupplier isEffective) {
        return like(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition like(T value, Predicate<T> isEffective) {
        return like(value, isEffective.test(value));
    }

    QueryCondition likeLeft_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, value + "%"));
    }

    @Override
    public QueryCondition likeLeft(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeLeft_(value);
    }

    @Override
    public QueryCondition likeLeft(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return likeLeft_(value);
    }

    @Override
    public QueryCondition likeLeft(Object value, BooleanSupplier isEffective) {
        return likeLeft(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition likeLeft(T value, Predicate<T> isEffective) {
        return likeLeft(value, isEffective.test(value));
    }

    QueryCondition likeRight_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, "%" + value));
    }

    @Override
    public QueryCondition likeRight(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeRight_(value);
    }

    @Override
    public QueryCondition likeRight(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return likeRight_(value);
    }

    @Override
    public QueryCondition likeRight(Object value, BooleanSupplier isEffective) {
        return likeRight(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition likeRight(T value, Predicate<T> isEffective) {
        return likeRight(value, isEffective.test(value));
    }


    QueryCondition likeRaw_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.LIKE, value));
    }


    /**
     * {@code LIKE value}
     */
    public QueryCondition likeRaw(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return likeRaw_(value);
    }

    /**
     * {@code LIKE value}
     */
    public QueryCondition likeRaw(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return likeRaw_(value);
    }

    /**
     * {@code LIKE value}
     */
    public QueryCondition likeRaw(Object value, BooleanSupplier isEffective) {
        return likeRaw(value, isEffective.getAsBoolean());
    }

    /**
     * {@code LIKE value}
     */
    public <T> QueryCondition likeRaw(T value, Predicate<T> isEffective) {
        return likeRaw(value, isEffective.test(value));
    }

    QueryCondition notLike_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value + "%"));
    }

    @Override
    public QueryCondition notLike(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return notLike_(value);
    }

    @Override
    public QueryCondition notLike(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notLike_(value);
    }

    @Override
    public QueryCondition notLike(Object value, BooleanSupplier isEffective) {
        return notLike(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition notLike(T value, Predicate<T> isEffective) {
        return notLike(value, isEffective.test(value));
    }

    QueryCondition notLikeLeft_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, value + "%"));
    }

    @Override
    public QueryCondition notLikeLeft(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return notLikeLeft_(value);
    }

    @Override
    public QueryCondition notLikeLeft(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notLikeLeft_(value);
    }

    @Override
    public QueryCondition notLikeLeft(Object value, BooleanSupplier isEffective) {
        return notLikeLeft(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition notLikeLeft(T value, Predicate<T> isEffective) {
        return notLikeLeft(value, isEffective.test(value));
    }

    QueryCondition notLikeRight_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, "%" + value));
    }

    @Override
    public QueryCondition notLikeRight(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return notLikeRight_(value);
    }

    @Override
    public QueryCondition notLikeRight(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notLikeRight_(value);
    }

    @Override
    public QueryCondition notLikeRight(Object value, BooleanSupplier isEffective) {
        return notLikeRight(value, isEffective.getAsBoolean());
    }

    @Override
    public <T> QueryCondition notLikeRight(T value, Predicate<T> isEffective) {
        return notLikeRight(value, isEffective.test(value));
    }


    QueryCondition notLikeRaw_(Object value) {
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.NOT_LIKE, value));
    }

    /**
     * {@code NOT LIKE value}
     */
    public QueryCondition notLikeRaw(Object value) {
        if (QueryColumnBehavior.shouldIgnoreValue(value)) {
            return QueryCondition.createEmpty();
        }
        return notLikeRaw_(value);
    }

    /**
     * {@code NOT LIKE value}
     */
    public QueryCondition notLikeRaw(Object value, boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return notLikeRaw_(value);
    }

    /**
     * {@code NOT LIKE value}
     */
    public QueryCondition notLikeRaw(Object value, BooleanSupplier isEffective) {
        return notLikeRaw(value, isEffective.getAsBoolean());
    }

    /**
     * {@code NOT LIKE value}
     */
    public <T> QueryCondition notLikeRaw(T value, Predicate<T> isEffective) {
        return notLikeRaw(value, isEffective.test(value));
    }

    @Override
    public QueryCondition isNull(boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.IS_NULL, null));
    }

    @Override
    public QueryCondition isNotNull(boolean isEffective) {
        if (!isEffective) {
            return QueryCondition.createEmpty();
        }
        return QueryColumnBehavior.castCondition(QueryCondition.create(this, SqlOperator.IS_NOT_NULL, null));
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

    /**
     * 生成列用于构建查询条件的 SQL 语句。
     *
     * @param queryTables 查询表
     * @param dialect     方言
     * @return SQL 语句
     */
    protected String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        QueryTable selectTable = getSelectTable(queryTables, table);
        if (selectTable == null) {
            return dialect.wrap(name);
        } else {
            if (StringUtil.hasText(selectTable.alias)) {
                return dialect.wrap(selectTable.alias) + SqlConsts.REFERENCE + dialect.wrap(name);
            } else if (StringUtil.hasText(selectTable.getSchema()) && StringUtil.hasText(selectTable.getName())) {
                String realTable = dialect.getRealTable(selectTable.getName(), OperateType.SELECT);
                return dialect.wrap(dialect.getRealSchema(selectTable.schema, realTable, OperateType.SELECT)) + SqlConsts.REFERENCE + dialect.wrap(realTable)
                    + SqlConsts.REFERENCE + dialect.wrap(name);
            } else if (StringUtil.hasText(selectTable.getName())) {
                return dialect.wrap(dialect.getRealTable(selectTable.getName(), OperateType.SELECT)) + SqlConsts.REFERENCE + dialect.wrap(name);
            } else {
                return dialect.wrap(name);
            }
        }
    }

    /**
     * 生成列用于构建查询列的 SQL 语句。
     *
     * @param queryTables 查询表
     * @param dialect     方言
     * @return SQL 语句
     */
    protected String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return toConditionSql(queryTables, dialect) + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    QueryTable getSelectTable(List<QueryTable> queryTables, QueryTable selfTable) {
        // 未查询任何表，或查询表仅有一个
        // 可以省略表的引用，直接使用列名
        // SELECT 1
        // SELECT id FROM tb_user
        if (queryTables == null || queryTables.isEmpty()) {
            return null;
        }

        QueryTable consideredTable = queryTables.get(0);

        // 列未指定表名，仅以字符串的形式输入列名
        // 以查询表中的第一个表为主
        // SELECT tb_user.id FROM tb_user
        if (selfTable == null) {
            return consideredTable;
        }

        // 当前表有别名，以别名为主
        // SELECT u.id FROM tb_user u
        if (StringUtil.hasText(selfTable.alias)) {
            return selfTable;
        }

        // 当前表没有别名，查询表只有一个
        // 如果两个表是一样的则可以忽略表的引用
        // 兼容子查询时，子查询的查询表和父查询没有合并的问题
        if (queryTables.size() == 1 && Objects.equals(selfTable.name, consideredTable.name)) {
            return null;
        }

        consideredTable = selfTable;

        // 当前表存在且没有别名
        ListIterator<QueryTable> it = queryTables.listIterator(queryTables.size());

        while (it.hasPrevious()) {
            QueryTable queryTable = it.previous();
            if (Objects.equals(queryTable.name, selfTable.name)) {
                if (StringUtil.noText(queryTable.alias)) {
                    // 因为当前表没有别名，所以表名相同有没有别名，一定是这个表
                    return queryTable;
                } else {
                    // 只是表名相同，但是查询表有别名，当前表没有别名
                    // 考虑这个表，但是继续寻找，是否有未设置别名的表
                    consideredTable = queryTable;
                }
            }
        }

        return consideredTable;
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
