package com.mybatisflex.coretest;

import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Test;

import java.util.Arrays;

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.mybatisflex.core.query.QueryMethods.union;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.coretest.table.ArticleTableDef.ARTICLE;

public class WithSQLTester {


    @Test
    public void testWithSql1() {
        QueryWrapper query = new QueryWrapper()
                .with("CTE").asSelect(
                        select().from(ARTICLE).where(ARTICLE.ID.ge(100))
                )
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.SEX.eq(1));

        System.out.println(query.toSQL());
    }


    @Test
    public void testWithSql2() {
        QueryWrapper query = new QueryWrapper()
                .withRecursive("CTE").asSelect(
                        select().from(ARTICLE).where(ARTICLE.ID.ge(100))
                )
                .from(ACCOUNT)
                .where(ACCOUNT.SEX.eq(1));

        System.out.println(query.toSQL());
    }

    @Test
    public void testWithSql3() {
        QueryWrapper query = new QueryWrapper()
                .withRecursive("CTE", "id", "value").asSelect(
                        QueryWrapper.create().from(ARTICLE).where(ARTICLE.ID.ge(100))
                )
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.SEX.eq(1));

        System.out.println(query.toSQL());
    }


    @Test
    public void testWithSql4() {
        QueryWrapper query = new QueryWrapper()
                .with("CTE").asSelect(
                        select().from(ARTICLE).where(ARTICLE.ID.ge(100))
                )
                .with("xxx").asSelect(
                        select().from(ARTICLE).where(ARTICLE.ID.ge(200))
                )
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.SEX.eq(1));

        System.out.println(query.toSQL());
    }

    @Test
    public void testWithSql5() {
        QueryWrapper query = new QueryWrapper()
                .withRecursive("CTE").asSelect(
                        select().from(ARTICLE).where(ARTICLE.ID.ge(100))
                )
                .with("xxx", "id", "name").asValues(
                        Arrays.asList("a", "b"),
                        union(
                                select().from(ARTICLE).where(ARTICLE.ID.ge(200))
                        )

                )
                .from(ACCOUNT)
                .where(ACCOUNT.SEX.eq(1));

        System.out.println(query.toSQL());
    }

}
