/*
 *    Copyright 2015-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.mybatisflex.spring.boot;

import org.mybatis.spring.SqlSessionFactoryBean;

/**
 * 参考：https://github.com/mybatis/spring-boot-starter/blob/master/mybatis-spring-boot-autoconfigure/src/main/java/org/mybatis/spring/boot/autoconfigure/SqlSessionFactoryBeanCustomizer.java
 *
 * 为 FlexSqlSessionFactoryBean 做自定义的配置支持。
 *
 * @see com.mybatisflex.spring.FlexSqlSessionFactoryBean
 */
@FunctionalInterface
public interface SqlSessionFactoryBeanCustomizer {

    /**
     * 自定义 {@link SqlSessionFactoryBean}。
     *
     * @param factoryBean FlexSqlSessionFactoryBean
     */
    void customize(SqlSessionFactoryBean factoryBean);

}
