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

/**
 * MybatisFlexException 异常封装类
 */
public final class FlexExceptions {

    private FlexExceptions() {
    }


    /**
     * 封装 MybatisFlexException 异常
     *
     * @param throwable 异常
     * @return MybatisFlexException
     */
    public static MybatisFlexException wrap(Throwable throwable) {
        if (throwable instanceof MybatisFlexException) {
            return (MybatisFlexException) throwable;
        }
        return new MybatisFlexException(throwable);
    }


    /**
     * 封装 MybatisFlexException 异常
     *
     * @param throwable 异常
     * @param msg       消息
     * @param params    消息参数
     * @return MybatisFlexException
     */
    public static MybatisFlexException wrap(Throwable throwable, String msg, Object... params) {
        return new MybatisFlexException(String.format(msg, params), throwable);
    }

    /**
     * 封装 MybatisFlexException 异常
     *
     * @param msg    消息
     * @param params 消息参数
     * @return MybatisFlexException
     */
    public static MybatisFlexException wrap(String msg, Object... params) {
        return new MybatisFlexException(String.format(msg, params));
    }


    /**
     * 断言 condition 必须为 true
     *
     * @param condition 条件
     * @param msg       消息
     * @param params    消息参数
     */
    public static void assertTrue(boolean condition, String msg, Object... params) {
        if (!condition) {
            throw wrap(msg, params);
        }
    }


    /**
     * 断言传入的内容不能为 null
     */
    public static void assertNotNull(Object object, String msg, Object params) {
        assertTrue(object != null, msg, params);
    }


    /**
     * 断言传入的数组内容不能为 null 或者 空
     */
    public static <T> void assertAreNotNull(T[] elements, String msg, Object params) {
        if (elements == null || elements.length == 0) {
            throw wrap(msg, params);
        }
        for (T element : elements) {
            if (element == null) {
                throw wrap(msg, params);
            }
        }
    }

}
