package com.mybatisflex.coretest;

import com.mybatisflex.core.keygen.IKeyGenerator;
import com.mybatisflex.core.keygen.KeyGeneratorFactory;
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
        int size = 10;
        long[] ids = new long[size];
        IKeyGenerator keyGenerator = KeyGeneratorFactory.getKeyGenerator("snowFlakeId");
        for (int i = 0; i < size; i++) {
            ids[i] = (Long) keyGenerator.generate(null, null);
        }
        Assert.assertEquals(size, LongStream.of(ids).distinct().count());
    }

}