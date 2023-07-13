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
 * 多对一映射。
 *
 * @author michael
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RelationManyToOne {

    /**
     * 当前实体类的属性。
     *
     * @return 属性名称
     */
    String selfField();

    /**
     * 目标实体类对应的表的 schema，一般情况下，关联数据不是 entity，而是 vo、dto 等需要配置此项
     *
     * @return schema 名称
     */
    String targetSchema() default "";

    /**
     * 目标实体类对应的表，一般情况下，关联数据不是 entity，而是 vo、dto 等需要配置此项
     *
     * @return 表名
     */
    String targetTable() default "";

    /**
     * 目标实体类的关联属性。
     *
     * @return 属性名称
	 */
    String targetField() default "";

    /**
     * 中间表名称，一对一的关系是通过通过中间表维护时，需要添加此项配置。
     *
     * @return 中间表名称
     */
    String joinTable() default "";

    /**
     * 中间表与当前表的关联字段，一对一的关系是通过通过中间表维护时，需要添加此项配置。
     *
     * @return 字段名称，列名
     */
    String joinSelfColumn() default "";

    /**
     * 目标表的关联字段名称，一对一的关系是通过通过中间表维护时，需要添加此项配置。
     *
     * @return 字段名称和表
     */
    String joinTargetColumn() default "";

    /**
     * 默认使用哪个数据源，若系统找不到该指定的数据源时，默认使用第一个数据源。
     *
     * @return 数据源
	 */
	String dataSource() default "";

}
