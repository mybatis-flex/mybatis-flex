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
 * @author michael
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RelationManyToMany {

	/**
	 * 当前 entity 的属性
	 *
	 * @return 属性名称
	 */
	String selfField() default "";

	/**
	 * 目标对象的关联属性
	 *
	 * @return 属性名称
	 */
	String targetField() default "";

	/**
	 * 中间表
	 *
	 * @return 中间表名称
	 */
	String joinTable();

	/**
	 * 中间表与当前表的关联字段
	 *
	 * @return 字段名称，列名
	 */
	String joinSelfColumn();


	/**
	 * 目标表的关联字段名称
	 *
	 * @return 字段名称和表
	 */
	String joinTargetColumn();

	/**
	 * 查询排序
	 *
	 * @return 排序方式
	 */
	String orderBy() default "";


	/**
	 * 默认使用哪个数据源，若系统找不到该指定的数据源时，默认使用第一个数据源。
	 */
	String dataSource() default "";

}
