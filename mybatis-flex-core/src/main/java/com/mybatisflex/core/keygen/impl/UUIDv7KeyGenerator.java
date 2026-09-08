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

import java.security.SecureRandom;
import java.util.UUID;

/**
 * UUIDv7 主键生成器，返回 32 位小写、无连字符的十六进制字符串。
 * <p>
 * 时间戳精度为毫秒，同一毫秒内以及时钟回拨时不保证严格递增。
 * 随机碰撞概率极低，但不保证绝对唯一。
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9562.html#section-5.7">RFC 9562 UUIDv7</a>
 */
public class UUIDv7KeyGenerator implements IKeyGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long MAX_TIMESTAMP = 0xFFFFFFFFFFFFL;

    @Override
    public Object generate(Object entity, String keyColumn) {
        long randomA = RANDOM.nextLong();
        long randomB = RANDOM.nextLong();
        long timestamp = System.currentTimeMillis();
        return createUUID(timestamp, randomA, randomB).toString().replace("-", "");
    }

    static UUID createUUID(long timestamp, long randomA, long randomB) {
        if (timestamp < 0 || timestamp > MAX_TIMESTAMP) {
            throw new IllegalArgumentException("UUIDv7 timestamp must be an unsigned 48-bit Unix millisecond value.");
        }
        // 48 位时间戳、4 位版本号 7、12 位随机数。
        long msb = (timestamp << 16) | 0x7000L | (randomA & 0x0FFFL);
        // 2 位变体 10、62 位随机数。
        long lsb = 0x8000000000000000L | (randomB & 0x3FFFFFFFFFFFFFFFL);
        return new UUID(msb, lsb);
    }
}
