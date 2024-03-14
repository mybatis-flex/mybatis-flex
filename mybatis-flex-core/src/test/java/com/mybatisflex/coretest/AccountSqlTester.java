/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mybatisflex.coretest;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.dialect.impl.OracleDialect;
import com.mybatisflex.core.query.DistinctQueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.RawQueryColumn;
import com.mybatisflex.core.query.SqlOperators;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.table.TableManager;
import com.mybatisflex.coretest.table.AccountTableDef;
import com.mybatisflex.coretest.table.ArticleTableDef;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static com.mybatisflex.core.query.QueryMethods.avg;
import static com.mybatisflex.core.query.QueryMethods.case_;
import static com.mybatisflex.core.query.QueryMethods.column;
import static com.mybatisflex.core.query.QueryMethods.convert;
import static com.mybatisflex.core.query.QueryMethods.count;
import static com.mybatisflex.core.query.QueryMethods.distinct;
import static com.mybatisflex.core.query.QueryMethods.exists;
import static com.mybatisflex.core.query.QueryMethods.left;
import static com.mybatisflex.core.query.QueryMethods.max;
import static com.mybatisflex.core.query.QueryMethods.noCondition;
import static com.mybatisflex.core.query.QueryMethods.notExists;
import static com.mybatisflex.core.query.QueryMethods.raw;
import static com.mybatisflex.core.query.QueryMethods.select;
import static com.mybatisflex.core.query.QueryMethods.selectCountOne;
import static com.mybatisflex.core.query.QueryMethods.selectOne;
import static com.mybatisflex.core.query.QueryMethods.year;
import static com.mybatisflex.coretest.table.Account01TableDef.ACCOUNT01;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.coretest.table.ArticleTableDef.ARTICLE;

public class AccountSqlTester {

    @Test
    public void testAlisa() {
        AccountTableDef a1 = ACCOUNT.as("a1");
        AccountTableDef a2 = ACCOUNT.as("a2");
        ArticleTableDef ar = ARTICLE.as("ar");
        QueryWrapper queryWrapper = new QueryWrapper()
            .select(ar.CONTENT, a1.ID, a2.AGE)
            .from(ar)
            .leftJoin(a1).on(a1.ID.eq(ar.ACCOUNT_ID))
            .leftJoin(a2).on(a2.ID.eq(ar.ACCOUNT_ID));
        String sql = SqlFormatter.format(queryWrapper.toSQL());
        Assert.assertEquals("SELECT\n" +
            "  ` ar `.` content `,\n" +
            "  ` a1 `.` id `,\n" +
            "  ` a2 `.` age `\n" +
            "FROM\n" +
            "  ` tb_article ` AS ` ar `\n" +
            "  LEFT JOIN ` tb_account ` AS ` a1 ` ON ` a1 `.` id ` = ` ar `.` account_id `\n" +
            "  LEFT JOIN ` tb_account ` AS ` a2 ` ON ` a2 `.` id ` = ` ar `.` account_id `", sql);
        System.out.println(sql);
        Assert.assertSame(a1, a1.as("a1"));
        Assert.assertNotSame(a1, a1.as("a2"));
        Assert.assertNotSame(a1, a2);
    }

    @Test
    public void testOracleFrom() {
        OracleDialect oracleDialect = new OracleDialect();
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT.as("a"));
        String sql = oracleDialect.buildSelectSql(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT * FROM TB_ACCOUNT A", sql);
    }


