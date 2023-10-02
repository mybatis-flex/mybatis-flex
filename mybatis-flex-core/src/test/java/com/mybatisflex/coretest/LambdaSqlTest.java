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
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Test;

import static com.mybatisflex.core.query.QueryMethods.allColumns;
import static com.mybatisflex.core.query.QueryMethods.defaultColumns;

/**
 * Lambda 构建 SQL 测试。
 *
 * @author 王帅
 * @since 2023-10-01
 */
public class LambdaSqlTest {

    public static void printSQL(QueryWrapper queryWrapper) {
        System.out.println(SqlFormatter.format(queryWrapper.toSQL()));
    }

    @Test
    public void test01() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(Account.class)
            .join(Article.class).on(wrapper -> wrapper.where(Article::getAccountId).eq(Account::getId))
            .where(Account::getAge).ge(18);

        printSQL(queryWrapper);
    }

    @Test
    public void test02() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(allColumns(Account.class, Article.class))
            .from(Account.class)
            .join(Article.class).on(wrapper -> wrapper.where(Article::getAccountId).eq(Account::getId))
            .where(Account::getAge).ge(18);

        printSQL(queryWrapper);
    }

    @Test
    public void test03() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(allColumns(Account.class, Article.class))
            .from(Account.class).as("ac")
            .join(Article.class).as("ar").on(wrapper -> wrapper.where(Article::getAccountId).eq(Account::getId))
            .where(Account::getAge).ge(18);

        printSQL(queryWrapper);
    }

    @Test
    public void test04() {
        @SuppressWarnings("unchecked")
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(defaultColumns(Account.class))
            .select(Article::getTitle, Article::getContent)
            .from(Account.class).as("ac")
            .join(Article.class).as("ar").on(wrapper -> wrapper.where(Article::getAccountId).eq(Account::getId))
            .where(Account::getAge).ge(18);

        printSQL(queryWrapper);
    }

    @Test
    public void test05() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(defaultColumns(Account.class))
            .select(allColumns(Article.class))
            .from(Account.class).as("ac")
            .join(Article.class).as("ar").on(wrapper -> wrapper.where(Article::getAccountId).eq(Account::getId))
            .where(Account::getAge).ge(18);

        printSQL(queryWrapper);
    }

}
