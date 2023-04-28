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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {

    /**
     * 显式指定表名称
     */
    String value();

    /**
     * 数据库的 schema
     */
    String schema() default "";

    /**
     * 默认为 驼峰属性 转换为 下划线字段
     */
    boolean camelToUnderline() default true;

    /**
     * 默认使用哪个数据源，若系统找不到该指定的数据源时，默认使用第一个数据源
     */
    String dataSource() default "";

    /**
     * 监听 entity 的 insert 行为
     */
    Class<? extends InsertListener>[] onInsert() default {};

    /**
     * 监听 entity 的 update 行为
     */
    Class<? extends UpdateListener>[] onUpdate() default {};

    /**
     * 监听 entity 的查询数据的 set 行为，用户主动 set 不会触发
     */
    Class<? extends SetListener>[] onSet() default {};

    /**
     * 在某些场景下，我们需要手动编写 Mapper，可以通过这个注解来关闭 APT 的 Mapper 生成
     */
    boolean mapperGenerateEnable() default true;
}