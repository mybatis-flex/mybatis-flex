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
package com.mybatisflex.loveqq.framework.boot.autoconfig;

import com.kfyty.loveqq.framework.core.autoconfig.InitializingBean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnProperty;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.MessageFactory;
import com.mybatisflex.core.audit.MessageReporter;
import com.mybatisflex.core.audit.http.HttpMessageReporter;

/**
 * MyBatis-Flex-Admin 自动配置。
 *
 * @author 王帅
 * @author kfyty725
 * @since 2023-07-01
 */
@Component
@ConditionalOnProperty(prefix = "mybatis-flex.adminConfig", value = "enable", havingValue = "true")
public class MybatisFlexAdminAutoConfiguration implements InitializingBean {
    @Autowired(required = false)
    private MessageFactory messageFactory;

    @Autowired(required = false)
    private MybatisFlexProperties properties;

    @Autowired(required = false)
    private HttpMessageReporter.JSONFormatter jsonFormatter;

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
