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
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class QueryConditionBuilder<Wrapper extends QueryWrapper> implements Conditional<Wrapper> {

    private final Wrapper queryWrapper;
    private final QueryColumn queryColumn;
    private final SqlConnector connector;


    public QueryConditionBuilder(Wrapper queryWrapper, QueryColumn queryColumn, SqlConnector connector) {
        this.queryWrapper = queryWrapper;
        this.queryColumn = queryColumn;
        this.connector = connector;
    }

    private void addWhereQueryCondition(QueryCondition queryCondition) {
        queryWrapper.addWhereQueryCondition(queryCondition, connector);
    }

    @Override
    public Wrapper eq(Object value) {
        addWhereQueryCondition(queryColumn.eq(value));
        return queryWrapper;
    }

    @Override
    public Wrapper eq(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.eq(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper eq(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.eq(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper eq(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.eq(value, isEffective));
        return queryWrapper;
    }

    /**
     * 等于 {@code =}
     */
    public <T> Wrapper eq(LambdaGetter<T> value) {
        return eq(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 等于 {@code =}
     */
    public <T> Wrapper eq(LambdaGetter<T> value, boolean isEffective) {
        return eq(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 等于 {@code =}
     */
    public <T> Wrapper eq(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return eq(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public Wrapper ne(Object value) {
        addWhereQueryCondition(queryColumn.ne(value));
        return queryWrapper;
    }

    @Override
    public Wrapper ne(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.ne(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper ne(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.ne(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper ne(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.ne(value, isEffective));
        return queryWrapper;
    }

    /**
     * 不等于 {@code !=}
     */
    public <T> Wrapper ne(LambdaGetter<T> value) {
        return ne(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 不等于 {@code !=}
     */
    public <T> Wrapper ne(LambdaGetter<T> value, boolean isEffective) {
        return ne(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 不等于 {@code !=}
     */
    public <T> Wrapper ne(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return ne(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public Wrapper gt(Object value) {
        addWhereQueryCondition(queryColumn.gt(value));
        return queryWrapper;
    }

    @Override
    public Wrapper gt(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.gt(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper gt(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.gt(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper gt(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.gt(value, isEffective));
        return queryWrapper;
    }

    /**
     * 大于 {@code >}
     */
    public <T> Wrapper gt(LambdaGetter<T> value) {
        return gt(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 大于 {@code >}
     */
    public <T> Wrapper gt(LambdaGetter<T> value, boolean isEffective) {
        return gt(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 大于 {@code >}
     */
    public <T> Wrapper gt(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return gt(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public Wrapper ge(Object value) {
        addWhereQueryCondition(queryColumn.ge(value));
        return queryWrapper;
    }

    @Override
    public Wrapper ge(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.ge(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper ge(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.ge(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper ge(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.ge(value, isEffective));
        return queryWrapper;
    }

    /**
     * 大于等于 {@code >=}
     */
    public <T> Wrapper ge(LambdaGetter<T> value) {
        return ge(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 大于等于 {@code >=}
     */
    public <T> Wrapper ge(LambdaGetter<T> value, boolean isEffective) {
        return ge(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 大于等于 {@code >=}
     */
    public <T> Wrapper ge(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return ge(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public Wrapper lt(Object value) {
        addWhereQueryCondition(queryColumn.lt(value));
        return queryWrapper;
    }

    @Override
    public Wrapper lt(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.lt(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper lt(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.lt(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper lt(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.lt(value, isEffective));
        return queryWrapper;
    }

    /**
     * 小于 {@code <}
     */
    public <T> Wrapper lt(LambdaGetter<T> value) {
        return lt(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 小于 {@code <}
     */
    public <T> Wrapper lt(LambdaGetter<T> value, boolean isEffective) {
        return lt(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 小于 {@code <}
     */
    public <T> Wrapper lt(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return lt(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public Wrapper le(Object value) {
        addWhereQueryCondition(queryColumn.le(value));
        return queryWrapper;
    }

    @Override
    public Wrapper le(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.le(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper le(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.le(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper le(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.le(value, isEffective));
        return queryWrapper;
    }

    /**
     * 小于等于 {@code <=}
     */
    public <T> Wrapper le(LambdaGetter<T> value) {
        return le(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 小于等于 {@code <=}
     */
    public <T> Wrapper le(LambdaGetter<T> value, boolean isEffective) {
        return le(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 小于等于 {@code <=}
     */
    public <T> Wrapper le(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return le(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public Wrapper in(Object... value) {
        addWhereQueryCondition(queryColumn.in(value));
        return queryWrapper;
    }

    @Override
    public Wrapper in(Object[] value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper in(Object[] value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper in(T[] value, Predicate<T[]> isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper in(Collection<?> value) {
        addWhereQueryCondition(queryColumn.in(value));
        return queryWrapper;
    }

    @Override
    public Wrapper in(Collection<?> value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper in(Collection<?> value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T extends Collection<?>> Wrapper in(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper in(QueryWrapper queryWrapper) {
        addWhereQueryCondition(queryColumn.in(queryWrapper));
        return this.queryWrapper;
    }

    @Override
    public Wrapper in(QueryWrapper queryWrapper, boolean isEffective) {
        addWhereQueryCondition(queryColumn.in(queryWrapper, isEffective));
        return this.queryWrapper;
    }

    @Override
    public Wrapper in(QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.in(queryWrapper, isEffective));
        return this.queryWrapper;
    }

    @Override
    public Wrapper notIn(Object... value) {
        addWhereQueryCondition(queryColumn.notIn(value));
        return queryWrapper;
    }

    @Override
    public Wrapper notIn(Object[] value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notIn(Object[] value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper notIn(T[] value, Predicate<T[]> isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notIn(Collection<?> value) {
        addWhereQueryCondition(queryColumn.notIn(value));
        return queryWrapper;
    }

    @Override
    public Wrapper notIn(Collection<?> value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notIn(Collection<?> value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T extends Collection<?>> Wrapper notIn(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notIn(QueryWrapper queryWrapper) {
        addWhereQueryCondition(queryColumn.notIn(queryWrapper));
        return this.queryWrapper;
    }

    @Override
    public Wrapper notIn(QueryWrapper queryWrapper, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notIn(queryWrapper, isEffective));
        return this.queryWrapper;
    }

    @Override
    public Wrapper notIn(QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notIn(queryWrapper, isEffective));
        return this.queryWrapper;
    }

    @Override
    public Wrapper between(Object[] values) {
        addWhereQueryCondition(queryColumn.between(values));
        return queryWrapper;
    }

    @Override
    public Wrapper between(Object[] values, boolean isEffective) {
        addWhereQueryCondition(queryColumn.between(values, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper between(Object start, Object end) {
        addWhereQueryCondition(queryColumn.between(start, end));
        return queryWrapper;
    }

    @Override
    public Wrapper between(Object start, Object end, boolean isEffective) {
        addWhereQueryCondition(queryColumn.between(start, end, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper between(Object start, Object end, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.between(start, end, isEffective));
        return queryWrapper;
    }

    @Override
    public <S, E> Wrapper between(S start, E end, BiPredicate<S, E> isEffective) {
        addWhereQueryCondition(queryColumn.between(start, end, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notBetween(Object[] values) {
        addWhereQueryCondition(queryColumn.notBetween(values));
        return queryWrapper;
    }

    @Override
    public Wrapper notBetween(Object[] values, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notBetween(values, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notBetween(Object start, Object end) {
        addWhereQueryCondition(queryColumn.notBetween(start, end));
        return queryWrapper;
    }

    @Override
    public Wrapper notBetween(Object start, Object end, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notBetween(start, end, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notBetween(Object start, Object end, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notBetween(start, end, isEffective));
        return queryWrapper;
    }

    @Override
    public <S, E> Wrapper notBetween(S start, E end, BiPredicate<S, E> isEffective) {
        addWhereQueryCondition(queryColumn.notBetween(start, end, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper like(Object value) {
        addWhereQueryCondition(queryColumn.like(value));
        return queryWrapper;
    }

    @Override
    public Wrapper like(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.like(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper like(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.like(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper like(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.like(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper likeLeft(Object value) {
        addWhereQueryCondition(queryColumn.likeLeft(value));
        return queryWrapper;
    }

    @Override
    public Wrapper likeLeft(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.likeLeft(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper likeLeft(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.likeLeft(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper likeLeft(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.likeLeft(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper likeRight(Object value) {
        addWhereQueryCondition(queryColumn.likeRight(value));
        return queryWrapper;
    }

    @Override
    public Wrapper likeRight(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.likeRight(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper likeRight(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.likeRight(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper likeRight(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.likeRight(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notLike(Object value) {
        addWhereQueryCondition(queryColumn.notLike(value));
        return queryWrapper;
    }

    @Override
    public Wrapper notLike(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notLike(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notLike(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notLike(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper notLike(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.notLike(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notLikeLeft(Object value) {
        addWhereQueryCondition(queryColumn.notLikeLeft(value));
        return queryWrapper;
    }

    @Override
    public Wrapper notLikeLeft(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notLikeLeft(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notLikeLeft(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notLikeLeft(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper notLikeLeft(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.notLikeLeft(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notLikeRight(Object value) {
        addWhereQueryCondition(queryColumn.notLikeRight(value));
        return queryWrapper;
    }

    @Override
    public Wrapper notLikeRight(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notLikeRight(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper notLikeRight(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notLikeRight(value, isEffective));
        return queryWrapper;
    }

    @Override
    public <T> Wrapper notLikeRight(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.notLikeRight(value, isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper isNull(boolean isEffective) {
        addWhereQueryCondition(queryColumn.isNull(isEffective));
        return queryWrapper;
    }

    @Override
    public Wrapper isNotNull(boolean isEffective) {
        addWhereQueryCondition(queryColumn.isNotNull(isEffective));
        return queryWrapper;
    }

}
