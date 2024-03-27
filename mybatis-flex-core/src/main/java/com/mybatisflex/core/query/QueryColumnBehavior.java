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

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 默认 {@link QueryColumn} 行为。
 *
 * @author michael
 * @author 王帅
 * @author CloudPlayer
 */
public class QueryColumnBehavior {

    private QueryColumnBehavior() {
    }

    /**
     * 内置的可选的忽略规则
     */
    public static final Predicate<Object> IGNORE_NULL = Objects::isNull;
    public static final Predicate<Object> IGNORE_NONE = o -> Boolean.FALSE;
    public static final Predicate<Object> IGNORE_EMPTY = o -> o == null || "".equals(o);
    public static final Predicate<Object> IGNORE_BLANK = o -> o == null || o.toString().trim().isEmpty();

    /**
     * 在满足输入的数组或可迭代对象中的容量为 1 （即只有一个元素）时，自动将条件中的 in 转换为 =
     */
    public static final Function<? super QueryCondition, ? extends QueryCondition> CONVERT_IN_TO_EQUALS = it -> {
        Object value = it.value;
        if (it.logic.equalsIgnoreCase(SqlConsts.IN) || it.logic.equalsIgnoreCase(SqlConsts.NOT_IN)) {
            Object firstValue;
            if (value instanceof Iterable<?>) {
                Iterator<?> iter = ((Iterable<?>) value).iterator();
                if (!iter.hasNext()) {  // 没有元素，直接返回原条件
                    return it;
                }
                firstValue = iter.next();  // 取第一个元素
                if (iter.hasNext()) {  // 如果有后续元素，则直接返回原条件
                    return it;
                }
            } else if (value instanceof Object[]) {
                Object[] array = (Object[]) value;
                if (array.length != 1) {  // 如果不是单元素的数组就直接返回
                    return it;
                }
                firstValue = array[0];  // 取第一个元素
            } else {
                return it;
            }

            SqlOperator operator = it.logic.equalsIgnoreCase(SqlConsts.IN) ? SqlOperator.EQUALS : SqlOperator.NOT_EQUALS;
            return QueryCondition.create(it.column, operator, firstValue);  // 将 in 转换为 =
        } else {
            return it;
        }
    };

    /**
     * 如果使用了 = 来比较 null ，则将其转为 is null 。
     */
    public static final Function<? super QueryCondition, ? extends QueryCondition> CONVERT_EQUALS_TO_IS_NULL = it ->
        it.value == null && it.logic.equalsIgnoreCase(SqlConsts.EQUALS) ? it.column.isNull() : it;
    /**
     * 自定义全局的自动忽略参数的方法。
     */
    private static Predicate<Object> ignoreFunction = IGNORE_NULL;

    /**
     * 自定义全局的自动转换条件的方法。
     */
    private static Function<? super QueryCondition, ? extends QueryCondition> conditionCaster = Function.identity();

    /**
     * 当 {@code IN(...)} 条件只有 1 个参数时，是否自动把的内容转换为相等。
     */
    private static boolean smartConvertInToEquals = false;

    public static Predicate<Object> getIgnoreFunction() {
        return ignoreFunction;
    }

    public static void setIgnoreFunction(Predicate<Object> ignoreFunction) {
        QueryColumnBehavior.ignoreFunction = ignoreFunction;
    }

    public static boolean isSmartConvertInToEquals() {
        return smartConvertInToEquals;
    }

    public static void setSmartConvertInToEquals(boolean smartConvertInToEquals) {
        QueryColumnBehavior.smartConvertInToEquals = smartConvertInToEquals;
    }

    static boolean shouldIgnoreValue(Object value) {
        return ignoreFunction.test(value);
    }

    public static Function<? super QueryCondition, ? extends QueryCondition> getConditionCaster() {
        return smartConvertInToEquals ? CONVERT_IN_TO_EQUALS.andThen(conditionCaster) : conditionCaster;
    }

    public static void setConditionCaster(Function<? super QueryCondition, ? extends QueryCondition> conditionCaster) {
        QueryColumnBehavior.conditionCaster = conditionCaster;
    }

    public static QueryCondition castCondition(QueryCondition condition) {
        return getConditionCaster().apply(condition);
    }

}
