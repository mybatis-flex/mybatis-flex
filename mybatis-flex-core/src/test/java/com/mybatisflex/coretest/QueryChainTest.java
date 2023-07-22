package com.mybatisflex.coretest;

import com.mybatisflex.core.query.QueryChain;
import org.junit.Test;

public class QueryChainTest {

    @Test
    public void test() {
        QueryChain queryChain = new QueryChain();
        queryChain.from("aaa")
        .leftJoin("bbb").on("aaa.id = bbb.x")
            .where(Account::getId).ge(100);

        System.out.println(queryChain.toSQL());
    }
}
