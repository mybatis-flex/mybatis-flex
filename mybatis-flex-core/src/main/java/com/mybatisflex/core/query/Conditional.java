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

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * 动态条件查询接口。
 *
 * @param <R> 链式调用返回值类型
 * @author 王帅
 * @since 2023-08-12
 */
@SuppressWarnings("unused")
public interface Conditional<R> {

    /**
     * 等于 {@code =}
     *
     * @param value 条件的值
     */
    R eq(Object value);

    /**
     * 等于 {@code =}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R eq(Object value, boolean isEffective);

    /**
     * 等于 {@code =}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R eq(Object value, BooleanSupplier isEffective);

    /**
     * 等于 {@code =}
     *
     * @param value       条件的值
     * @param isEffective 是否生效
     */
    <T> R eq(T value, Predicate<T> isEffective);

    /**
     * 不等于 {@code !=}
     *
     * @param value 条件的值
     */
    R ne(Object value);

    /**
     * 不等于 {@code !=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R ne(Object value, boolean isEffective);

    /**
     * 不等于 {@code !=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R ne(Object value, BooleanSupplier isEffective);

    /**
     * 不等于 {@code !=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R ne(T value, Predicate<T> isEffective);

    /**
     * 大于 {@code >}
     *
     * @param value 条件的值
     */
    R gt(Object value);

    /**
     * 大于 {@code >}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R gt(Object value, boolean isEffective);

    /**
     * 大于 {@code >}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R gt(Object value, BooleanSupplier isEffective);

    /**
     * 大于 {@code >}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R gt(T value, Predicate<T> isEffective);

    /**
     * 大于等于 {@code >=}
     *
     * @param value 条件的值
     */
    R ge(Object value);

    /**
     * 大于等于 {@code >=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R ge(Object value, boolean isEffective);

    /**
     * 大于等于 {@code >=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R ge(Object value, BooleanSupplier isEffective);

    /**
     * 大于等于 {@code >=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R ge(T value, Predicate<T> isEffective);

    /**
     * 小于 {@code <}
     *
     * @param value 条件的值
     */
    R lt(Object value);

    /**
     * 小于 {@code <}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R lt(Object value, boolean isEffective);

    /**
     * 小于 {@code <}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R lt(Object value, BooleanSupplier isEffective);

    /**
     * 小于 {@code <}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R lt(T value, Predicate<T> isEffective);

    /**
     * 小于等于 {@code <=}
     *
     * @param value 条件的值
     */
    R le(Object value);

    /**
     * 小于等于 {@code <=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R le(Object value, boolean isEffective);

    /**
     * 小于等于 {@code <=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R le(Object value, BooleanSupplier isEffective);

    /**
     * 小于等于 {@code <=}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R le(T value, Predicate<T> isEffective);

    /**
     * {@code IN(value)}
     *
     * @param value 条件的值
     */
    R in(Object... value);

    /**
     * {@code IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R in(Object[] value, boolean isEffective);

    /**
     * {@code IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R in(Object[] value, BooleanSupplier isEffective);

    /**
     * {@code IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R in(T[] value, Predicate<T[]> isEffective);

    /**
     * {@code IN(value)}
     *
     * @param value 条件的值
     */
    R in(Collection<?> value);

    /**
     * {@code IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R in(Collection<?> value, boolean isEffective);

    /**
     * {@code IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R in(Collection<?> value, BooleanSupplier isEffective);

    /**
     * {@code IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T extends Collection<?>> R in(T value, Predicate<T> isEffective);

    /**
     * {@code IN(value)}
     *
     * @param queryWrapper 子查询值
     */
    R in(QueryWrapper queryWrapper);

    /**
     * {@code IN(value)}
     *
     * @param queryWrapper 子查询值
     * @param isEffective  是否有效
     */
    R in(QueryWrapper queryWrapper, boolean isEffective);

    /**
     * {@code IN(value)}
     *
     * @param queryWrapper 子查询值
     * @param isEffective  是否有效
     */
    R in(QueryWrapper queryWrapper, BooleanSupplier isEffective);

    /**
     * {@code NOT IN(value)}
     *
     * @param value 条件的值
     */
    R notIn(Object... value);

    /**
     * {@code NOT IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notIn(Object[] value, boolean isEffective);

    /**
     * {@code NOT IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notIn(Object[] value, BooleanSupplier isEffective);

    /**
     * {@code NOT IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R notIn(T[] value, Predicate<T[]> isEffective);

    /**
     * {@code NOT IN(value)}
     *
     * @param value 条件的值
     */
    R notIn(Collection<?> value);

    /**
     * {@code NOT IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notIn(Collection<?> value, boolean isEffective);

    /**
     * {@code NOT IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notIn(Collection<?> value, BooleanSupplier isEffective);

    /**
     * {@code NOT IN(value)}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T extends Collection<?>> R notIn(T value, Predicate<T> isEffective);

    /**
     * {@code NOT IN(value)}
     *
     * @param queryWrapper 子查询值
     */
    R notIn(QueryWrapper queryWrapper);

    /**
     * {@code NOT IN(value)}
     *
     * @param queryWrapper 子查询值
     * @param isEffective  是否有效
     */
    R notIn(QueryWrapper queryWrapper, boolean isEffective);

    /**
     * {@code NOT IN(value)}
     *
     * @param queryWrapper 子查询值
     * @param isEffective  是否有效
     */
    R notIn(QueryWrapper queryWrapper, BooleanSupplier isEffective);

