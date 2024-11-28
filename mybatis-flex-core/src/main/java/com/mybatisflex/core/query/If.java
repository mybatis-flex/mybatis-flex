/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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

import com.mybatisflex.core.util.StringUtil;

import java.util.Collection;
import java.util.Map;

public class If {

    private If() {
    }

    /**
     * 判断对象是否为空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * 判断对象是否非空
     */
    public static boolean notNull(Object object) {
        return object != null;
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    @Deprecated
    public static <T> boolean isNotEmpty(T[] array) {
        return notEmpty(array);
    }

    public static <T> boolean notEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    @Deprecated
    public static boolean isNotEmpty(Map<?, ?> map) {
        return notEmpty(map);
    }

    public static boolean notEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    @Deprecated
    public static boolean isNotEmpty(Collection<?> collection) {
        return notEmpty(collection);
    }

    public static boolean notEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean hasText(String string) {
        return StringUtil.hasText(string);
    }

    public static boolean noText(String string) {
        return StringUtil.noText(string);
    }

}