    @Test
    public void testSelectSql() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT);

        Assert.assertEquals("SELECT * FROM `tb_account`", query.toSQL());
    }

    @Test
    public void testSelectWithSchemaSql() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT01);

        Assert.assertEquals("SELECT * FROM `flex`.`tb_a01`", query.toSQL());
    }

    @Test
    public void testSelectWithSchemaSql01() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT01)
            .leftJoin(ACCOUNT).on(ACCOUNT01.ID.eq(ACCOUNT.ID))
            .where(ACCOUNT01.ID.ge(100))
            .and(ACCOUNT.SEX.eq(1));

        TableManager.setDynamicTableProcessor(tableName -> tableName + "_01");

        Assert.assertEquals("SELECT * FROM `flex`.`tb_a01_01` " +
                "LEFT JOIN `tb_account_01` ON `flex`.`tb_a01_01`.`id` = `tb_account_01`.`id` " +
                "WHERE `flex`.`tb_a01_01`.`id` >= 100 AND `tb_account_01`.`sex` = 1"
            , query.toSQL());

        System.out.println(query.toSQL());
        // 重置dynamicTableProcessor，防止影响其他的测试用例
        TableManager.setDynamicTableProcessor(null);
    }


    @Test
    public void testSelectWithSchemaSql02() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT01).as("a1").leftJoin(ACCOUNT).on(ACCOUNT01.ID.eq(ACCOUNT.ID))
            .where(ACCOUNT01.ID.ge(100))
            .and(ACCOUNT.SEX.eq(1));

        TableManager.setDynamicTableProcessor(original -> original + "_01");

        Assert.assertEquals("SELECT * FROM `flex`.`tb_a01_01` AS `a1` " +
                "LEFT JOIN `tb_account_01` ON `a1`.`id` = `tb_account_01`.`id` " +
                "WHERE `a1`.`id` >= 100 AND `tb_account_01`.`sex` = 1"
            , query.toSQL());

        System.out.println(query.toSQL());
        // 重置dynamicTableProcessor，防止影响其他的测试用例
        TableManager.setDynamicTableProcessor(null);
    }


    @Test
    public void testSelectColumnsSql() {
        QueryWrapper query = new QueryWrapper()
            .select(ACCOUNT.ID, ACCOUNT.USER_NAME)
            .from(ACCOUNT);

        Assert.assertEquals("SELECT `id`, `user_name` FROM `tb_account`"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testSelect1ColumnsSql() {
        QueryWrapper query = new QueryWrapper()
            .select(ACCOUNT.ID, ACCOUNT.USER_NAME,
                ARTICLE.ID.as("articleId"), ARTICLE.TITLE,
                max(ACCOUNT.AGE).as("ageMax"))
            .from(ACCOUNT.as("a"), ARTICLE.as("b"))
            .where(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID));

        Assert.assertEquals("SELECT `a`.`id`, `a`.`user_name`, `b`.`id` AS `articleId`, `b`.`title`, " +
                "MAX(`a`.`age`) AS `ageMax` " +
                "FROM `tb_account` AS `a`, `tb_article` AS `b` " +
                "WHERE `a`.`id` = `b`.`account_id`"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testSelectColumnsAndFunctionsSql() {
        QueryWrapper query = new QueryWrapper()
            .select(ACCOUNT.ID, ACCOUNT.USER_NAME, ACCOUNT.AGE.as("aGe"), max(ACCOUNT.BIRTHDAY).as("Max_BirthDay"), avg(ACCOUNT.SEX).as("sex_avg"))
            .from(ACCOUNT.as("tableAlias"));

        Assert.assertEquals("SELECT `id`, `user_name`, `age` AS `aGe`, " +
                "MAX(`birthday`) AS `Max_BirthDay`, " +
                "AVG(`sex`) AS `sex_avg` " +
                "FROM `tb_account` AS `tableAlias`"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testSelectColumnsAndFunctionsSqlAlias() {
        QueryWrapper query = new QueryWrapper()
            .select(ACCOUNT.ID, ACCOUNT.USER_NAME, ACCOUNT.AGE.as("aGe"), max(ACCOUNT.BIRTHDAY).as("Max_BirthDay"), avg(ACCOUNT.SEX).as("sex_avg"))
            .from(ACCOUNT.as("tableAlias"));

        Assert.assertEquals("SELECT `id`, `user_name`, `age` AS `aGe`, " +
                "MAX(`birthday`) AS `Max_BirthDay`, " +
                "AVG(`sex`) AS `sex_avg` " +
                "FROM `tb_account` AS `tableAlias`"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testDistinctColumnAlias() {
        QueryWrapper query = new QueryWrapper()
            .select(
                new DistinctQueryColumn(ACCOUNT.SEX).as("sexDis"))
            .select(ACCOUNT.USER_NAME.add(ACCOUNT.AGE).as("addAlias"))
            .select(new RawQueryColumn("abc").as("aBc"))
            .from(ACCOUNT);

        Assert.assertEquals("SELECT DISTINCT `sex` AS `sexDis`, (`user_name` + `age`) AS `addAlias`, abc AS `aBc` " +
                "FROM `tb_account`"
            , query.toSQL());

        System.out.println("sql = " + query.toSQL());

    }


    @Test
    public void testSelectAllColumnsSql() {
        QueryWrapper query = new QueryWrapper()
            .select(ACCOUNT.ALL_COLUMNS)
            .from(ACCOUNT);

        Assert.assertEquals("SELECT * FROM `tb_account`"
            , query.toSQL());

        System.out.println(query.toSQL());
    }


    @Test
    public void testUnionSql() {
        QueryWrapper query = new QueryWrapper()
            .select(ACCOUNT.ID)
            .from(ACCOUNT)
            .orderBy(ACCOUNT.ID.desc())
            .union(select(ARTICLE.ID).from(ARTICLE))
            .unionAll(select(ARTICLE.ID).from(ARTICLE));

        Assert.assertEquals("(SELECT `id` FROM `tb_account` ORDER BY `id` DESC) " +
                "UNION (SELECT `id` FROM `tb_article`) " +
                "UNION ALL (SELECT `id` FROM `tb_article`)"
            , query.toSQL());

        System.out.println(query.toSQL());
    }


    @Test
    public void testWhereSql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(100))
            .and(ACCOUNT.USER_NAME.like("michael"));

        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "WHERE `id` >= 100 AND `user_name` LIKE '%michael%'"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testWhere2Sql() {
        QueryWrapper query = QueryWrapper.create()
            .select(column("A.*"), column("b.x"))
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(100))
            .and(column("aaa").in("michael", "aaa"));

        Assert.assertEquals("SELECT A.*, b.x FROM `tb_account` " +
                "WHERE `id` >= 100 AND aaa IN ('michael', 'aaa')"
            , query.toSQL());

        System.out.println(query.toSQL());
    }


    @Test
    public void testWhereIssues181() {
        int id = 100;
        String name = null;
        QueryWrapper query = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(100))
            .and(ACCOUNT.USER_NAME.eq(name))
            .or(ACCOUNT.SEX.eq(null).and(ACCOUNT.BIRTHDAY.eq(1)))
            .or(ACCOUNT.SEX.eq(1).and(ACCOUNT.BIRTHDAY.eq(2)));

        String sql = "SELECT * FROM `tb_account` WHERE `id` >= 100 OR (`birthday` = 1) OR (`sex` = 1 AND `birthday` = 2)";
        Assert.assertEquals(sql, query.toSQL());
        System.out.println(query.toSQL());
    }


    @Test
    public void testWhereCond1Sql() {
        boolean flag = false;
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(100).when(flag))
            .and(ACCOUNT.USER_NAME.like("michael"));

        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `user_name` LIKE '%michael%'"
            , query.toSQL());

        System.out.println(query.toSQL());
    }


    @Test
    public void testWhereSql3() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(100))
            .and(ACCOUNT.USER_NAME.notLike("michael"));

        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `id` >= 100 AND `user_name` NOT LIKE '%michael%'"
            , query.toSQL());

        System.out.println(query.toSQL());
    }


    @Test
    public void testWhereCond2Sql() {
        boolean flag = false;
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(flag ? ACCOUNT.ID.ge(100) : noCondition())
            .and(ACCOUNT.USER_NAME.like("michael"));

        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `user_name` LIKE '%michael%'"
            , query.toSQL());

        System.out.println(query.toSQL());
    }


    @Test
    public void testWhereExistSql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(100))
            .and(
                exists(
                    selectOne().from(ARTICLE).as("a").where(ARTICLE.ID.ge(100))
                )
            );

        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "WHERE `id` >= 100 " +
                "AND EXISTS (SELECT 1 AS `temp_one` FROM `tb_article` AS `a` WHERE `id` >= 100)"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testSelectCountOne() {
        QueryWrapper query = selectCountOne()
            .from(ARTICLE);
        Assert.assertEquals("SELECT COUNT(1) AS `temp_count_one` FROM `tb_article`"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testWhereAndOrSql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(100))
            .and(ACCOUNT.SEX.eq(1).or(ACCOUNT.SEX.eq(2)))
            .or(ACCOUNT.AGE.in(18, 19, 20).or(ACCOUNT.USER_NAME.like("michael")));

        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "WHERE `id` >= 100 " +
                "AND (`sex` = 1 OR `sex` = 2) " +
                "OR (`age` IN (18, 19, 20) OR `user_name` LIKE '%michael%')"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testWhereSelectSql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(
                select(ARTICLE.ACCOUNT_ID).from(ARTICLE).where(ARTICLE.ID.ge(100))
            ));


        Assert.assertEquals("SELECT * FROM `tb_account`" +
                " WHERE `id` >= (SELECT `account_id` FROM `tb_article` WHERE `id` >= 100)"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testGroupSql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .groupBy(ACCOUNT.USER_NAME);

        Assert.assertEquals("SELECT * FROM `tb_account` GROUP BY `user_name`"
            , query.toSQL());

        System.out.println(query.toSQL());
    }


    //https://gitee.com/mybatis-flex/mybatis-flex/issues/I7EAY9
    @Test
    public void testGroup_I7EAY9() {
        QueryWrapper query = QueryWrapper.create()
            .from(ACCOUNT).as("a")
            .from(ACCOUNT01).as("b")
            .groupBy(year(ACCOUNT.BIRTHDAY));

        Assert.assertEquals("SELECT * FROM `tb_account` AS `a`, `flex`.`tb_a01` AS `b` " +
                "GROUP BY YEAR(`a`.`birthday`)"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    //https://gitee.com/mybatis-flex/mybatis-flex/issues/I7EAY9
    @Test
    public void testGroup184() {
        QueryWrapper query = QueryWrapper.create()
            .select(left(ACCOUNT.AGE, 6).as("regionCode"))
            .from(ACCOUNT)
            .groupBy("regionCode");

        Assert.assertEquals("SELECT LEFT(`age`, 6) AS `regionCode` FROM `tb_account` " +
                "GROUP BY regionCode"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testHavingSql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .groupBy(ACCOUNT.USER_NAME)
            .having(ACCOUNT.AGE.between(18, 25));

        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "GROUP BY `user_name` " +
                "HAVING `age` BETWEEN  18 AND 25 "
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testJoinSql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
            .where(ACCOUNT.AGE.ge(10));

        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "LEFT JOIN `tb_article` " +
                "ON `tb_account`.`id` = `tb_article`.`account_id` " +
                "WHERE `tb_account`.`age` >= 10"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testJoin2Sql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .leftJoin(ARTICLE).on(
                ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID).and(ACCOUNT.AGE.eq(18))
            )
            .where(ACCOUNT.AGE.ge(10));

        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "LEFT JOIN `tb_article` " +
                "ON `tb_account`.`id` = `tb_article`.`account_id` AND `tb_account`.`age` = 18 " +
                "WHERE `tb_account`.`age` >= 10"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testJoin3Sql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .leftJoin(
                select().from(ARTICLE).where(ARTICLE.ID.ge(100))
            ).as("a").on(
                ACCOUNT.ID.eq(raw("a.id"))
            )
            .where(ACCOUNT.AGE.ge(10));

        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "LEFT JOIN (SELECT * FROM `tb_article` WHERE `id` >= 100) AS `a` " +
                "ON `tb_account`.`id` = a.id " +
                "WHERE `tb_account`.`age` >= 10"
            , query.toSQL());

        System.out.println(query.toSQL());
    }


    @Test
    public void testJoin4Sql() {
        QueryWrapper query = QueryWrapper.create()
            .select(ACCOUNT.ALL_COLUMNS,
                column("bui.user_code"),
                column("bui.user_name"),
                column("burmc.user_name as created_by_name"))
            .from(ACCOUNT).as("burm")
            .leftJoin("base_admin_user_info").as("bui").on("bui.user_id = burm.user_id")
            .leftJoin("base_admin_user_info").as("burmc").on("burmc.user_id = burm.created_by")
            .where("bui.is_valid = ?", 3);

        Assert.assertEquals("SELECT `burm`.*, bui.user_code, bui.user_name, burmc.user_name as created_by_name " +
                "FROM `tb_account` AS `burm` " +
                "LEFT JOIN `base_admin_user_info` AS `bui` " +
                "ON  bui.user_id = burm.user_id  " +
                "LEFT JOIN `base_admin_user_info` AS `burmc` " +
                "ON  burmc.user_id = burm.created_by  " +
                "WHERE  bui.is_valid = 3 "
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testJoinSelf() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ALL_COLUMNS
//                ,column("a0.xxxx").as("xxx")
            )
            .from(ACCOUNT).as("a0")
            .leftJoin(ACCOUNT).as("a1").on(ACCOUNT.ID.eq(ACCOUNT.AGE).and(ACCOUNT.USER_NAME.like("a")))
            .where(ACCOUNT.AGE.ge(10));

        Assert.assertEquals("SELECT `a0`.* " +
            "FROM `tb_account` AS `a0` " +
            "LEFT JOIN `tb_account` AS `a1` " +
            "ON `a1`.`id` = `a0`.`age` AND `a1`.`user_name` LIKE '%a%' " +
            "WHERE `a0`.`age` >= 10", queryWrapper.toSQL());

        System.out.println(queryWrapper.toSQL());

        QueryWrapper queryWrapper1 = QueryWrapper.create()
            .select(ACCOUNT.ID)
            .from(ACCOUNT)
            .where(ACCOUNT.AGE.ge(10));

        Assert.assertEquals("SELECT `id` FROM `tb_account` WHERE `age` >= 10", queryWrapper1.toSQL());

        System.out.println(queryWrapper1.toSQL());
    }


    @Test
    public void testOrderBySql() {
        QueryWrapper query = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .orderBy(ACCOUNT.AGE.asc(), ACCOUNT.USER_NAME.desc().nullsLast());


        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "ORDER BY `age` ASC, `user_name` DESC NULLS LAST"
            , query.toSQL());

        System.out.println(query.toSQL());
    }

    @Test
    public void testDeleteSql() {
        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Account.class);
        String sql = dialect.forDeleteEntityById(tableInfo);

        Assert.assertEquals("UPDATE `tb_account` SET `is_delete` = 1 WHERE `id` = ?  AND `is_delete` = 0"
            , sql);

        System.out.println(sql);
    }


    /**
     * test https://gitee.com/mybatis-flex/mybatis-flex/issues/I8ASWS
     */
    @Test
    public void testDeleteWithJoin() {
        QueryWrapper qw = QueryWrapper.create()
            .from(ACCOUNT).leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
            .where(ACCOUNT.USER_NAME.eq("x"));
        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forDeleteByQuery(qw);
        Assert.assertEquals("DELETE `tb_account` FROM `tb_account` " +
                "LEFT JOIN `tb_article` ON `tb_account`.`id` = `tb_article`.`account_id` " +
                "WHERE `tb_account`.`user_name` = ?"
            , sql);
        System.out.println(sql);
    }

    @Test
    public void testForUpdate() {
        IDialect dialect = new CommonsDialectImpl();

        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .and(ACCOUNT.USER_NAME.like("michael"))
            .forUpdate();

        String sql = dialect.forSelectByQuery(queryWrapper);

        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `user_name` LIKE ? FOR UPDATE"
            , sql);

        System.out.println(sql);
    }

    @Test
    public void testConvert() {
        IDialect dialect = new CommonsDialectImpl();

        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ALL_COLUMNS, convert("NVARCHAR(30)", "GETDATE()", "126").as("result"))
            .from(ACCOUNT)
            .and(ACCOUNT.USER_NAME.like("michael"))
            .and(convert("NVARCHAR(30)", "GETDATE()", "126").in(
                select(ACCOUNT.ID).from(ACCOUNT).where(ACCOUNT.ID.ge(100)))
            );

        String sql = dialect.forSelectByQuery(queryWrapper);

        Assert.assertEquals("SELECT *, CONVERT(NVARCHAR(30), GETDATE(), 126) AS `result` " +
                "FROM `tb_account` " +
                "WHERE `user_name` LIKE ? " +
                "AND CONVERT(NVARCHAR(30), GETDATE(), 126) IN (SELECT `id` FROM `tb_account` WHERE `id` >= ?)"
            , sql);

        System.out.println(sql);
    }


    @Test
    public void testCase1() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ALL_COLUMNS,
                case_()
                    .when(ACCOUNT.ID.eq(100)).then(100)
                    .when(ACCOUNT.ID.ge(200)).then(200)
                    .else_(300)
                    .end().as("result"))
            .from(ACCOUNT)
            .and(ACCOUNT.USER_NAME.like("michael"));

        Assert.assertEquals("SELECT *, (CASE WHEN `id` = 100 THEN 100 WHEN `id` >= 200 THEN 200 ELSE 300 END) AS `result` " +
            "FROM `tb_account` " +
            "WHERE `user_name` LIKE '%michael%'", queryWrapper.toSQL());

        System.out.println(queryWrapper.toSQL());
    }

    @Test
    public void testCase2() {
        IDialect dialect = new CommonsDialectImpl();

        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ALL_COLUMNS,
                case_(ACCOUNT.ID)
                    .when(100).then(100)
                    .when(200).then(200)
                    .else_(300)
                    .end().as("result"))
            .from(ACCOUNT)
            .and(ACCOUNT.USER_NAME.like("michael"));

        String sql = dialect.forSelectByQuery(queryWrapper);

        Assert.assertEquals("SELECT *, (CASE `id` WHEN 100 THEN 100 WHEN 200 THEN 200 ELSE 300 END) AS `result` " +
            "FROM `tb_account` WHERE `user_name` LIKE ?", sql);

        System.out.println(sql);
    }


    @Test
    public void testCase3() {
        IDialect dialect = new CommonsDialectImpl();

        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ALL_COLUMNS,
                case_(ACCOUNT.ID)
                    .when(100).then(100)
                    .when(200).then(200)
                    .else_(convert("varchar", "GETDATE()", "126"))
                    .end().as("result"))
            .from(ACCOUNT)
            .and(ACCOUNT.USER_NAME.like("michael"));

        String sql = dialect.forSelectByQuery(queryWrapper);

        Assert.assertEquals("SELECT *, (CASE `id` WHEN 100 THEN 100 WHEN 200 THEN 200 ELSE CONVERT(varchar, GETDATE(), 126) END) AS `result` " +
            "FROM `tb_account` " +
            "WHERE `user_name` LIKE ?", sql);

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
        Assert.assertEquals("SELECT * FROM `tb_account` ORDER BY `id` DESC LIMIT 20, 10", sql1);
        System.out.println(sql1);

        IDialect dialect2 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.ORACLE);
        String sql2 = dialect2.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM (SELECT TEMP_DATAS.*, ROWNUM RN " +
            "FROM (SELECT * FROM \"tb_account\" ORDER BY \"id\" DESC) TEMP_DATAS WHERE ROWNUM <= 30) " +
            "WHERE RN > 20", sql2);
        System.out.println(sql2);

