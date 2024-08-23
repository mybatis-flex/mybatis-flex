package com.mybatisflex.core.util;

import com.mybatisflex.core.mybatis.MappedStatementTypes;
import org.junit.Assert;
import org.junit.Test;

public class MappedStatementTypesTest {

    @Test
    public void test() {
        MappedStatementTypes.clear();

        MappedStatementTypes.setCurrentType(String.class);
        MappedStatementTypes.setCurrentType(MappedStatementTypesTest.class);
        MappedStatementTypes.setCurrentType(StringUtilTest.class);

        Assert.assertEquals(StringUtilTest.class, MappedStatementTypes.getCurrentType());
        System.out.println(MappedStatementTypes.getCurrentType());
        MappedStatementTypes.clear();

        Assert.assertEquals(MappedStatementTypesTest.class, MappedStatementTypes.getCurrentType());
        System.out.println(MappedStatementTypes.getCurrentType());
        MappedStatementTypes.clear();

        Assert.assertEquals(String.class, MappedStatementTypes.getCurrentType());
        System.out.println(MappedStatementTypes.getCurrentType());
        MappedStatementTypes.clear();

        Assert.assertNull(MappedStatementTypes.getCurrentType());
        System.out.println(MappedStatementTypes.getCurrentType());
    }
}
