package com.mybatisflex.test;

import com.mybatisflex.core.query.QueryMethods;
import org.junit.Test;

import java.math.BigDecimal;

public class ArithmeticQueryColumnTest {
    @Test
    public void testAddWithCondition() {
        BigDecimal bigDecimal = Demo.create()
            .select(QueryMethods.sum(Demo::getFirstField)
                .add(QueryMethods.sum(QueryMethods.if_(QueryMethods.column(Demo::getForeignId).eq("1"), QueryMethods.column(Demo::getSecondField), QueryMethods.number(0)))))
            .where(Demo::getUserId).eq("1").objAsOpt(BigDecimal.class).orElse(BigDecimal.ZERO);
        System.out.println(bigDecimal);
    }

}
