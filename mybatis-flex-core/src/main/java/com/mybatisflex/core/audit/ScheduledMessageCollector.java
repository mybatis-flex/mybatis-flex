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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 默认的审计消息收集器，其收集消息后，定时通过消息发送器{@link MessageReporter}把消息发送过去
 */
public class ScheduledMessageCollector extends AbstractMessageCollector {

    private final ScheduledExecutorService scheduler;

    public ScheduledMessageCollector() {
        this(10, new ConsoleMessageReporter());
    }


    public ScheduledMessageCollector(long period, MessageReporter messageSender) {
        super(messageSender);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "ScheduledMessageCollector");
            thread.setDaemon(true);
            return thread;
        });
        this.scheduler.scheduleAtFixedRate(this::doSendMessages, period, period, TimeUnit.SECONDS);
    }

    public void release() {
        doSendMessages(); //clear the messages
        scheduler.shutdown();
    }

}
