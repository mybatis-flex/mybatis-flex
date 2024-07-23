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
package com.mybatisflex.core.keygen.impl;

import com.mybatisflex.core.keygen.IKeyGenerator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * ULID: 对比UUID的优势在于可排序性和性能。
 * <p>
 * 特点：
 * 1、保证 id 生成的顺序为时间顺序，越往后生成的 ID 值越大；
 * 2、可以按照生成的时间进行排序，而不需要全局协调；
 * 3、生成速度快；
 * <p>
 * <p>参考：<a href="https://github.com/ulid/spec">Sequence</a>
 */
public class ULIDKeyGenerator implements IKeyGenerator {

    private static final char[] ENCODING_CHARS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
        'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
        'Y', 'Z'
    };

    private static final long TIMESTAMP_OVERFLOW_MASK = 0xFFFF_0000_0000_0000L;

    private static final ThreadLocal<StringBuilder> THREAD_LOCAL_BUILDER =
        ThreadLocal.withInitial(() -> new StringBuilder(26));

    private long lastTimestamp = 0;

    private long lastRandom = 0;

    @Override
    public Object generate(Object entity, String keyColumn) {
        return nextId();
    }

    /**
     * 生成一个 ULID
     *
     * @return ULID
     */
    public String nextId() {
        return generateULID(System.currentTimeMillis()).toLowerCase();
    }

    /**
     * 生成一个严格单调的 ULID
     *
     * @return ULID
     */
    public synchronized String nextMonotonicId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp > lastTimestamp) {
            lastTimestamp = timestamp;
            lastRandom = ThreadLocalRandom.current().nextLong();
        } else {
            lastRandom++;
            if (lastRandom == 0) {
                timestamp = waitNextMillis(lastTimestamp);
                lastTimestamp = timestamp;
                lastRandom = ThreadLocalRandom.current().nextLong();
            }
        }
        return generateULID(lastTimestamp, lastRandom).toLowerCase();
    }

    private String generateULID(long timestamp) {
        return generateULID(timestamp, ThreadLocalRandom.current().nextLong());
    }

    private String generateULID(long timestamp, long random) {
        checkTimestamp(timestamp);
        StringBuilder builder = THREAD_LOCAL_BUILDER.get();
        builder.setLength(0);

        appendCrockford(builder, timestamp, 10);
        appendCrockford(builder, random, 16);

        return builder.toString();
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    private static void appendCrockford(StringBuilder builder, long value, int count) {
        for (int i = (count - 1) * 5; i >= 0; i -= 5) {
            int index = (int) ((value >>> i) & 0x1F);
            builder.append(ENCODING_CHARS[index]);
        }
    }

    private static void checkTimestamp(long timestamp) {
        if ((timestamp & TIMESTAMP_OVERFLOW_MASK) != 0) {
            throw new IllegalArgumentException("ULID does not support timestamps after +10889-08-02T05:31:50.655Z!");
        }
    }
}
