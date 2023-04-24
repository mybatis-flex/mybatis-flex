package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcesser;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.junit.Test;

import java.util.Arrays;

import static com.mybatisflex.core.query.QueryMethods.*;
import static com.mybatisflex.coretest.table.Tables.ACCOUNT;
import static com.mybatisflex.coretest.table.Tables.ARTICLE;

public class AccountSqlTester {


    @Test
    public void testSelectSql() {
        QueryWrapper query = new QueryWrapper()
                .select()
                .from(ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }

    @Test
    public void testSelectColumnsSql() {
        QueryWrapper query = new QueryWrapper()
                .select(ACCOUNT.ID, ACCOUNT.USER_NAME)
                .from(ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }

    @Test
    public void testSelect1ColumnsSql() {
        QueryWrapper query = new QueryWrapper()
                .select(ACCOUNT.ID, ACCOUNT.USER_NAME,
                        ARTICLE.ID.as("articleId"), ARTICLE.TITLE)
                .from(ACCOUNT.as("a"), ARTICLE.as("b"))
                .where(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID));

        IDialect dialect = new CommonsDialectImpl(KeywordWrap.NONE, LimitOffsetProcesser.MYSQL);
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }

    @Test
    public void testSelectColumnsAndFunctionsSql() {
        QueryWrapper query = new QueryWrapper()
                .select(ACCOUNT.ID, ACCOUNT.USER_NAME, max(ACCOUNT.BIRTHDAY), avg(ACCOUNT.SEX).as("sex_avg"))
                .from(ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }


    @Test
    public void testSelectAllColumnsSql() {
        QueryWrapper query = new QueryWrapper()
                .select(ACCOUNT.ALL_COLUMNS)
                .from(ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }


    @Test
    public void testUnionSql() {
        QueryWrapper query = new QueryWrapper()
                .select(ACCOUNT.ID)
                .from(ACCOUNT)
                .orderBy(ACCOUNT.ID.desc())
                .union(select(ARTICLE.ID).from(ARTICLE))
                .unionAll(select(ARTICLE.ID).from(ARTICLE));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
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
    public void testWhereSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testWhere2Sql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(column("A.*"), column("b.x"))
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(column("aaa").in("michael", "aaa"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }


    @Test
    public void testWhereCond1Sql() {
        boolean flag = false;
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100).when(flag))
                .and(ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);

        Object[] valueArray = CPI.getValueArray(queryWrapper);
        System.out.println(Arrays.toString(valueArray));
    }


    @Test
    public void testWhereCond2Sql() {
        boolean flag = false;
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(flag ? ACCOUNT.ID.ge(100) : noCondition())
                .and(ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);

        Object[] valueArray = CPI.getValueArray(queryWrapper);
        System.out.println(Arrays.toString(valueArray));
    }


    @Test
    public void testWhereExistSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(
                        exists(
                                selectOne().from(ARTICLE).as("a").where(ARTICLE.ID.ge(100))
                        )
                );

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }


    @Test
    public void testWhereAndOrSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(ACCOUNT.SEX.eq(1).or(ACCOUNT.SEX.eq(2)))
                .or(ACCOUNT.AGE.in(18, 19, 20).or(ACCOUNT.USER_NAME.like("michael")));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testWhereSelectSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(
                        select(ARTICLE.ACCOUNT_ID).from(ARTICLE).where(ARTICLE.ID.ge(100))
                ));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testGroupSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .groupBy(ACCOUNT.USER_NAME);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testHavingSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .groupBy(ACCOUNT.USER_NAME)
                .having(ACCOUNT.AGE.between(18, 25));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testJoinSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
                .where(ACCOUNT.AGE.ge(10));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testJoin2Sql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .leftJoin(ARTICLE).on(
                        ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID).and(ACCOUNT.AGE.eq(18))
                )
                .where(ACCOUNT.AGE.ge(10));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testJoin3Sql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .leftJoin(
                        select().from(ARTICLE).where(ARTICLE.ID.ge(100))
                ).as("a").on(
                        ACCOUNT.ID.eq(raw("a.id"))
                )
                .where(ACCOUNT.AGE.ge(10));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testOrderBySql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .orderBy(ACCOUNT.AGE.asc(), ACCOUNT.USER_NAME.desc().nullsLast());

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testDeleteSql() {
        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Account.class);
        String sql = dialect.forDeleteEntityById(tableInfo);
        System.out.println(sql);
    }


    @Test
    public void testLimitOffset() {

        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .orderBy(ACCOUNT.ID.desc())
                .limit(10)
                .offset(20);

        IDialect dialect1 = new CommonsDialectImpl();
        String sql1 = dialect1.buildSelectSql(queryWrapper);
        System.out.println(sql1);

        IDialect dialect2 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.ORACLE);
        String sql2 = dialect2.buildSelectSql(queryWrapper);
        System.out.println(sql2);

        IDialect dialect3 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.DB2);
        String sql3 = dialect3.buildSelectSql(queryWrapper);
        System.out.println(sql3);

        IDialect dialect4 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.POSTGRESQL);
        String sql4 = dialect4.buildSelectSql(queryWrapper);
        System.out.println(sql4);

        IDialect dialect5 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.INFORMIX);
        String sql5 = dialect5.buildSelectSql(queryWrapper);
        System.out.println(sql5);

        IDialect dialect6 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.SYBASE);
        String sql6 = dialect6.buildSelectSql(queryWrapper);
        System.out.println(sql6);


        IDialect dialect7 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.FIREBIRD);
        String sql7 = dialect7.buildSelectSql(queryWrapper);
        System.out.println(sql7);
    }


    @Test
    public void testSelectLimitSql() {
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
                        notExists(
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
