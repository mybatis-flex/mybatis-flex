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

package com.mybatisflex.test.service;

import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.mybatisflex.core.query.QueryMethods.case_;
import static com.mybatisflex.core.query.QueryMethods.length;
import static com.mybatisflex.test.model.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.model.table.ArticleTableDef.ARTICLE;

/**
 * @author 王帅
 * @since 2023-07-22
 */
@SpringBootTest
class ArticleServiceTest {

    @Autowired
    ArticleService articleService;

    @Test
    void testChain() {
        Assertions.assertDoesNotThrow(() ->
            articleService.queryChain()
                .select(ARTICLE.DEFAULT_COLUMNS)
                .from(ARTICLE)
                .where(ARTICLE.ID.ge(100))
                .objList()
                .forEach(System.out::println)
        );
    }

    @Test
    void testExists() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ARTICLE.DEFAULT_COLUMNS)
            .from(ARTICLE)
            .where(ARTICLE.ACCOUNT_ID.eq(1))
            .orderBy(ARTICLE.ACCOUNT_ID.desc());
        boolean exists = articleService.exists(queryWrapper);
        Assertions.assertTrue(exists);
    }

    @Test
    @Transactional
    void testSubUpdate() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.AGE)
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq(1));

        boolean updated = articleService.updateChain()
            .set(ARTICLE.CONTENT, "hhhh")
            .set(ARTICLE.ACCOUNT_ID, queryWrapper)
            .set(ARTICLE.IS_DELETE, ARTICLE.IS_DELETE.eq(0))
            .set(ARTICLE.TITLE, case_(length(ARTICLE.TITLE)).when(1).then("title1").else_("大标题").end())
            .update();

        Assertions.assertTrue(updated);
    }

}
