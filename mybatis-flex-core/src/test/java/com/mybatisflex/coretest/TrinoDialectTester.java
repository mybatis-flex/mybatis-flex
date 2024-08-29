package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Assert;
import org.junit.Test;

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

/**
 * trino使用示例
 */
public class TrinoDialectTester {
    private static final IDialect DIALECT = new CommonsDialectImpl(KeywordWrap.NONE, LimitOffsetProcessor.SQLSERVER);

    /**
     * 分页
     */
    @Test
    public void testPageSql() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.in("100", "200"))
            .and(ACCOUNT.SEX.eq(1))
            .orderBy(ACCOUNT.ID.desc())
            .limit(10, 10);

        String sql = DIALECT.forSelectByQuery(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT * FROM tb_account WHERE id IN (?, ?) AND sex = ? ORDER BY id DESC OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY", sql);
    }

    /**
     * 聚合函数的过滤器
     */
    @Test
    public void testFilteringDuringAggregation() {
        QueryWrapper query = new QueryWrapper()
            .select(ACCOUNT.SEX)
            .select("count(*) filter(where age > 10)")
            .from(ACCOUNT)
            .groupBy(ACCOUNT.SEX);

        String sql = DIALECT.forSelectByQuery(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT sex, count(*) filter(where age > 10) FROM tb_account GROUP BY sex", sql);
    }

    /**
     * 利用array函数实现mysql group_concat功能
     */
    @Test
    public void testArrayFunction() {
        QueryWrapper query = new QueryWrapper()
            .select(ACCOUNT.SEX)
            .select("array_join(array_agg(user_name), ',')")
            .from(ACCOUNT)
            .groupBy(ACCOUNT.SEX);

        String sql = DIALECT.forSelectByQuery(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT sex, array_join(array_agg(user_name), ',') FROM tb_account GROUP BY sex", sql);
    }

    /**
     * 窗口函数
     */
    @Test
    public void testOverFunction() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(QueryWrapper.create().select(ACCOUNT.ID, ACCOUNT.USER_NAME)
            .select("ROW_NUMBER() OVER(PARTITION BY sex ORDER BY birthday) AS row_num")
            .from(ACCOUNT))
            .where("row_num = 1");
        String sql = DIALECT.forSelectByQuery(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT * FROM (SELECT id, user_name, ROW_NUMBER() OVER(PARTITION BY sex ORDER BY birthday) AS row_num FROM tb_account) WHERE  row_num = 1 ", sql);
    }

    /**
     * with 子查询
     */
    @Test
    public void testWith() {
        QueryWrapper query = new QueryWrapper()
            .with("t").asSelect(
                select().from(ACCOUNT).where(ACCOUNT.ID.ge(100))
            )
            .select()
            .from("t")
            .where("t.sex = 0");

        String sql = DIALECT.forSelectByQuery(query);
        System.out.println(sql);
        Assert.assertEquals("WITH t AS (SELECT * FROM tb_account WHERE id >= ?) SELECT * FROM t WHERE  t.sex = 0 ", sql);
    }
}
