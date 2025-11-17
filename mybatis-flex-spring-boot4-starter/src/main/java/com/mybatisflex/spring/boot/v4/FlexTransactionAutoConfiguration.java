/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.spring.boot.v4;

import com.mybatisflex.core.row.Db;
import com.mybatisflex.spring.FlexTransactionManager;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * MyBatis-Flex 事务自动配置。
 *
 * @author michael
 */
@ConditionalOnClass(
    value = Db.class,
    name = {
        "org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration",
        "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration",
    }
)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ConditionalOnMissingBean(TransactionManager.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureAfter({MybatisFlexAutoConfiguration.class})
@AutoConfigureBefore(value = {TransactionAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
public class FlexTransactionAutoConfiguration implements TransactionManagementConfigurer {

    /**
     * 这里使用 final 修饰属性是因为：<br>
     * <p>
     * 1、调用 {@link #annotationDrivenTransactionManager} 方法会返回 TransactionManager 对象<br>
     * 2、{@code @Bean} 注入又会返回 TransactionManager 对象<br>
     * <p>
     * 需要保证两个对象的一致性。
     */
    private final FlexTransactionManager flexTransactionManager = new FlexTransactionManager();

    @NonNull
    @Override
    @Bean(name = "transactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return flexTransactionManager;
    }

}
