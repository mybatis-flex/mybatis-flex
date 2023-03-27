/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 默认的审计消息收集器，其收集消息后，定时通过消息发送器{@link AuditMessageSender}把消息发送过去
 */
public class DefaultAuditMessageCollector implements AuditMessageCollector, Runnable {

    private long period = 10;
    private ScheduledExecutorService scheduler;
    private AuditMessageSender messageSender = new ConsoleAuditMessageSender();

    private List<AuditMessage> messages = Collections.synchronizedList(new ArrayList<>());

    public DefaultAuditMessageCollector() {
        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "DefaultAuditMessageCollector");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(this, period, period, TimeUnit.SECONDS);
    }

    public DefaultAuditMessageCollector(long period, AuditMessageSender messageSender) {
        this.period = period;
        this.messageSender = messageSender;
    }

    @Override
    public void collect(AuditMessage message) {
        messages.add(message);
    }


    @Override
    public void run() {
        if (messages.isEmpty()) {
            return;
        }
        List<AuditMessage> sendMessages = new ArrayList(messages);
        messages.clear();
        messageSender.sendMessages(sendMessages);
    }
}
