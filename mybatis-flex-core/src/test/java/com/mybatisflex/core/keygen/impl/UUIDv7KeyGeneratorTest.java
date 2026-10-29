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

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class UUIDv7KeyGeneratorTest {

    @Test
    public void generatesCompactUUIDv7() {
        String value = (String) new UUIDv7KeyGenerator().generate(null, null);
        UUID uuid = parseCompact(value);
        assertEquals(7, uuid.version());
        assertEquals(2, uuid.variant());
    }

    @Test
    public void preservesTimestampAndMasksRandomBits() {
        long timestamp = 0x0123456789ABL;
        for (long random : new long[]{0L, -1L}) {
            UUID uuid = UUIDv7KeyGenerator.createUUID(timestamp, random, random);
            assertEquals(timestamp, uuid.getMostSignificantBits() >>> 16);
            assertEquals(7, uuid.version());
            assertEquals(2, uuid.variant());
            assertEquals(random & 0xFFFL, uuid.getMostSignificantBits() & 0xFFFL);
            assertEquals(random & 0x3FFFFFFFFFFFFFFFL,
                uuid.getLeastSignificantBits() & 0x3FFFFFFFFFFFFFFFL);
        }
    }

    @Test
    public void acceptsTimestampBoundaries() {
        for (long timestamp : new long[]{0L, 0xFFFFFFFFFFFFL}) {
            UUID uuid = UUIDv7KeyGenerator.createUUID(timestamp, -1L, -1L);
            assertEquals(timestamp, uuid.getMostSignificantBits() >>> 16);
            assertEquals(7, uuid.version());
            assertEquals(2, uuid.variant());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeTimestamp() {
        UUIDv7KeyGenerator.createUUID(-1L, 0L, 0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsTimestampOverflow() {
        UUIDv7KeyGenerator.createUUID(0x1000000000000L, 0L, 0L);
    }

    @Test
    public void sortsAcrossMillisecondsRegardlessOfRandomBits() {
        String earlier = compact(UUIDv7KeyGenerator.createUUID(1000L, -1L, -1L));
        String later = compact(UUIDv7KeyGenerator.createUUID(1001L, 0L, 0L));
        assertTrue(earlier.compareTo(later) < 0);
    }

    @Test
    public void preservesTimeOnClockRollback() {
        UUID later = UUIDv7KeyGenerator.createUUID(1001L, 0L, 0L);
        UUID rolledBack = UUIDv7KeyGenerator.createUUID(1000L, 0L, 0L);
        assertEquals(1000L, rolledBack.getMostSignificantBits() >>> 16);
        assertTrue(compact(rolledBack).compareTo(compact(later)) < 0);
    }

    @Test(timeout = 30000)
    public void generatesConcurrentlyWithoutDuplicatesInSample() throws Exception {
        UUIDv7KeyGenerator generator = new UUIDv7KeyGenerator();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<List<String>>> futures = new ArrayList<>();
            for (int thread = 0; thread < 8; thread++) {
                futures.add(executor.submit(() -> {
                    List<String> values = new ArrayList<>();
                    for (int i = 0; i < 2000; i++) {
                        String value = (String) generator.generate(null, null);
                        UUID uuid = parseCompact(value);
                        assertEquals(7, uuid.version());
                        assertEquals(2, uuid.variant());
                        values.add(value);
                    }
                    return values;
                }));
            }
            Set<String> unique = new HashSet<>();
            for (Future<List<String>> future : futures) {
                for (String value : future.get(20, TimeUnit.SECONDS)) {
                    assertTrue("Duplicate UUIDv7 in sample", unique.add(value));
                }
            }
            assertEquals(16000, unique.size());
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private static String compact(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    private static UUID parseCompact(String value) {
        assertTrue(value.matches("[0-9a-f]{32}"));
        return UUID.fromString(value.substring(0, 8) + "-" + value.substring(8, 12)
            + "-" + value.substring(12, 16) + "-" + value.substring(16, 20)
            + "-" + value.substring(20));
    }
}
