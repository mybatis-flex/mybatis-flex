package com.mybatisflex.core.util;

import org.junit.Assert;
import org.junit.Test;

public class LambdaUtilTest {

    @Test
    public void testIssue516() {

        for (int i = 0; i < 100; i++) {
            LambdaUtil.getFieldName(TestAccount::getName);
        }

        Assert.assertEquals(LambdaUtil.getFieldNameMap().size(), 1);
        System.out.println("testIssue516");
    }
}
