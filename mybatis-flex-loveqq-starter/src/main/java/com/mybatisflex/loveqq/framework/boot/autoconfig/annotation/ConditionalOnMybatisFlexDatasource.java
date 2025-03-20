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
package com.mybatisflex.loveqq.framework.boot.autoconfig.annotation;

import com.kfyty.loveqq.framework.core.autoconfig.condition.Condition;
import com.kfyty.loveqq.framework.core.autoconfig.condition.ConditionContext;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.Conditional;
import com.kfyty.loveqq.framework.core.autoconfig.env.PropertyContext;
import com.kfyty.loveqq.framework.core.support.AnnotationMetadata;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * <p>判断是否有 MyBatis-Flex 的多数据源配置。
 * <p>如果配置文件中有 MyBatis-Flex 的多数据源配置，就加载 MyBatis-Flex 多数据源自动配置类。
 *
 * @author michael
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(ConditionalOnMybatisFlexDatasource.OnMybatisFlexDataSourceCondition.class)
public @interface ConditionalOnMybatisFlexDatasource {

    class OnMybatisFlexDataSourceCondition implements Condition {

        @Override
        public boolean isMatch(ConditionContext context, AnnotationMetadata<?> metadata) {
            PropertyContext propertyContext = context.getBeanFactory().getBean(PropertyContext.class);
            Map<String, String> properties = propertyContext.searchMapProperties("mybatis-flex.datasource");
            return properties != null && !properties.isEmpty();
        }
    }
}
