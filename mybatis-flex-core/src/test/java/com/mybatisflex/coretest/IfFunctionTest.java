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

import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static com.mybatisflex.core.query.QueryMethods.*;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

/**
 * @author 王帅
 * @since 2023-07-07
 */
public class IfFunctionTest {

    @Test
    public void test01() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(if_(ACCOUNT.AGE.ge(6), ACCOUNT.IS_NORMAL, ACCOUNT.IS_DELETE).as("type"))
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq(1));
        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT IF(`age` >= 6, `is_normal`, `is_delete`) AS `type` FROM `tb_account` WHERE `id` = 1", queryWrapper.toSQL());
    }

    @Test
    public void test02() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(if_(ACCOUNT.AGE.ge(18), string("成年人"),
                if_(ACCOUNT.AGE.le(8), string("未上学"), string("已上学"))).as("type"))
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq(1));
        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT IF(`age` >= 18, '成年人', IF(`age` <= 8, '未上学', '已上学')) AS `type` FROM `tb_account` WHERE `id` = 1", queryWrapper.toSQL());
    }

    @Test
    public void test03() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ifNull(ACCOUNT.ID, number(0)))
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq(1));
        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT IFNULL(`id`, 0) FROM `tb_account` WHERE `id` = 1", queryWrapper.toSQL());
    }

    @Test
    public void test04() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ifNull(null_(), number(0)))
            .from(ACCOUNT);
        System.out.println(queryWrapper.toSQL());
        assertEquals("SELECT IFNULL(NULL, 0) FROM `tb_account`", queryWrapper.toSQL());
    }

}
