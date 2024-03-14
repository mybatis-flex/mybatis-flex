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

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.CollectionUtil;
import org.junit.Test;

import static com.mybatisflex.coretest.table.ArticleTableDef.ARTICLE;
import static org.junit.Assert.assertEquals;


public class ArticleSqlTester {


    @Test
    public void testSelectSql() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ARTICLE);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectByQuery(query);
        System.out.println(sql);
        assertEquals("SELECT * FROM `tb_article`", sql);
    }

    @Test
    public void testInsertSql() {
        Article article = new Article();
        article.setAccountId(1L);
        article.setContent("aaa");

        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Article.class);
        String sql = dialect.forInsertEntity(tableInfo, article, false);
        System.out.println(sql);
        assertEquals("INSERT INTO `tb_article`(`uuid`, `account_id`, `title`, `content`, `created`, `modified`, `is_delete`, `version`) " +
            "VALUES (?, ?, ?, ?, now(), now(), ?, ?)", sql);
    }


    @Test
    public void testInsert1Sql() {
        Article article = new Article();
        article.setAccountId(1L);
        article.setContent("aaa");

        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Article.class);
        String sql = dialect.forInsertEntity(tableInfo, article, true);
        System.out.println(sql);
        assertEquals("INSERT INTO `tb_article`(`account_id`, `content`, `created`, `modified`) VALUES (?, ?, now(), now())", sql);
    }


    @Test
    public void testInsertBatchSql() {
        Article article1 = new Article();
        article1.setAccountId(1L);
        article1.setContent("aaa");

        Article article2 = new Article();
        article2.setAccountId(2L);
        article2.setContent("bbb");

        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Article.class);
        String sql = dialect.forInsertEntityBatch(tableInfo, CollectionUtil.newArrayList(article1, article2));
        System.out.println(sql);
        assertEquals("INSERT INTO `tb_article`(`uuid`, `account_id`, `title`, `content`, `created`, `modified`, `is_delete`, `version`) " +
            "VALUES (?, ?, ?, ?, now(), now(), ?, ?), (?, ?, ?, ?, now(), now(), ?, ?)", sql);
    }


    @Test
    public void testDeleteSql() {
        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Article.class);
        String sql = dialect.forDeleteEntityById(tableInfo);
        System.out.println(sql);
        assertEquals("UPDATE `tb_article` SET `is_delete` = 1 WHERE `id` = ?  AND `uuid` = ?  AND `is_delete` = 0", sql);
    }


    @Test
    public void testDeleteByIdsSql() {
        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Article.class);
        String sql = dialect.forDeleteEntityBatchByIds(tableInfo, new Object[]{1, 2, 3});
        System.out.println(sql);
        assertEquals("UPDATE `tb_article` SET `is_delete` = 1 WHERE ((`id` = ?  AND `uuid` = ? )) AND `is_delete` = 0", sql);
    }


    @Test
    public void testUpdateSql() {
        Article article = new Article();
        article.setAccountId(1L);
        article.setContent("aaa");
        article.setVersion(1L);

        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Article.class);
        String sql = dialect.forUpdateEntity(tableInfo, article, true);
        System.out.println(sql);
        assertEquals("UPDATE `tb_article` " +
            "SET `account_id` = ? , `content` = ? , `modified` = now(), `version` = `version` + 1  " +
            "WHERE `id` = ?  AND `uuid` = ?  AND `is_delete` = 0 AND `version` = 1", sql);
    }


    @Test
    public void testUpdateByQuerySql() {
        Article article = new Article();
        article.setAccountId(1L);
        article.setContent("aaa");
        article.setVersion(1L);

        QueryWrapper queryWrapper = new QueryWrapper()
            .where(ARTICLE.ID.ge(100));

        IDialect dialect = new CommonsDialectImpl();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Article.class);
        String sql = dialect.forUpdateEntityByQuery(tableInfo, article, true, queryWrapper);
        System.out.println(sql);
        assertEquals("UPDATE `tb_article` SET `account_id` = ? , `content` = ? , `modified` = now(), `version` = `version` + 1  WHERE `id` >= ?", sql);
    }

}
