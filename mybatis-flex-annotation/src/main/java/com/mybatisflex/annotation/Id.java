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
package com.mybatisflex.annotation;

import java.lang.annotation.*;

/**
 * 数据库表中的列信息注解。
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Id {

    /**
     * ID 生成策略，默认为 {@link KeyType#None}。
     *
     * @return 生成策略
     */
    KeyType keyType() default KeyType.None;

    /**
     * <p>若 keyType 类型是 sequence， value 则代表的是
     * sequence 序列的 sql 内容。
     * 例如：select SEQ_USER_ID.nextval as id from dual
     *
     * <p>若 keyType 是 Generator，value 则代表的是使用的那个 keyGenerator 的名称。
     */
    String value() default "";

    /**
     * <p>sequence 序列执行顺序。
     *
     * <p>是在 entity 数据插入之前执行，还是之后执行，之后执行的一般是数据主动生成的 id。
     *
     * @return 执行之前还是之后
     */
    boolean before() default true;


    /**
     * 数据库字段注释，在 AI 时代，注释的内容往往可用于 AI 辅助对话
     */
    String comment() default "";
}
