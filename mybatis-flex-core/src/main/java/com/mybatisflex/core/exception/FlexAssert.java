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

package com.mybatisflex.core.exception;

import java.util.Map;

/**
 * 断言。
 *
 * @author 王帅
 * @since 2023-07-08
 */
public final class FlexAssert {

    private FlexAssert() {
    }

    /**
     * 断言对象不为空，如果为空抛出异常，并指明哪个对象为空。
     *
     * @param obj 对象
     * @param msg 错误消息
     * @throws MybatisFlexException 如果对象为空，抛出此异常。
     */
    public static void notNull(Object obj, String msg) {
        if (obj == null) {
            throw FlexExceptions.wrap(msg);
        }
    }

    /**
     * 断言 Map 集合不为 {@code null} 或者空集合，如果为空则抛出异常，并指明为什么不允许为空集合。
     *
     * @param map Map 集合
     * @param msg 错误消息
     * @throws MybatisFlexException 如果集合为空，抛出此异常。
     */
    public static void notEmpty(Map<?, ?> map, String msg) {
        if (map == null || map.isEmpty()) {
            throw FlexExceptions.wrap(msg);
        }
    }

}