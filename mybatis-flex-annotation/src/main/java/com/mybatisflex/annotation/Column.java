/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {

    /**
     * 字段名称
     */
    String value() default "";

    /**
     * update 的时候自动赋值，这个值会直接被拼接到 sql 而不通过参数设置
     */
    String onUpdateValue() default "";

    /**
     * insert 的时候默认值，这个值会直接被拼接到 sql 而不通过参数设置
     */
    String onInsertValue() default "";

    /**
     * 是否忽略该字段，可能只是业务字段，而非数据库对应字段
     */
    boolean ignore() default false;

    /**
     * 是否是大字段，大字默认不会对齐进行查询，除非指定查询
     */
    boolean isLarge() default false;

    /**
     * 是否是逻辑删除字段，一张表中只能存在 1 一个逻辑删除字段
     * 逻辑删除的字段，被删除时，会设置为 1，正常状态为 0
     */
    boolean isLogicDelete() default false;

    /**
     * 是否为乐观锁字段，如果是的话更新的时候会去检测当前版本号，更新成功的话会设置当前版本号 +1
     * 只能用于数值的字段
     */
    boolean version() default false;

}