//        IDialect dialect3 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.DB2);
//        String sql3 = dialect3.buildSelectSql(queryWrapper);
//        System.out.println(sql3);

        IDialect dialect4 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.POSTGRESQL);
        String sql4 = dialect4.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM \"tb_account\" ORDER BY \"id\" DESC LIMIT 10 OFFSET 20", sql4);
        System.out.println(sql4);

        IDialect dialect5 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.INFORMIX);
        String sql5 = dialect5.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT SKIP 20 FIRST 10 * FROM \"tb_account\" ORDER BY \"id\" DESC", sql5);
        System.out.println(sql5);

        IDialect dialect6 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.SYBASE);
        String sql6 = dialect6.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT TOP 10 START AT 21 * FROM \"tb_account\" ORDER BY \"id\" DESC", sql6);
        System.out.println(sql6);


        IDialect dialect7 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcessor.FIREBIRD);
        String sql7 = dialect7.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM \"tb_account\" ORDER BY \"id\" DESC ROWS 20 TO 30", sql7);
        System.out.println(sql7);
    }


    @Test
    public void testSelectLimitSql() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(distinct(ARTICLE.ID))
            .select(ACCOUNT.ALL_COLUMNS)
            .select(ARTICLE.ID.as("article_id"))
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

        Assert.assertEquals("SELECT DISTINCT `b1`.`id`, `a1`.*, `b1`.`id` AS `article_id`, MAX(`a1`.`sex`), COUNT(DISTINCT `b1`.`id`) " +
            "FROM `tb_account` AS `a1` LEFT JOIN `tb_article` AS `b1` ON `b1`.`account_id` = `a1`.`id` " +
            "WHERE `a1`.`id` >= (SELECT `id` FROM `tb_article` AS `cc` WHERE `id` = 111) " +
            "AND `a1`.`user_name` LIKE '%michael%' " +
            "AND `b1`.`id` IN (SELECT `tb_article`.`id` FROM `aaa`) " +
            "AND NOT EXISTS (SELECT 1 AS `temp_one` FROM `aaa` WHERE `tb_article`.`id` >= 333) " +
            "GROUP BY `a1`.`id` HAVING `b1`.`id` >= 0 " +
            "ORDER BY `a1`.`id` DESC LIMIT 10, 10", queryWrapper.toSQL());

        System.out.println(queryWrapper.toSQL());

