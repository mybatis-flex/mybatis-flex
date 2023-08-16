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

package com.mybatisflex.core.update;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.util.LambdaGetter;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * 属性设置接口。
 *
 * @param <R> 链式调用返回值类型
 * @author 王帅
 * @since 2023-08-16
 */
public interface PropertySetter<R> {

    /**
     * 设置字段对应参数值。
     *
     * @param property 字段名
     * @param value    参数值
     */
    default R set(String property, Object value) {
        return set(property, value, true);
    }

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    R set(String property, Object value, boolean isEffective);

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    default R set(String property, Object value, BooleanSupplier isEffective) {
        return set(property, value, isEffective.getAsBoolean());
    }

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    default <V> R set(String property, V value, Predicate<V> isEffective) {
        return set(property, value, isEffective.test(value));
    }

    /**
     * 设置字段对应参数值。
     *
     * @param property 字段名
     * @param value    参数值
     */
    default R set(QueryColumn property, Object value) {
        return set(property, value, true);
    }

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    R set(QueryColumn property, Object value, boolean isEffective);

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    default R set(QueryColumn property, Object value, BooleanSupplier isEffective) {
        return set(property, value, isEffective.getAsBoolean());
    }

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    default <V> R set(QueryColumn property, V value, Predicate<V> isEffective) {
        return set(property, value, isEffective.test(value));
    }

    /**
     * 设置字段对应参数值。
     *
     * @param property 字段名
     * @param value    参数值
     */
    default <T> R set(LambdaGetter<T> property, Object value) {
        return set(property, value, true);
    }

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    <T> R set(LambdaGetter<T> property, Object value, boolean isEffective);

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    default <T> R set(LambdaGetter<T> property, Object value, BooleanSupplier isEffective) {
        return set(property, value, isEffective.getAsBoolean());
    }

    /**
     * 设置字段对应参数值。
     *
     * @param property    字段名
     * @param value       参数值
     * @param isEffective 是否生效
     */
    default <T, V> R set(LambdaGetter<T> property, V value, Predicate<V> isEffective) {
        return set(property, value, isEffective.test(value));
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property 字段名
     * @param value    原生值
     */
    default R setRaw(String property, Object value) {
        return setRaw(property, value, true);
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    R setRaw(String property, Object value, boolean isEffective);

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    default R setRaw(String property, Object value, BooleanSupplier isEffective) {
        return setRaw(property, value, isEffective.getAsBoolean());
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    default <V> R setRaw(String property, V value, Predicate<V> isEffective) {
        return setRaw(property, value, isEffective.test(value));
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property 字段名
     * @param value    原生值
     */
    default R setRaw(QueryColumn property, Object value) {
        return setRaw(property, value, true);
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    R setRaw(QueryColumn property, Object value, boolean isEffective);

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    default R setRaw(QueryColumn property, Object value, BooleanSupplier isEffective) {
        return setRaw(property, value, isEffective.getAsBoolean());
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    default <V> R setRaw(QueryColumn property, V value, Predicate<V> isEffective) {
        return setRaw(property, value, isEffective.test(value));
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property 字段名
     * @param value    原生值
     */
    default <T> R setRaw(LambdaGetter<T> property, Object value) {
        return setRaw(property, value, true);
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    <T> R setRaw(LambdaGetter<T> property, Object value, boolean isEffective);

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    default <T> R setRaw(LambdaGetter<T> property, Object value, BooleanSupplier isEffective) {
        return setRaw(property, value, isEffective.getAsBoolean());
    }

    /**
     * 设置字段对应原生值。
     *
     * @param property    字段名
     * @param value       原生值
     * @param isEffective 是否生效
     */
    default <T, V> R setRaw(LambdaGetter<T> property, V value, Predicate<V> isEffective) {
        return setRaw(property, value, isEffective.test(value));
    }

}
