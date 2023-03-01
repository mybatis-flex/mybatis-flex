package com.mybatisflex.test;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.CommonsDialectImpl;
import com.mybatisflex.core.querywrapper.CPI;
import com.mybatisflex.core.querywrapper.QueryWrapper;
import org.junit.Test;

import java.util.Arrays;

import static com.mybatisflex.core.querywrapper.QueryMethods.*;
import static com.mybatisflex.test.table.Tables.ACCOUNT;
import static com.mybatisflex.test.table.Tables.ARTICLE;

public class AccountSqlTester {

    @Test
    public void testJava(){
        StringBuilder sql = new StringBuilder("select * from xxx");
//        sql.(7," 你好 ");
        System.out.println(sql);
    }

    @Test
    public void testSelectCountSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectCountByQuery(queryWrapper);
        System.out.println(sql);
    }


    @Test
    public void testrSelectLimitSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(ACCOUNT.ALL_COLUMNS)
                .select(ARTICLE.ID.as("article_id"))
                .select(distinct(ARTICLE.ID))
                .select(max(ACCOUNT.SEX))
                .select(count(distinct(ARTICLE.ID)))
                .from(ACCOUNT).as("a1")
//                .leftJoin(newWrapper().select().from(ARTICLE).where(ARTICLE.ID.ge(100))).as("aaa")
                .leftJoin(ARTICLE).as("b1")
                .on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
                .where(ACCOUNT.ID.ge(select(ARTICLE.ID).from(ARTICLE).as("cc").where(ARTICLE.ID.eq(111))))
                .and((true ? noCondition() : ARTICLE.ID.ge(22211)).and(ACCOUNT.ID.eq(10011)).when(false))
                .and(ACCOUNT.USER_NAME.like("michael"))
                .and(ARTICLE.ID.in(select(ARTICLE.ID).from("aaa")))
                .and(
                        notExist(
                                selectOne().from("aaa").where(ARTICLE.ID.ge(333))
                        )
                )
                .groupBy(ACCOUNT.ID).having(ARTICLE.ID.ge(0))
//                .and("bbb.id > ?",100)
                .orderBy(ACCOUNT.ID.desc())
                .limit(10, 10);

        String mysqlSql = new CommonsDialectImpl().forSelectListByQuery(queryWrapper);
        System.out.println(">>>>> mysql: \n" + mysqlSql);
        System.out.println(">>>>> mysql: \n" + Arrays.toString(CPI.getValueArray(queryWrapper)));

//        String oracleSql = new OracleDialect().forSelectListByQuery(CPI.getQueryTable(queryWrapper).getName(), queryWrapper);
//        System.out.println(">>>>> oracle: " + oracleSql);
//
//        String informixSql = new InformixDialect().forSelectListByQuery(CPI.getQueryTable(queryWrapper).getName(), queryWrapper);
//        System.out.println(">>>>> informix: " + informixSql);
    }

}
