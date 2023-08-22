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
package com.mybatisflex.core.keygen.impl;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.keygen.IKeyGenerator;
import com.mybatisflex.core.util.StringUtil;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * <p>雪花算法 ID 生成器。
 *
 * <ul>
 *     <li>最高 1 位固定值 0，因为生成的 ID 是正整数；
 *     <li>接下来 41 位存储毫秒级时间戳，2 ^ 41 / ( 1000 * 60 * 60 * 24 * 365) = 69，大概可以使用 69 年；
 *     <li>再接下 10 位存储机器码，包括 5 位 dataCenterId 和 5 位 workerId，最多可以部署 2 ^ 10 = 1024 台机器；
 *     <li>最后 12 位存储序列号，同一毫秒时间戳时，通过这个递增的序列号来区分，即对于同一台机器而言，同一毫秒时间戳下，可以生成 2 ^ 12 = 4096 个不重复 ID。
 * </ul>
 *
 * <p>优化自开源项目：<a href="https://gitee.com/yu120/sequence">Sequence</a>
 *
 * @author 王帅
 * @since 2023-05-12
 */
public class SnowFlakeIDKeyGenerator implements IKeyGenerator {

    /**
     * 工作机器 ID 占用的位数（5bit）。
     */
    private static final long WORKER_ID_BITS = 5L;
    /**
     * 数据中心 ID 占用的位数（5bit）。
     */
    private static final long DATA_CENTER_ID_BITS = 5L;
    /**
     * 序号占用的位数（12bit）。
     */
    private static final long SEQUENCE_BITS = 12L;
    /**
     * 工作机器 ID 占用 5bit 时的最大值 31。
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /**
     * 数据中心 ID 占用 5bit 时的最大值 31。
     */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    /**
     * 序号掩码，用于与自增后的序列号进行位“与”操作，如果值为 0，则代表自增后的序列号超过了 4095。
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    /**
     * 工作机器 ID 位需要左移的位数（12bit）。
     */
    private static final long WORK_ID_SHIFT = SEQUENCE_BITS;
    /**
     * 数据中心 ID 位需要左移的位数（12bit + 5bit）。
     */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * 时间戳需要左移的位数（12bit + 5bit + 5bit）。
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    /**
     * 时间起始标记点，一旦确定不能变动（2023-04-02 13:01:00）。
     */
    private static long twepoch = 1680411660000L;
    /**
     * 可容忍的时间偏移量。
     */
    private static long offsetPeriod = 5L;

    /**
     * 工作机器 ID。
     */
    private final long workerId;
    /**
     * 数据中心 ID。
     */
    private final long dataCenterId;

    /**
     * IP 地址信息，用来生成工作机器 ID 和数据中心 ID。
     */
    protected InetAddress address;
    /**
     * 同一毫秒内的最新序号，最大值可为（2^12 - 1 = 4095）。
     */
    private long sequence;
    /**
     * 上次生产 ID 时间戳。
     */
    private long lastTimeMillis = -1L;

    /**
     * 雪花算法 ID 生成器。
     */
    public SnowFlakeIDKeyGenerator() {
        this(null);
    }

    /**
     * 根据 IP 地址计算数据中心 ID 和工作机器 ID 生成数据库 ID。
     *
     * @param address IP 地址
     */
    public SnowFlakeIDKeyGenerator(InetAddress address) {
        this.address = address;
        this.dataCenterId = getDataCenterId(MAX_DATA_CENTER_ID);
        this.workerId = getWorkerId(dataCenterId, MAX_WORKER_ID);
    }

    /**
     * 根据数据中心 ID 和工作机器 ID 生成数据库 ID。
     *
     * @param workerId     工作机器 ID
     * @param dataCenterId 数据中心 ID
     */
    public SnowFlakeIDKeyGenerator(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                String.format("workerId must be greater than 0 and less than %d.", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(
                String.format("dataCenterId must be greater than 0 and less than %d.", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 根据 MAC + PID 的 hashCode 获取 16 个低位生成工作机器 ID。
     */
    protected long getWorkerId(long dataCenterId, long maxWorkerId) {
        StringBuilder mpId = new StringBuilder();
        mpId.append(dataCenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtil.isNotBlank(name)) {
            // GET jvmPid
            mpId.append(name.split("@")[0]);
        }
        // MAC + PID 的 hashCode 获取16个低位
        return (mpId.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 根据网卡 MAC 地址计算余数作为数据中心 ID。
     */
    protected long getDataCenterId(long maxDataCenterId) {
        long id = 0L;
        try {
            if (address == null) {
                address = InetAddress.getLocalHost();
            }
            NetworkInterface network = NetworkInterface.getByInetAddress(address);
            if (null == network) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 2]) | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
                    id = id % (maxDataCenterId + 1);
                }
            }
        } catch (Exception e) {
            throw FlexExceptions.wrap(e, "dataCenterId: %s", e.getMessage());
        }
        return id;
    }

    @Override
    public Object generate(Object entity, String keyColumn) {
        return nextId();
    }

    /**
     * 获取下一个 ID。
     */
    public synchronized long nextId() {
        long currentTimeMillis = System.currentTimeMillis();
        // 当前时间小于上一次生成 ID 使用的时间，可能出现服务器时钟回拨问题。
        if (currentTimeMillis < lastTimeMillis) {
            long offset = lastTimeMillis - currentTimeMillis;
            // 在可容忍的时间差值之内等待时间恢复正常
            if (offset <= offsetPeriod) {
                try {
                    wait(offset << 1L);
                    currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis < lastTimeMillis) {
                        throw FlexExceptions.wrap("Clock moved backwards, please check the time. Current timestamp: %d, last used timestamp: %d", currentTimeMillis, lastTimeMillis);
                    }
                } catch (InterruptedException e) {
                    throw FlexExceptions.wrap(e);
                }
            } else {
                throw FlexExceptions.wrap("Clock moved backwards, please check the time. Current timestamp: %d, last used timestamp: %d", currentTimeMillis, lastTimeMillis);
            }
        }

        if (currentTimeMillis == lastTimeMillis) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 同一毫秒的序列数已经达到最大
                currentTimeMillis = tilNextMillis(lastTimeMillis);
            }
        } else {
            // 不同毫秒内，序列号置为 0。
            sequence = 0L;
        }

        // 记录最后一次使用的毫秒时间戳
        lastTimeMillis = currentTimeMillis;

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return ((currentTimeMillis - twepoch) << TIMESTAMP_SHIFT)
            | (dataCenterId << DATA_CENTER_ID_SHIFT)
            | (workerId << WORK_ID_SHIFT)
            | sequence;
    }

    /**
     * 获取指定时间戳的接下来的时间戳。
     */
    private long tilNextMillis(long lastTimestamp) {
        long currentTimeMillis = System.currentTimeMillis();
        while (currentTimeMillis <= lastTimestamp) {
            currentTimeMillis = System.currentTimeMillis();
        }
        return currentTimeMillis;
    }

    public static long getTwepoch() {
        return twepoch;
    }

    public static void setTwepoch(long twepoch) {
        SnowFlakeIDKeyGenerator.twepoch = twepoch;
    }

    public static long getOffsetPeriod() {
        return offsetPeriod;
    }

    public static void setOffsetPeriod(long offsetPeriod) {
        SnowFlakeIDKeyGenerator.offsetPeriod = offsetPeriod;
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getLastTimeMillis() {
        return lastTimeMillis;
    }

    public void setLastTimeMillis(long lastTimeMillis) {
        this.lastTimeMillis = lastTimeMillis;
    }
}