//        String oracleSql = new OracleDialect().forSelectListByQuery(CPI.getQueryTable(queryWrapper).getName(), queryWrapper);
//        System.out.println(">>>>> oracle: " + oracleSql);
//
//        String informixSql = new InformixDialect().forSelectListByQuery(CPI.getQueryTable(queryWrapper).getName(), queryWrapper);
//        System.out.println(">>>>> informix: " + informixSql);
    }


    @Test
    public void testEntityToQueryWrapper1() {
        Account account = new Account();
        account.setAge(18);
        account.setUserName("michael");

        QueryWrapper qw = QueryWrapper.create(account);

        Assert.assertEquals("SELECT `id`, `user_name`, `birthday`, `sex`, `age`, `is_normal`, `is_delete` " +
                "FROM `tb_account` " +
                "WHERE `user_name` = 'michael' " +
                "AND `sex` = 0 " +
                "AND `age` = 18 " +
                "AND `is_normal` = false"
            , qw.toSQL());

        System.out.println(qw.toSQL());
    }


    @Test
    public void testEntityToQueryWrapper2() {
        Account account = new Account();
        account.setAge(18);
        account.setUserName("michael");
        account.setBirthday(new Date());

        SqlOperators operators = SqlOperators.of()
            .set(Account::getUserName, SqlOperator.LIKE_LEFT)
            .set(Account::getBirthday, SqlOperator.IGNORE)
            .set(Account::getAge, SqlOperator.GE);

        QueryWrapper qw = QueryWrapper.create(account, operators);

        Assert.assertEquals("SELECT `id`, `user_name`, `birthday`, `sex`, `age`, `is_normal`, `is_delete` FROM `tb_account` " +
                "WHERE `user_name` LIKE 'michael%' " +
                "AND `sex` = 0 " +
                "AND `age` >= 18 " +
                "AND `is_normal` = false"
            , qw.toSQL());
        System.out.println(SqlFormatter.format(qw.toSQL()));
    }

}
