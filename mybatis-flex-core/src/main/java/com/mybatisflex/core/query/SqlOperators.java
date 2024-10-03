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

import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.HashMap;

/**
 * <p>SQL 操作符集合，用于为多个字段分别设置操作符。
 *
 * <p>该类继承自 {@link HashMap}，其中键是<strong>数据库字段的名称</strong>，
 * 值是对应的 {@link SqlOperator} 枚举实例。
 *
 * @author michael
 * @author 王帅
 * @see SqlOperator
 */
public class SqlOperators extends HashMap<String, SqlOperator> {

    /**
     * 一个空的实例，用于表示没有操作符的情况。
     */
    private static final SqlOperators EMPTY = new SqlOperators() {
        @Override
        public SqlOperator put(String key, SqlOperator value) {
            throw new IllegalArgumentException("不能为 \"empty\" SqlOperators 设置 SqlOperator");
        }
    };

    /**
     * 默认构造函数。
     * 创建一个空的 {@link SqlOperators} 实例。
     */
    public SqlOperators() {
    }

    /**
     * <p>带初始容量的构造函数。
     *
     * <p>创建一个具有指定初始容量的 {@link SqlOperators} 实例。
     *
     * @param initialCapacity 初始容量
     */
    public SqlOperators(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * <p>复制构造函数。
     *
     * <p>创建一个包含指定 {@link SqlOperators} 实例所有元素的新的 {@link SqlOperators} 实例。
     *
     * @param sqlOperators 要复制的 {@link SqlOperators} 实例
     */
    public SqlOperators(SqlOperators sqlOperators) {
        this.putAll(sqlOperators);
    }

    /**
     * <p>获取一个空的 {@link SqlOperators} 实例。
     *
     * <p><strong>注意：空实例不允许向其中添加任何操作符。</strong>
     *
     * @return 一个空的、不可操作的 {@link SqlOperators} 实例
     */
    public static SqlOperators empty() {
        return EMPTY;
    }

    /**
     * 创建一个新的 {@link SqlOperators} 实例。
     *
     * @return 一个新的、可操作的 {@link SqlOperators} 实例
     */
    public static SqlOperators of() {
        return new SqlOperators();
    }

    /**
     * 使用给定数据库的字段名称和操作符创建一个新的 {@link SqlOperators} 实例。
     *
     * @param columnName 数据库的字段名称
     * @param operator   对应的字段操作符
     * @return 包含指定字段和操作符的 {@link SqlOperators} 实例
     */
    public static SqlOperators of(String columnName, SqlOperator operator) {
        return new SqlOperators(1).set(columnName, operator);
    }

    /**
     * 使用给定的查询列（{@link QueryColumn}）和操作符创建一个新的 {@link SqlOperators} 实例。
     *
     * @param column   查询列
     * @param operator 对应的字段操作符
     * @return 包含指定字段和操作符的 {@link SqlOperators} 实例
     */
    public static SqlOperators of(QueryColumn column, SqlOperator operator) {
        return new SqlOperators(1).set(column, operator);
    }

    /**
     * 使用给定的 Lambda 表达式和操作符创建一个新的 {@link SqlOperators} 实例。
     *
     * @param getter   Lambda 表达式
     * @param operator 对应的字段操作符
     * @param <T>      实体类的类型。
     * @return 包含指定字段和操作符的 {@link SqlOperators} 实例
     */
    public static <T> SqlOperators of(LambdaGetter<T> getter, SqlOperator operator) {
        return new SqlOperators(1).set(getter, operator);
    }

    /**
     * 设置数据库的字段名称以及对应的操作符。
     *
     * @param columnName 字段名称
     * @param operator   字段操作符
     * @return 当前 {@link SqlOperators} 实例，以便进行链式调用
     */
    public SqlOperators set(String columnName, SqlOperator operator) {
        this.put(columnName, operator);
        return this;
    }

    /**
     * 设置查询列（{@link QueryColumn}）对应数据库字段的操作符。
     *
     * @param column   查询列
     * @param operator 操作符
     * @return 当前 {@link SqlOperators} 实例，以便进行链式调用
     */
    public SqlOperators set(QueryColumn column, SqlOperator operator) {
        return set(column.getName(), operator);
    }

    /**
     * 设置 Lambda 表达式对应数据库字段的操作符。
     *
     * @param getter   Lambda 表达式
     * @param operator 对应的操作符
     * @param <T>      实体类的类型
     * @return 当前 {@link SqlOperators} 实例，以便进行链式调用
     */
    public <T> SqlOperators set(LambdaGetter<T> getter, SqlOperator operator) {
        return set(LambdaUtil.getQueryColumn(getter), operator);
    }

}
