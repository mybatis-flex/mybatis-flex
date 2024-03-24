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

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.*;

/**
 * 数据库表中的列信息注解。
 *
 * @author 开源海哥
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {

    /**
     * 字段名称。
     */
    String value() default "";

    /**
     * 是否忽略该字段，可能只是业务字段，而非数据库对应字段。
     */
    boolean ignore() default false;

    /**
     * insert 的时候默认值，这个值会直接被拼接到 sql 而不通过参数设置。
     */
    String onInsertValue() default "";

    /**
     * update 的时候自动赋值，这个值会直接被拼接到 sql 而不通过参数设置。
     */
    String onUpdateValue() default "";

    /**
     * 是否是大字段，大字段 APT 不会生成到 DEFAULT_COLUMNS 里。
     */
    boolean isLarge() default false;

    /**
     * <p>是否是逻辑删除字段，一张表中只能存在 1 一个逻辑删除字段。
     *
     * <p>逻辑删除的字段，被删除时，会设置为 1，正常状态为 0，可以通过 FlexGlobalConfig 配置来修改 1 和 0 为其他值。
     */
    boolean isLogicDelete() default false;

    /**
     * <p>是否为乐观锁字段。
     *
     * <p>若是乐观锁字段的话，数据更新的时候会去检测当前版本号，若更新成功的话会设置当前版本号 +1
     * 只能用于数值的字段。
     */
    boolean version() default false;

    /**
     * 是否是租户 ID。
     */
    boolean tenantId() default false;

    /**
     * 配置的 jdbcType。
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * 自定义 TypeHandler。
     */
    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

    /**
     * 数据字段注释，在 AI 时代，注释的内容往往可用于 AI 辅助对话
     */
    String comment() default "";

}
