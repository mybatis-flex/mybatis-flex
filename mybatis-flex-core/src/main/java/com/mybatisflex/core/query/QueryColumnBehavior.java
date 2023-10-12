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

import java.util.Objects;
import java.util.function.Predicate;

/**
 * 默认 {@link QueryColumn} 行为。
 *
 * @author michael
 * @author 王帅
 */
public class QueryColumnBehavior {

    private QueryColumnBehavior() {
    }

    /**
     * 内置的可选的忽略规则
     */
    public static final Predicate<Object> IGNORE_NULL = Objects::isNull;
    public static final Predicate<Object> IGNORE_EMPTY = o -> o == null || "".equals(o);
    public static final Predicate<Object> IGNORE_BLANK = o -> o == null || "".equals(o.toString().trim());

    /**
     * 自定义全局的自动忽略参数的方法。
     */
    private static Predicate<Object> ignoreFunction = IGNORE_NULL;

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

}
