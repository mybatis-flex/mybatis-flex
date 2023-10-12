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

import com.mybatisflex.core.constant.SqlConnector;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.util.StringUtil;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

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
            .anyMatch(tableName -> tableName.equals(ACCOUNT.getTableName()));

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
            .where(ACCOUNT.USER_NAME.in( ""));

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

}
