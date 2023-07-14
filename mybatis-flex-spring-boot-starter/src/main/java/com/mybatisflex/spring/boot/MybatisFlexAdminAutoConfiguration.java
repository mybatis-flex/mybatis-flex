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

import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.MessageFactory;
import com.mybatisflex.core.audit.MessageReporter;
import com.mybatisflex.core.audit.http.HttpMessageReporter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex-Admin 自动配置。
 *
 * @author 王帅
 * @since 2023-07-01
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MybatisFlexAutoConfiguration.class)
@EnableConfigurationProperties(MybatisFlexProperties.class)
@ConditionalOnProperty(prefix = "mybatis-flex.admin-config", name = "enable", havingValue = "true")
public class MybatisFlexAdminAutoConfiguration implements InitializingBean {

    private final MessageFactory messageFactory;
    private final MybatisFlexProperties properties;
    private final HttpMessageReporter.JSONFormatter jsonFormatter;

    public MybatisFlexAdminAutoConfiguration(ObjectProvider<MessageFactory> messageFactory,
                                             ObjectProvider<HttpMessageReporter.JSONFormatter> jsonFormatter,
                                             MybatisFlexProperties properties) {
        this.properties = properties;
        this.jsonFormatter = jsonFormatter.getIfAvailable();
        this.messageFactory = messageFactory.getIfAvailable();
    }

    @Override
    public void afterPropertiesSet() {
        AuditManager.setAuditEnable(true);
        if (messageFactory != null) {
            AuditManager.setMessageFactory(messageFactory);
        }
        MybatisFlexProperties.AdminConfig adminConfig = properties.getAdminConfig();
        MessageReporter messageReporter = new HttpMessageReporter(
            adminConfig.getEndpoint(),
            adminConfig.getSecretKey(),
            jsonFormatter
        );
        AuditManager.setMessageReporter(messageReporter);
    }

}
