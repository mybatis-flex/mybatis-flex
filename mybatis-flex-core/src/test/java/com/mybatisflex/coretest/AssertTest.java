package com.mybatisflex.coretest;

import com.mybatisflex.core.query.Assert;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Test;

import static com.mybatisflex.core.query.QueryMethods.column;


public class AssertTest {


    @Test(expected = IllegalArgumentException.class)
    public void testAssert() {
        String testArg = null;
        QueryWrapper queryWrapper = new QueryWrapper()
            .from("account")
            .where(column("id").eq(1, Assert::notNull))
            .and(column("name").eq(testArg, Assert::hasText));
    }
}
