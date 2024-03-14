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

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Iterator;

/**
 * <p>判断是否有 MyBatis-Flex 的多数据源配置。
 * <p>如果配置文件中有 MyBatis-Flex 的多数据源配置，就加载 MyBatis-Flex 多数据源自动配置类。
 *
 * @author michael
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ConditionalOnMybatisFlexDatasource.OnMybatisFlexDataSourceCondition.class)
public @interface ConditionalOnMybatisFlexDatasource {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    class OnMybatisFlexDataSourceCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment env = context.getEnvironment();
            if (env instanceof AbstractEnvironment) {
                MutablePropertySources propertySources = ((AbstractEnvironment) env).getPropertySources();
                Iterator<PropertySource<?>> it = propertySources.stream().iterator();
                while (it.hasNext()) {
                    PropertySource<?> ps = it.next();
                    if (ps instanceof EnumerablePropertySource) {
                        for (String propertyName : ((EnumerablePropertySource<?>) ps).getPropertyNames()) {
                            if (propertyName.startsWith("mybatis-flex.datasource.")) {
                                return ConditionOutcome.match();
                            }
                        }
                    }
                }
            }
            return ConditionOutcome.noMatch("'mybatis-flex.datasource' is necessary.");
        }

    }

}
