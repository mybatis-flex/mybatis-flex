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
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.Conditional;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * Lambda 条件构建器。
 *
 * @author 王帅
 * @since 2023-07-24
 */
public class WhereBuilder<R extends QueryModel<R>> implements Conditional<R> {

    private final R queryModel;
    private final QueryColumn queryColumn;
    private final SqlConnector connector;

    public WhereBuilder(R queryModel, QueryColumn queryColumn, SqlConnector connector) {
        this.queryModel = queryModel;
        this.queryColumn = queryColumn;
        this.connector = connector;
    }

    private void addWhereQueryCondition(QueryCondition queryCondition) {
        CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryCondition, connector);
    }

    @Override
    public R eq(Object value) {
        addWhereQueryCondition(queryColumn.eq(value));
        return queryModel;
    }

    @Override
    public R eq(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.eq(value, isEffective));
        return queryModel;
    }

    @Override
    public R eq(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.eq(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R eq(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.eq(value, isEffective));
        return queryModel;
    }

    /**
     * 等于 {@code =}
     */
    public <T> R eq(LambdaGetter<T> value) {
        return eq(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 等于 {@code =}
     */
    public <T> R eq(LambdaGetter<T> value, boolean isEffective) {
        return eq(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 等于 {@code =}
     */
    public <T> R eq(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return eq(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public R ne(Object value) {
        addWhereQueryCondition(queryColumn.ne(value));
        return queryModel;
    }

    @Override
    public R ne(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.ne(value, isEffective));
        return queryModel;
    }

    @Override
    public R ne(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.ne(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R ne(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.ne(value, isEffective));
        return queryModel;
    }

    /**
     * 不等于 {@code !=}
     */
    public <T> R ne(LambdaGetter<T> value) {
        return ne(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 不等于 {@code !=}
     */
    public <T> R ne(LambdaGetter<T> value, boolean isEffective) {
        return ne(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 不等于 {@code !=}
     */
    public <T> R ne(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return ne(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public R gt(Object value) {
        addWhereQueryCondition(queryColumn.gt(value));
        return queryModel;
    }

    @Override
    public R gt(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.gt(value, isEffective));
        return queryModel;
    }

    @Override
    public R gt(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.gt(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R gt(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.gt(value, isEffective));
        return queryModel;
    }

    /**
     * 大于 {@code >}
     */
    public <T> R gt(LambdaGetter<T> value) {
        return gt(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 大于 {@code >}
     */
    public <T> R gt(LambdaGetter<T> value, boolean isEffective) {
        return gt(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 大于 {@code >}
     */
    public <T> R gt(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return gt(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public R ge(Object value) {
        addWhereQueryCondition(queryColumn.ge(value));
        return queryModel;
    }

    @Override
    public R ge(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.ge(value, isEffective));
        return queryModel;
    }

    @Override
    public R ge(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.ge(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R ge(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.ge(value, isEffective));
        return queryModel;
    }

    /**
     * 大于等于 {@code >=}
     */
    public <T> R ge(LambdaGetter<T> value) {
        return ge(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 大于等于 {@code >=}
     */
    public <T> R ge(LambdaGetter<T> value, boolean isEffective) {
        return ge(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 大于等于 {@code >=}
     */
    public <T> R ge(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return ge(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public R lt(Object value) {
        addWhereQueryCondition(queryColumn.lt(value));
        return queryModel;
    }

    @Override
    public R lt(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.lt(value, isEffective));
        return queryModel;
    }

    @Override
    public R lt(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.lt(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R lt(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.lt(value, isEffective));
        return queryModel;
    }

    /**
     * 小于 {@code <}
     */
    public <T> R lt(LambdaGetter<T> value) {
        return lt(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 小于 {@code <}
     */
    public <T> R lt(LambdaGetter<T> value, boolean isEffective) {
        return lt(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 小于 {@code <}
     */
    public <T> R lt(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return lt(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public R le(Object value) {
        addWhereQueryCondition(queryColumn.le(value));
        return queryModel;
    }

    @Override
    public R le(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.le(value, isEffective));
        return queryModel;
    }

    @Override
    public R le(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.le(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R le(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.le(value, isEffective));
        return queryModel;
    }

    /**
     * 小于等于 {@code <=}
     */
    public <T> R le(LambdaGetter<T> value) {
        return le(LambdaUtil.getQueryColumn(value), true);
    }

    /**
     * 小于等于 {@code <=}
     */
    public <T> R le(LambdaGetter<T> value, boolean isEffective) {
        return le(LambdaUtil.getQueryColumn(value), isEffective);
    }

    /**
     * 小于等于 {@code <=}
     */
    public <T> R le(LambdaGetter<T> value, BooleanSupplier isEffective) {
        return le(LambdaUtil.getQueryColumn(value), isEffective.getAsBoolean());
    }

    @Override
    public R in(Object... value) {
        addWhereQueryCondition(queryColumn.in(value));
        return queryModel;
    }

    @Override
    public R in(Object[] value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryModel;
    }

    @Override
    public R in(Object[] value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R in(T[] value, Predicate<T[]> isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryModel;
    }

    @Override
    public R in(Collection<?> value) {
        addWhereQueryCondition(queryColumn.in(value));
        return queryModel;
    }

    @Override
    public R in(Collection<?> value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryModel;
    }

    @Override
    public R in(Collection<?> value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryModel;
    }

    @Override
    public <T extends Collection<?>> R in(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.in(value, isEffective));
        return queryModel;
    }

    @Override
    public R in(QueryWrapper queryWrapper) {
        addWhereQueryCondition(queryColumn.in(queryWrapper));
        return queryModel;
    }

    @Override
    public R in(QueryWrapper queryWrapper, boolean isEffective) {
        addWhereQueryCondition(queryColumn.in(queryWrapper, isEffective));
        return queryModel;
    }

    @Override
    public R in(QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.in(queryWrapper, isEffective));
        return queryModel;
    }

    /**
     * {@code IN(value)}
     */
    public R in(QueryModel<R> queryModel) {
        return in(queryModel, true);
    }

    /**
     * {@code IN(value)}
     */
    public R in(QueryModel<R> queryModel, boolean isEffective) {
        if (queryModel != null) {
            addWhereQueryCondition(queryColumn.in(queryModel.queryWrapper(), isEffective));
        }
        return this.queryModel;
    }

    /**
     * {@code IN(value)}
     */
    public R in(QueryModel<R> queryModel, BooleanSupplier isEffective) {
        return in(queryModel, isEffective.getAsBoolean());
    }

    @Override
    public R notIn(Object... value) {
        addWhereQueryCondition(queryColumn.notIn(value));
        return queryModel;
    }

    @Override
    public R notIn(Object[] value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryModel;
    }

    @Override
    public R notIn(Object[] value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R notIn(T[] value, Predicate<T[]> isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryModel;
    }

    @Override
    public R notIn(Collection<?> value) {
        addWhereQueryCondition(queryColumn.notIn(value));
        return queryModel;
    }

    @Override
    public R notIn(Collection<?> value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryModel;
    }

    @Override
    public R notIn(Collection<?> value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryModel;
    }

    @Override
    public <T extends Collection<?>> R notIn(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.notIn(value, isEffective));
        return queryModel;
    }

    @Override
    public R notIn(QueryWrapper queryWrapper) {
        addWhereQueryCondition(queryColumn.notIn(queryWrapper));
        return queryModel;
    }

    @Override
    public R notIn(QueryWrapper queryWrapper, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notIn(queryWrapper, isEffective));
        return queryModel;
    }

    @Override
    public R notIn(QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notIn(queryWrapper, isEffective));
        return queryModel;
    }

    @Override
    public R between(Object[] values) {
        addWhereQueryCondition(queryColumn.between(values));
        return queryModel;
    }

    @Override
    public R between(Object[] values, boolean isEffective) {
        addWhereQueryCondition(queryColumn.between(values, isEffective));
        return queryModel;
    }

    /**
     * {@code NOT IN(value)}
     */
    public R notIn(QueryModel<R> queryModel) {
        return notIn(queryModel, true);
    }

    /**
     * {@code NOT IN(value)}
     */
    public R notIn(QueryModel<R> queryModel, boolean isEffective) {
        if (queryModel != null) {
            addWhereQueryCondition(queryColumn.notIn(queryModel.queryWrapper(), isEffective));
        }
        return this.queryModel;
    }

    /**
     * {@code NOT IN(value)}
     */
    public R notIn(QueryModel<R> queryModel, BooleanSupplier isEffective) {
        return notIn(queryModel, isEffective.getAsBoolean());
    }

    @Override
    public R between(Object start, Object end) {
        addWhereQueryCondition(queryColumn.between(start, end));
        return queryModel;
    }

    @Override
    public R between(Object start, Object end, boolean isEffective) {
        addWhereQueryCondition(queryColumn.between(start, end, isEffective));
        return queryModel;
    }

    @Override
    public R between(Object start, Object end, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.between(start, end, isEffective));
        return queryModel;
    }

    @Override
    public <S, E> R between(S start, E end, BiPredicate<S, E> isEffective) {
        addWhereQueryCondition(queryColumn.between(start, end, isEffective));
        return queryModel;
    }

    @Override
    public R notBetween(Object[] values) {
        addWhereQueryCondition(queryColumn.notBetween(values));
        return queryModel;
    }

    @Override
    public R notBetween(Object[] values, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notBetween(values, isEffective));
        return queryModel;
    }

    @Override
    public R notBetween(Object start, Object end) {
        addWhereQueryCondition(queryColumn.notBetween(start, end));
        return queryModel;
    }

    @Override
    public R notBetween(Object start, Object end, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notBetween(start, end, isEffective));
        return queryModel;
    }

    @Override
    public R notBetween(Object start, Object end, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notBetween(start, end, isEffective));
        return queryModel;
    }

    @Override
    public <S, E> R notBetween(S start, E end, BiPredicate<S, E> isEffective) {
        addWhereQueryCondition(queryColumn.notBetween(start, end, isEffective));
        return queryModel;
    }

    @Override
    public R like(Object value) {
        addWhereQueryCondition(queryColumn.like(value));
        return queryModel;
    }

    @Override
    public R like(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.like(value, isEffective));
        return queryModel;
    }

    @Override
    public R like(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.like(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R like(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.like(value, isEffective));
        return queryModel;
    }

    @Override
    public R likeLeft(Object value) {
        addWhereQueryCondition(queryColumn.likeLeft(value));
        return queryModel;
    }

    @Override
    public R likeLeft(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.likeLeft(value, isEffective));
        return queryModel;
    }

    @Override
    public R likeLeft(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.likeLeft(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R likeLeft(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.likeLeft(value, isEffective));
        return queryModel;
    }

    @Override
    public R likeRight(Object value) {
        addWhereQueryCondition(queryColumn.likeRight(value));
        return queryModel;
    }

    @Override
    public R likeRight(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.likeRight(value, isEffective));
        return queryModel;
    }

    @Override
    public R likeRight(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.likeRight(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R likeRight(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.likeRight(value, isEffective));
        return queryModel;
    }

    @Override
    public R notLike(Object value) {
        addWhereQueryCondition(queryColumn.notLike(value));
        return queryModel;
    }

    @Override
    public R notLike(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notLike(value, isEffective));
        return queryModel;
    }

    @Override
    public R notLike(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notLike(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R notLike(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.notLike(value, isEffective));
        return queryModel;
    }

    @Override
    public R notLikeLeft(Object value) {
        addWhereQueryCondition(queryColumn.notLikeLeft(value));
        return queryModel;
    }

    @Override
    public R notLikeLeft(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notLikeLeft(value, isEffective));
        return queryModel;
    }

    @Override
    public R notLikeLeft(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notLikeLeft(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R notLikeLeft(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.notLikeLeft(value, isEffective));
        return queryModel;
    }

    @Override
    public R notLikeRight(Object value) {
        addWhereQueryCondition(queryColumn.notLikeRight(value));
        return queryModel;
    }

    @Override
    public R notLikeRight(Object value, boolean isEffective) {
        addWhereQueryCondition(queryColumn.notLikeRight(value, isEffective));
        return queryModel;
    }

    @Override
    public R notLikeRight(Object value, BooleanSupplier isEffective) {
        addWhereQueryCondition(queryColumn.notLikeRight(value, isEffective));
        return queryModel;
    }

    @Override
    public <T> R notLikeRight(T value, Predicate<T> isEffective) {
        addWhereQueryCondition(queryColumn.notLikeRight(value, isEffective));
        return queryModel;
    }

    @Override
    public R isNull(boolean isEffective) {
        addWhereQueryCondition(queryColumn.isNull(isEffective));
        return queryModel;
    }

    @Override
    public R isNotNull(boolean isEffective) {
        addWhereQueryCondition(queryColumn.isNotNull(isEffective));
        return queryModel;
    }

}
