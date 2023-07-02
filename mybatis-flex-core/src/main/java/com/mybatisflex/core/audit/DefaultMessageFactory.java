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
package com.mybatisflex.core.audit;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.audit.http.HttpUtil;

/**
 * 默认的审计消息创建器，用来创建带有 hostIp 的审计消息。
 */
public class DefaultMessageFactory implements MessageFactory {

    private final String hostIp = HttpUtil.getHostIp();

    @Override
    public AuditMessage create() {
        AuditMessage message = new AuditMessage();
        message.setPlatform(FlexConsts.NAME);
        message.setHostIp(hostIp);
        return message;
    }

}
