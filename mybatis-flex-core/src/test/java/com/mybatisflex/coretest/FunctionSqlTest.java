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

import com.mybatisflex.core.query.FunctionQueryColumn;
import com.mybatisflex.core.query.QueryOrderBy;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.RawQueryColumn;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static com.mybatisflex.core.query.QueryMethods.*;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

/**
 * @author 王帅
 * @since 2023-06-28
 */
public class FunctionSqlTest {

    @Test
    public void test() {
        String sql = QueryWrapper.create()
            .select(new FunctionQueryColumn("NOW").as("n1"))
            .select(new FunctionQueryColumn("NOW", new RawQueryColumn("")).as("n2"))
            .select(new FunctionQueryColumn("CONCAT", ACCOUNT.USER_NAME, ACCOUNT.AGE).as("c1"))
            .from(ACCOUNT)
            .toSQL();
        System.out.println(sql);
        assertEquals("SELECT NOW() AS `n1`, NOW() AS `n2`, CONCAT(`user_name`, `age`) AS `c1` FROM `tb_account`", sql);
    }

    @Test
    public void test02() {
        String sql = QueryWrapper.create()
            .select(concatWs(string("abc"), ACCOUNT.USER_NAME, ACCOUNT.BIRTHDAY))
            .select(abs(number(-3)))
            .from(ACCOUNT)
            .where(not(ACCOUNT.ID.eq(1)))
            .toSQL();
        System.out.println(sql);
        assertEquals("SELECT CONCAT_WS('abc', `user_name`, `birthday`), ABS(-3) FROM `tb_account` WHERE NOT (`id` = 1)", sql);
    }

    @Test
    public void test03() {
        String sql = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(upper(ACCOUNT.USER_NAME).likeRaw(raw("UPPER('ws')")))
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account` WHERE UPPER(`user_name`) LIKE UPPER('ws')", sql);
    }

    @Test
    public void test04() {
        String sql = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(findInSet(number(100), ACCOUNT.ID).gt(0))
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account` WHERE FIND_IN_SET(100, `id`) > 0", sql);
    }

    @Test
    public void test05() {
        String sql = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .orderBy(new QueryOrderBy(rand(), ""))
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_account` ORDER BY RAND()", sql);
    }

    @Test
    public void test06() {
        String sql = QueryWrapper.create()
            .select(column("(select role_name from tb_role where id = ?)", 1))
            .select(ACCOUNT.USER_NAME)
            .from(ACCOUNT)
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT (select role_name from tb_role where id = 1), `user_name` FROM `tb_account`", sql);
    }
    @Test
    public void testReplaceString() {
        String sql = QueryWrapper.create()
            .select(ACCOUNT.USER_NAME)
            .from(ACCOUNT)
            .where(ACCOUNT.USER_NAME.eq(replace("nsg_contract.primer_name","' '","''")))
            .toSQL();

        System.out.println(sql);
        assertEquals("SELECT `user_name` FROM `tb_account` WHERE `user_name` = REPLACE(nsg_contract.primer_name, ' ', '')", sql);
    }

}
