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

package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.table.ArticleTableDef.ARTICLE;

public class UpdateChainTest {

    static AccountMapper accountMapper;

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .setLogImpl(StdOutImpl.class)
            .addMapper(AccountMapper.class)
            .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);

        accountMapper = bootstrap.getMapper(AccountMapper.class);

    }

    @Test
    public void testUpdateChain() {
        UpdateChain.of(Account.class)
            .set(Account::getUserName, "张三")
            .setRaw(Account::getAge, "age + 1")
            .where(Account::getId).eq(1)
            .update();

        Account account = accountMapper.selectOneById(1);
        System.out.println(account);
    }

    @Test
    public void testUpdateChain1() {
        UpdateChain.of(Account.class)
            .set(Account::getAge, ACCOUNT.AGE.add(1))
            .where(Account::getId).ge(100)
            .and(Account::getAge).eq(18)
            .update();

        QueryChain.of(accountMapper)
            .list()
            .forEach(System.out::println);
    }

    @Test
    public void testUpdateChainToSql() {
        String sql = UpdateChain.of(Account.class)
            .set(ACCOUNT.AGE, 18)
            .set(Article::getAccountId, 4)
            .leftJoin(ARTICLE).as("ar").on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
            .where(ACCOUNT.ID.eq(4))
            .toSQL();

        System.out.println(sql);
    }

}
