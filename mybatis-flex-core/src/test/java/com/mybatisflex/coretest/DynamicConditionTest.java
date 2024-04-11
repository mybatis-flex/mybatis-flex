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
import com.mybatisflex.core.constant.SqlConnector;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.If;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.mybatisflex.core.query.QueryColumnBehavior.CONVERT_EQUALS_TO_IS_NULL;
import static com.mybatisflex.core.query.QueryColumnBehavior.getConditionCaster;
import static com.mybatisflex.core.query.QueryMethods.bracket;
import static com.mybatisflex.core.query.QueryMethods.case_;
import static com.mybatisflex.core.query.QueryMethods.max;
import static com.mybatisflex.core.query.QueryMethods.raw;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * 动态条件测试。
 *
 * @author 王帅
 * @since 2023-08-10
 */
public class DynamicConditionTest {

    @Test
    public void test01() {
        String sql = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.AGE.ge(18))
            .or(qw -> qw.where(ACCOUNT.ID.eq(1)), false)
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account` WHERE `age` >= 18", sql);
    }

    @Test
    public void test02() {
        List<Integer> idList = Arrays.asList(1, 2, 3);

        String sql = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.in(idList).when(false))
            .where(ACCOUNT.ID.in(idList, If::isNotEmpty))
            .where(ACCOUNT.ID.in(idList).when(idList::isEmpty))
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account` WHERE `id` IN (1, 2, 3)", sql);
    }

    @Test
    public void test03() {
        String sql = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq("1", StringUtil::isNumeric))
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account` WHERE `id` = '1'", sql);
    }

    @Test
    public void test04() {
        String sql = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.between('1', '2', (start, end) -> start < end))
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account` WHERE `id` BETWEEN  '1' AND '2' ", sql);
    }

    @Test
    public void test05() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.in(1, 2, 3));

        boolean anyMatch = CPI.getQueryTables(queryWrapper)
            .stream()
            .map(QueryTable::getName)
            .anyMatch(tableName -> tableName.equals(ACCOUNT.getName()));

        if (anyMatch) {
            CPI.addWhereQueryCondition(queryWrapper, ACCOUNT.AGE.ge(18), SqlConnector.AND);
        }

        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT * FROM `tb_account` WHERE `id` IN (1, 2, 3) AND `age` >= 18", queryWrapper.toSQL());
    }

    @Test
    public void test06() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.in(1, 2, 3))
            .and(ACCOUNT.AGE.ge(18))
            .or(ACCOUNT.USER_NAME.eq("zhang san"));

        for (QueryCondition condition = CPI.getWhereQueryCondition(queryWrapper); condition != null; condition = CPI.getNextCondition(condition)) {
            if (condition.getColumn().getName().equals(ACCOUNT.AGE.getName())) {
                condition.when(false);
            }
        }

        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT * FROM `tb_account` WHERE `id` IN (1, 2, 3) OR `user_name` = 'zhang san'", queryWrapper.toSQL());
    }

    @Test
    public void test07() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.in(1, 2, 3)
                .and(ACCOUNT.AGE.ge(18))
                .or(ACCOUNT.USER_NAME.eq("zhang san")));

        for (QueryCondition condition = CPI.getWhereQueryCondition(queryWrapper); condition != null; condition = CPI.getNextCondition(condition)) {
            if (condition.getColumn().getName().equals(ACCOUNT.AGE.getName())) {
                condition.when(false);
            }
        }

        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT * FROM `tb_account` WHERE `id` IN (1, 2, 3) OR `user_name` = 'zhang san'", queryWrapper.toSQL());
    }

    @Test
    public void test08() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq(1)
                .and(ACCOUNT.AGE.in(17, 18, 19).or(ACCOUNT.USER_NAME.eq("zhang san"))
                ));

        QueryCondition condition = CPI.getWhereQueryCondition(queryWrapper);
        while (condition != null) {
            System.out.println(condition.getColumn().getName());
            condition = CPI.getNextCondition(condition);
        }

        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT * FROM `tb_account` WHERE `id` = 1 AND (`age` IN (17, 18, 19) OR `user_name` = 'zhang san')", queryWrapper.toSQL());
    }

    @Test
    public void test09() {
        QueryColumnBehavior.setIgnoreFunction(e -> e == null || "".equals(e));
        QueryColumnBehavior.setSmartConvertInToEquals(false);

        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.USER_NAME.in(""));

        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT * FROM `tb_account`", queryWrapper.toSQL());
        // 重置QueryColumnBehavior
        QueryColumnBehavior.setIgnoreFunction(Objects::isNull);
    }

    @Test
    public void test10() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            // 满足忽略规则，但动态条件又是true
            .where(ACCOUNT.USER_NAME.eq(null).when(true));
        String sql = queryWrapper.toSQL();
        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account`", sql);
    }

    @Test
    public void test11() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.IS_DELETE.eq(0))
            .and(ACCOUNT.ID.ge("1").and(bracket(ACCOUNT.AGE.ge(18).or(ACCOUNT.USER_NAME.ge("zs")))))
            .or(ACCOUNT.BIRTHDAY.le("2023-10-28 22:13:36"));

        String printSql = queryWrapper.toSQL();

        assertEquals("SELECT * FROM `tb_account` " +
            "WHERE `is_delete` = 0 " +
            "AND (`id` >= '1' AND (`age` >= 18 OR `user_name` >= 'zs')) " +
            "OR `birthday` <= '2023-10-28 22:13:36'", printSql);

        System.out.println(printSql);


        QueryWrapper queryWrapper2 = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.IS_DELETE.eq(0))
            .and(ACCOUNT.ID.ge("1").and(ACCOUNT.AGE.ge(18).or(ACCOUNT.USER_NAME.ge("zs"))))
            .or(ACCOUNT.BIRTHDAY.le("2023-10-28 22:13:36"));
        System.out.println(queryWrapper2.toSQL());

        assertEquals(printSql, queryWrapper2.toSQL());
    }


    @Test
    public void test12() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select().from(ACCOUNT)
            .where(ACCOUNT.IS_DELETE.eq(0))
            .or(raw("1 = 1").or(ACCOUNT.ID.eq(123)))
            .and(ACCOUNT.AGE.ge(1));
        String sql = queryWrapper.toSQL();
        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account` WHERE `is_delete` = 0 OR ( 1 = 1  OR `id` = 123) AND `age` >= 1", sql);
    }


    private void assertConditionEquals(QueryCondition expect, QueryCondition actual) {
        Assert.assertEquals(expect.getColumn(), actual.getColumn());
        Assert.assertEquals(expect.getLogic(), actual.getLogic());
        Assert.assertEquals(expect.getValue(), actual.getValue());
    }

    @Test
    public void testCastFunction1() {
        QueryCondition condition = QueryCondition.create(new QueryColumn("id"), SqlOperator.IN, new Object[]{null});
        Assert.assertSame(condition, getConditionCaster().apply(condition));
    }

    @Test
    public void testCastFunction2() {
        QueryColumn column = new QueryColumn("id");
        QueryColumnBehavior.setConditionCaster(CONVERT_EQUALS_TO_IS_NULL);

        QueryCondition condition = QueryCondition.create(column, SqlOperator.EQUALS, null);
        QueryCondition expect = column.isNull();
        QueryCondition actual = getConditionCaster().apply(condition);

        assertConditionEquals(expect, actual);
    }

    @Test
    public void testCastFunction3() {
        QueryColumn column = new QueryColumn("id");
        QueryColumnBehavior.setConditionCaster(CONVERT_EQUALS_TO_IS_NULL);
        QueryColumnBehavior.setSmartConvertInToEquals(true);

        QueryCondition condition = QueryCondition.create(column, SqlOperator.EQUALS, null);
        QueryCondition expect = column.isNull();
        QueryCondition actual = getConditionCaster().apply(condition);

        assertConditionEquals(expect, actual);
    }

    @Test
    public void testCastFunction4() {
        QueryColumn column = new QueryColumn("id");
        QueryColumnBehavior.setConditionCaster(CONVERT_EQUALS_TO_IS_NULL);
        QueryColumnBehavior.setSmartConvertInToEquals(true);

        QueryCondition condition = QueryCondition.create(column, SqlOperator.IN, new Object[]{1});
        QueryCondition expect = QueryCondition.create(column, SqlOperator.EQUALS, 1);
        QueryCondition actual = getConditionCaster().apply(condition);

        assertConditionEquals(expect, actual);
    }

    @Test
    public void testCastFunction5() {
        QueryColumn column = new QueryColumn("id");
        QueryColumnBehavior.setConditionCaster(CONVERT_EQUALS_TO_IS_NULL);
        QueryColumnBehavior.setSmartConvertInToEquals(true);

        QueryCondition condition = QueryCondition.create(column, SqlOperator.IN, new Object[]{null});
        QueryCondition expect = column.isNull();
        QueryCondition actual = getConditionCaster().apply(condition);

        assertConditionEquals(expect, actual);
    }

    @Test
    public void testCastFunction6() {
        QueryColumn column = new QueryColumn("id");
        QueryColumnBehavior.setConditionCaster(CONVERT_EQUALS_TO_IS_NULL);
        QueryColumnBehavior.setSmartConvertInToEquals(true);

        QueryCondition condition = QueryCondition.create(column, SqlOperator.IN, Collections.singletonList(null));
        QueryCondition expect = column.isNull();
        QueryCondition actual = getConditionCaster().apply(condition);

        assertConditionEquals(expect, actual);
    }

    @Test
    public void testHasCondition() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        assertFalse(queryWrapper.hasCondition());

        queryWrapper = QueryWrapper.create()
            .where(ACCOUNT.ID.eq(1));
        assertTrue(queryWrapper.hasCondition());

        queryWrapper = QueryWrapper.create()
            .where(ACCOUNT.ID.eq(1).and(ACCOUNT.AGE.eq(18)));
        assertTrue(queryWrapper.hasCondition());

        queryWrapper = QueryWrapper.create()
            .where(ACCOUNT.ID.eq(1, false).and(ACCOUNT.AGE.eq(18)));
        assertTrue(queryWrapper.hasCondition());

        queryWrapper = QueryWrapper.create()
            .where(ACCOUNT.ID.eq(1, false).and(ACCOUNT.AGE.eq(18, false)));
        assertFalse(queryWrapper.hasCondition());

        queryWrapper = QueryWrapper.create()
            .where(ACCOUNT.ID.eq(1, false));
        assertFalse(queryWrapper.hasCondition());

        queryWrapper = QueryWrapper.create()
            .where(ACCOUNT.ID.eq(1, false))
            .and(ACCOUNT.AGE.eq(18, false));
        assertFalse(queryWrapper.hasCondition());

        queryWrapper = QueryWrapper.create()
            .where(ACCOUNT.ID.eq(1, false))
            .and(ACCOUNT.AGE.eq(18))
            .or(ACCOUNT.IS_DELETE.eq(0, false));
        assertTrue(queryWrapper.hasCondition());
    }

    @Test
    public void testNull() {
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NONE);
        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq(null))
            .and(ACCOUNT.USER_NAME.eq("QAQ", false))
            .and(ACCOUNT.AGE.ge(null).or(ACCOUNT.BIRTHDAY.ne(new Date())))
            .and(QueryCondition.createEmpty());
        String sql1 = queryWrapper.toSQL();
        System.out.println(sql1);

        assertThrows(Exception.class, () -> QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.AGE.in((Object[]) null)));

        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NULL);
        queryWrapper = QueryWrapper.create()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq(null))
            .and(ACCOUNT.USER_NAME.eq("QAQ", false))
            .and(ACCOUNT.AGE.ne(null));
        String sql2 = queryWrapper.toSQL();
        System.out.println(sql2);

        assertNotEquals(sql1, sql2);
    }

    @Test
    public void testHaving() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(max(ACCOUNT.ID))
            .from(ACCOUNT)
            .groupBy(ACCOUNT.BIRTHDAY)
            .having(case_()
                .when(ACCOUNT.AGE.ge(18)).then(1)
                .else_(2).end().eq(3));

        String sql = SqlFormatter.format(queryWrapper.toSQL());
        System.out.println(sql);

        assertEquals("SELECT\n" +
            "  MAX(` id `)\n" +
            "FROM\n" +
            "  ` tb_account `\n" +
            "GROUP BY\n" +
            "  ` birthday `\n" +
            "HAVING\n" +
            "  (\n" +
            "    CASE\n" +
            "      WHEN ` age ` >= 18 THEN 1\n" +
            "      ELSE 2\n" +
            "    END\n" +
            "  ) = 3", sql);
    }

}
