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
package com.mybatisflex.loveqq.framework.boot.autoconfig.transaction;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionTimedOutException;

import java.util.Date;

/**
 * 事务定义管理器 用于更完整的实现Spring事务
 * 仅支持传统事务不支持R2DBC事务
 *
 * @author Aliothmoon
 * @author Michael
 * @since 2024/10/25
 */
public final class TimeoutHolder {
    private static final ThreadLocal<Long> TRANSACTION_DEADLINE = new ThreadLocal<>();

    public static void hold(TransactionDefinition definition) {
        if (definition == null) {
            return;
        }
        int timeout = definition.getTimeout();
        if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
            Long deadline = System.currentTimeMillis() + timeout * 1000L;
            TRANSACTION_DEADLINE.set(deadline);
        }
    }


    /**
     * 清除事务上下文
     */
    public static void clear() {
        TRANSACTION_DEADLINE.remove();
    }

    /**
     * 获取当前事务可用TTL
     *
     * @return int
     */
    public static Integer getTimeToLiveInSeconds() {
        Long deadline = TRANSACTION_DEADLINE.get();
        if (deadline == null) {
            return null;
        }
        double diff = ((double) getTimeToLiveInMillis(deadline)) / 1000;
        int secs = (int) Math.ceil(diff);
        checkTransactionTimeout(secs <= 0, deadline);
        return secs;
    }


    private static void checkTransactionTimeout(boolean deadlineReached, Long deadline) throws TransactionTimedOutException {
        if (deadlineReached) {
            throw new TransactionTimedOutException("Transaction timed out: deadline was " + new Date(deadline));
        }
    }


    private static long getTimeToLiveInMillis(Long deadline) throws TransactionTimedOutException {
        if (deadline == null) {
            throw new IllegalStateException("No timeout specified for this resource holder");
        }
        long timeToLive = deadline - System.currentTimeMillis();
        checkTransactionTimeout(timeToLive <= 0, deadline);
        return timeToLive;
    }
}
