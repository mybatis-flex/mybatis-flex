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
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.mapper.ArticleMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

import static com.mybatisflex.core.query.QueryMethods.raw;
import static com.mybatisflex.core.query.QueryMethods.select;
import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.table.ArticleTableDef.ARTICLE;

/**
 * test https://gitee.com/mybatis-flex/mybatis-flex/issues/I7EV67
 */
public class JoinWithDeleteColumnTestStarter {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(AccountMapper.class)
            .addMapper(MyAccountMapper.class)
            .addMapper(ArticleMapper.class)
            .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);


        AccountMapper accountMapper = bootstrap.getMapper(AccountMapper.class);

        QueryWrapper query1 = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .leftJoin(ARTICLE).as("a").on(
                ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID)
            )
            .where(ACCOUNT.AGE.ge(10));

        List<AccountDTO> accountDTOS1 = accountMapper.selectListByQueryAs(query1, AccountDTO.class);
        System.out.println(accountDTOS1);

        System.out.println(">>>>>>>>>");

        QueryWrapper query2 = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .leftJoin(
                select().from(ARTICLE).where(ARTICLE.ID.ge(100))
            ).as("a").on(
                ACCOUNT.ID.eq(raw("a.id"))
            )
            .where(ACCOUNT.AGE.ge(10));

        List<AccountDTO> accountDTOS2 = accountMapper.selectListByQueryAs(query2, AccountDTO.class);
        System.out.println(accountDTOS2);

    }

}
