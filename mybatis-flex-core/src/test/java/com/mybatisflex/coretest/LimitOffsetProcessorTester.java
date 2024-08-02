package com.mybatisflex.coretest;

import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.coretest.table.ArticleTableDef.ARTICLE;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.dialect.impl.Sqlserver2005DialectImpl;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Assert;
import org.junit.Test;

public class LimitOffsetProcessorTester {

    @Test
    public void testSqlServer2005() {
        IDialect dialect = new CommonsDialectImpl(KeywordWrap.SQUARE_BRACKETS, LimitOffsetProcessor.SQLSERVER_2005);
        QueryWrapper oneTableQueryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .orderBy(ACCOUNT.ID.desc()).limit(10, 10);
        String sql = dialect.forSelectByQuery(oneTableQueryWrapper);
        System.out.println(sql);
        Assert.assertEquals("WITH temp_datas AS(SELECT ROW_NUMBER() OVER ( ORDER BY [id] DESC) as __rn, * FROM [tb_account]) SELECT * FROM temp_datas WHERE __rn BETWEEN 11 AND 20 ORDER BY __rn", sql);

        QueryWrapper twoTablequeryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .leftJoin(ARTICLE).on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .orderBy(ACCOUNT.ID.desc()).limit(10, 10);
        sql = dialect.forSelectByQuery(twoTablequeryWrapper);
        System.out.println(sql);
        Assert.assertEquals("WITH temp_datas AS(" +
            "SELECT ROW_NUMBER() OVER ( ORDER BY [tb_account].[id] DESC) as __rn, * FROM [tb_account] LEFT JOIN [tb_article] ON [tb_article].[account_id] = [tb_account].[id]" +
            ") SELECT * FROM temp_datas WHERE __rn BETWEEN 11 AND 20 ORDER BY __rn", sql);
    }

    @Test
    public void testSqlServer2005With() {
        IDialect dialect = new Sqlserver2005DialectImpl();
        QueryWrapper allTablequeryWrapper = QueryWrapper.create()
            .select(QueryMethods.distinct(ACCOUNT.ID, ARTICLE.TITLE, ARTICLE.ID.as("ar_id")),
                QueryMethods.count(ACCOUNT.ID).as("cnt")).from(ACCOUNT)
            .leftJoin(ARTICLE).on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID)).orderBy(ACCOUNT.ID.desc()).limit(10, 10);
        String sql = dialect.forSelectByQuery(allTablequeryWrapper);
        System.out.println(sql);
        Assert.assertEquals(
            "WITH temp_datas AS(SELECT __Tab.*, ROW_NUMBER() OVER (  ORDER BY [tb_account].[id] DESC) as __rn FROM ( SELECT DISTINCT [tb_account].[id], [tb_article].[title], [tb_article].[id] AS [ar_id], COUNT([tb_account].[id]) AS [cnt] FROM [tb_account] LEFT JOIN [tb_article] ON [tb_article].[account_id] = [tb_account].[id] ) as __Tab )  SELECT * FROM temp_datas WHERE __rn BETWEEN 11 AND 20 ORDER BY __rn",
            sql);
    }
}
