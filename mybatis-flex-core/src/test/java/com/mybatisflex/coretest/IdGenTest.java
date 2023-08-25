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

package com.mybatisflex.coretest;

import com.mybatisflex.core.keygen.IKeyGenerator;
import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import com.mybatisflex.core.keygen.KeyGenerators;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.LongStream;

/**
 * 数据库 ID 生成器测试。
 *
 * @author 王帅
 * @since 2023-05-12
 */
public class IdGenTest {

    @Test
    public void snowFlakeID() {
        int size = 10_0000;
        long[] ids = new long[size];
        IKeyGenerator keyGenerator = KeyGeneratorFactory.getKeyGenerator(KeyGenerators.snowFlakeId);
        for (int i = 0; i < size; i++) {
            ids[i] = (Long) keyGenerator.generate(null, null);
        }
        Assert.assertEquals(size, LongStream.of(ids).distinct().count());
    }

    @Test
    public void flexID() {
        int size = 100_0000;
        long[] ids = new long[size];
        IKeyGenerator keyGenerator = KeyGeneratorFactory.getKeyGenerator(KeyGenerators.flexId);
        for (int i = 0; i < size; i++) {
            ids[i] = (Long) keyGenerator.generate(null, null);
        }
        Assert.assertEquals(size, LongStream.of(ids).distinct().count());
    }

}
