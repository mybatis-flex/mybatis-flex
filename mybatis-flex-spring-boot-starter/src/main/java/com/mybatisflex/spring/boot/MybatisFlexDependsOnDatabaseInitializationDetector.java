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
package com.mybatisflex.spring.boot;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector;

import java.util.Collections;
import java.util.Set;

/**
 * {@link DependsOnDatabaseInitializationDetector} for Mybatis-Flex.
 */
class MybatisFlexDependsOnDatabaseInitializationDetector
    extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {

    @Override
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return Collections.singleton(SqlSessionTemplate.class);
    }

}
