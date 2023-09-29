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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 抽象消息收集器。
 *
 * @author 王帅
 * @since 2023-09-28
 */
public abstract class AbstractMessageCollector implements MessageCollector {

    private final MessageReporter messageSender;
    private final List<AuditMessage> messages = Collections.synchronizedList(new ArrayList<>());
    private final ReentrantReadWriteLock rrwLock = new ReentrantReadWriteLock();

    protected AbstractMessageCollector(MessageReporter messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void collect(AuditMessage message) {
        try {
            rrwLock.readLock().lock();
            messages.add(message);
        } finally {
            rrwLock.readLock().unlock();
        }
    }

    protected void doSendMessages() {
        if (messages.isEmpty()) {
            return;
        }
        List<AuditMessage> sendMessages;
        try {
            rrwLock.writeLock().lock();
            sendMessages = new ArrayList<>(messages);
            messages.clear();
        } finally {
            rrwLock.writeLock().unlock();
        }
        messageSender.sendMessages(sendMessages);
    }

    public void release() {
        doSendMessages();
    }

    protected List<AuditMessage> getMessages() {
        return messages;
    }

    protected MessageReporter getMessageSender() {
        return messageSender;
    }

}
