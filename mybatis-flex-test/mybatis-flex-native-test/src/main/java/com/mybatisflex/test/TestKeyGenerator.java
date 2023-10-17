package com.mybatisflex.test;

import com.mybatisflex.core.keygen.IKeyGenerator;

public class TestKeyGenerator implements IKeyGenerator {
    @Override
    public Object generate(Object entity, String keyColumn) {
        return System.currentTimeMillis() / 1000;
    }
}
