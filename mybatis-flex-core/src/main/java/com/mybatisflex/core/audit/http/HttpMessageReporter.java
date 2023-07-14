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
package com.mybatisflex.core.audit.http;

import com.mybatisflex.core.audit.AuditMessage;
import com.mybatisflex.core.audit.MessageReporter;

import java.util.List;

public class HttpMessageReporter implements MessageReporter {

    private final String endpoint;
    private final String secretKey;
    private final JSONFormatter jsonFormatter;

    public HttpMessageReporter(String endpoint, String secretKey, JSONFormatter jsonFormatter) {
        this.endpoint = endpoint;
        this.secretKey = secretKey;
        this.jsonFormatter = jsonFormatter;
    }

    @Override
    public void sendMessages(List<AuditMessage> messages) {
        long timeMillis = System.currentTimeMillis();
        String sign = HashUtil.md5(secretKey + timeMillis);
        String url = endpoint + "?time=" + timeMillis + "&sign=" + sign;
        HttpUtil.post(url, jsonFormatter.toJSONString(messages));
    }

    public interface JSONFormatter {

        String toJSONString(Object object);

    }

}
