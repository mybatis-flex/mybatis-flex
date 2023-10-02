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

/**
 * 计数消息收集器，当消息达到指定数量时发送消息。
 *
 * @author 王帅
 * @since 2023-09-28
 */
public class CountableMessageCollector extends AbstractMessageCollector {

    private final int count;

    public CountableMessageCollector() {
        this(1000, new ConsoleMessageReporter());
    }

    public CountableMessageCollector(int count, MessageReporter messageSender) {
        super(messageSender);
        this.count = count;
    }

    @Override
    public void collect(AuditMessage message) {
        super.collect(message);
        if (getMessages().size() >= count) {
            new Thread(this::doSendMessages).start();
        }
    }

}
