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
import org.junit.Assert;
import org.junit.Test;

import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.coretest.table.ArticleTableDef.ARTICLE;

/**
 * @author 王帅
 * @since 2023-09-01
 */
public class DynamicOrderByTest {

    @Test
    public void test01() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ID, ACCOUNT.USER_NAME.as("name"), ACCOUNT.AGE)
            .from(ACCOUNT.as("ac"))
            .leftJoin(ARTICLE).as("ar").on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .orderBy(ACCOUNT.USER_NAME, true)
            .orderBy(Account::getAge, false)
            .orderBy("name", true)
            .orderBy(ACCOUNT.BIRTHDAY, null);

        System.out.println(queryWrapper.toSQL());
        Assert.assertEquals("SELECT `ac`.`id`, `ac`.`user_name` AS `name`, `ac`.`age` " +
            "FROM `tb_account` AS `ac` " +
            "LEFT JOIN `tb_article` AS `ar` ON `ar`.`account_id` = `ac`.`id` " +
            "ORDER BY `ac`.`user_name` ASC, `ac`.`age` DESC, name ASC", queryWrapper.toSQL());
    }

}
