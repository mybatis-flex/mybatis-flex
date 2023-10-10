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
import com.mybatisflex.core.util.MapperUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.coretest.table.ArticleTableDef.ARTICLE;

/**
 * @author 王帅
 * @since 2023-08-13
 */
public class CountSqlTest {

    @Test
    public void test01() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .leftJoin(ARTICLE).as("a1").on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .leftJoin(ARTICLE).as("a2").on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .where(ARTICLE.ACCOUNT_ID.in(1, 2, 3));

        System.out.println(queryWrapper.toSQL());

        QueryWrapper optimized = MapperUtil.optimizeCountQueryWrapper(queryWrapper);

        System.out.println(optimized.toSQL());

        assertEquals("SELECT COUNT(*) AS `total` " +
            "FROM `tb_account` " +
            "LEFT JOIN `tb_article` AS `a1` ON `a1`.`account_id` = `tb_account`.`id` " +
            "LEFT JOIN `tb_article` AS `a2` ON `a2`.`account_id` = `tb_account`.`id` " +
            "WHERE `a1`.`account_id` IN (1, 2, 3)", optimized.toSQL());
    }

    @Test
    public void test02() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .leftJoin(ARTICLE).as("a1").on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .leftJoin(ARTICLE).as("a2").on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .where("a1.account_id IN (1, 2, 3)");

        System.out.println(queryWrapper.toSQL());

        QueryWrapper optimized = MapperUtil.optimizeCountQueryWrapper(queryWrapper);

        System.out.println(optimized.toSQL());

        assertEquals("SELECT COUNT(*) AS `total` " +
            "FROM `tb_account` " +
            "LEFT JOIN `tb_article` AS `a1` ON `a1`.`account_id` = `tb_account`.`id` " +
            "LEFT JOIN `tb_article` AS `a2` ON `a2`.`account_id` = `tb_account`.`id` " +
            "WHERE  a1.account_id IN (1, 2, 3) ", optimized.toSQL());
    }

    @Test
    public void test03() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT.as("a"))
            .leftJoin("tb_article").as("a1").on("a1.account_id = a.id")
            .leftJoin("tb_article").as("a2").on("a1.account_id = a.id")
            .where("a1.account_id IN (1, 2, 3)");

        System.out.println(queryWrapper.toSQL());

        QueryWrapper optimized = MapperUtil.optimizeCountQueryWrapper(queryWrapper);

        System.out.println(optimized.toSQL());

        assertEquals("SELECT COUNT(*) AS `total` " +
            "FROM `tb_account` AS `a` " +
            "LEFT JOIN `tb_article` AS `a1` ON  a1.account_id = a.id  " +
            "LEFT JOIN `tb_article` AS `a2` ON  a1.account_id = a.id  " +
            "WHERE  a1.account_id IN (1, 2, 3) ", optimized.toSQL());
    }

}