    /**
     * {@code BETWEEN values[0] AND values[1]}
     *
     * @param values 范围值
     */
    R between(Object[] values);

    /**
     * {@code BETWEEN values[0] AND values[1]}
     *
     * @param values 值
     * @param isEffective 是否有效
     */
    R between(Object[] values, boolean isEffective);

    /**
     * {@code BETWEEN start AND end}
     *
     * @param start 开始的值
     * @param end   结束的值
     */
    R between(Object start, Object end);

    /**
     * {@code BETWEEN start AND end}
     *
     * @param start       开始的值
     * @param end         结束的值
     * @param isEffective 是否有效
     */
    R between(Object start, Object end, boolean isEffective);

    /**
     * {@code BETWEEN start AND end}
     *
     * @param start       开始的值
     * @param end         结束的值
     * @param isEffective 是否有效
     */
    R between(Object start, Object end, BooleanSupplier isEffective);

    /**
     * {@code BETWEEN start AND end}
     *
     * @param start       开始的值
     * @param end         结束的值
     * @param isEffective 是否有效
     */
    <S, E> R between(S start, E end, BiPredicate<S, E> isEffective);

    /**
     * {@code NOT BETWEEN values[0] AND values[1]}
     *
     * @param values 范围值
     */
    R notBetween(Object[] values);

    /**
     * {@code NOT BETWEEN values[0] AND values[1]}
     *
     * @param values 值
     * @param isEffective 是否有效
     */
    R notBetween(Object[] values, boolean isEffective);

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param start 开始的值
     * @param end   结束的值
     */
    R notBetween(Object start, Object end);

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param start       开始的值
     * @param end         结束的值
     * @param isEffective 是否有效
     */
    R notBetween(Object start, Object end, boolean isEffective);

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param start       开始的值
     * @param end         结束的值
     * @param isEffective 是否有效
     */
    R notBetween(Object start, Object end, BooleanSupplier isEffective);

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param start       开始的值
     * @param end         结束的值
     * @param isEffective 是否有效
     */
    <S, E> R notBetween(S start, E end, BiPredicate<S, E> isEffective);

    /**
     * {@code LIKE %value%}
     *
     * @param value 条件的值
     */
    R like(Object value);

    /**
     * {@code LIKE %value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R like(Object value, boolean isEffective);

    /**
     * {@code LIKE %value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R like(Object value, BooleanSupplier isEffective);

    /**
     * {@code LIKE %value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R like(T value, Predicate<T> isEffective);

    /**
     * {@code LIKE value%}
     *
     * @param value 条件的值
     */
    R likeLeft(Object value);

    /**
     * {@code LIKE value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R likeLeft(Object value, boolean isEffective);

    /**
     * {@code LIKE value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R likeLeft(Object value, BooleanSupplier isEffective);

    /**
     * {@code LIKE value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R likeLeft(T value, Predicate<T> isEffective);

    /**
     * {@code LIKE %value}
     *
     * @param value 条件的值
     */
    R likeRight(Object value);

    /**
     * {@code LIKE %value}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R likeRight(Object value, boolean isEffective);

    /**
     * {@code LIKE %value}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R likeRight(Object value, BooleanSupplier isEffective);

    /**
     * {@code LIKE %value}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R likeRight(T value, Predicate<T> isEffective);

    /**
     * {@code NOT LIKE %value%}
     *
     * @param value 条件的值
     */
    R notLike(Object value);

    /**
     * {@code NOT LIKE %value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notLike(Object value, boolean isEffective);

    /**
     * {@code NOT LIKE %value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notLike(Object value, BooleanSupplier isEffective);

    /**
     * {@code NOT LIKE %value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R notLike(T value, Predicate<T> isEffective);

    /**
     * {@code NOT LIKE value%}
     *
     * @param value 条件的值
     */
    R notLikeLeft(Object value);

    /**
     * {@code NOT LIKE value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notLikeLeft(Object value, boolean isEffective);

    /**
     * {@code NOT LIKE value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notLikeLeft(Object value, BooleanSupplier isEffective);

    /**
     * {@code NOT LIKE value%}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R notLikeLeft(T value, Predicate<T> isEffective);

    /**
     * {@code NOT LIKE %value}
     *
     * @param value 条件的值
     */
    R notLikeRight(Object value);

    /**
     * {@code NOT LIKE %value}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notLikeRight(Object value, boolean isEffective);

    /**
     * {@code NOT LIKE %value}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    R notLikeRight(Object value, BooleanSupplier isEffective);

    /**
     * {@code NOT LIKE %value}
     *
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    <T> R notLikeRight(T value, Predicate<T> isEffective);

    /**
     * {@code IS NULL}
     */
    default R isNull() {
        return isNull(true);
    }

    /**
     * {@code IS NULL}
     *
     * @param isEffective 是否有效
     */
    R isNull(boolean isEffective);

    /**
     * {@code IS NULL}
     *
     * @param isEffective 是否有效
     */
    default R isNull(BooleanSupplier isEffective) {
        return isNull(isEffective.getAsBoolean());
    }

    /**
     * {@code IS NOT NULL}
     */
    default R isNotNull() {
        return isNotNull(true);
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param isEffective 是否有效
     */
    R isNotNull(boolean isEffective);

    /**
     * {@code IS NOT NULL}
     *
     * @param isEffective 是否有效
     */
    default R isNotNull(BooleanSupplier isEffective) {
        return isNotNull(isEffective.getAsBoolean());
    }

}
