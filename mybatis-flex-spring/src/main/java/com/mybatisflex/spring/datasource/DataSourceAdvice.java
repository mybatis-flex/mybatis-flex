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

package com.mybatisflex.spring.datasource;

import com.mybatisflex.annotation.UseDataSource;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;

/**
 * 多数据源切面。
 *
 * @author 王帅
 * @since 2023-06-25
 */
public class DataSourceAdvice extends AbstractPointcutAdvisor {

    private final Advice advice;
    private final Pointcut pointcut;

    public DataSourceAdvice() {
        this.advice = new DataSourceInterceptor();

        AnnotationMatchingPointcut cpc = new AnnotationMatchingPointcut(UseDataSource.class, true);
        AnnotationMethodMatcher mpc = new AnnotationMethodMatcher(UseDataSource.class);
        this.pointcut =  new ComposablePointcut(mpc).union(cpc);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }


    @Override
    public Advice getAdvice() {
        return this.advice;
    }

}
