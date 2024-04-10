package com.mybatisflex.coretest;

import com.mybatisflex.core.util.LambdaUtil;
import org.junit.Assert;
import org.junit.Test;

public class LambdaUtilTest {

    @Test
    public void testGetFieldName() {
        String fieldName = LambdaUtil.getFieldName(Account::getAge);
        Assert.assertEquals(fieldName, "age");
        System.out.println(fieldName);
    }
}
