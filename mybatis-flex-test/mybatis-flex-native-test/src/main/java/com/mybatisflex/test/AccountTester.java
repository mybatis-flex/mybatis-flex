/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.query.If;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;


public class AccountTester {

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
    public void testLambda() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(Account::getId).ge(100)
                .and(Account::getUserName).like("michael")
                .or(Account::getUserName).like(null, If::notNull);
        System.out.println(queryWrapper.toSQL());
    }

    @Test
    public void testTenant() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(Account::getId).ge(1);
        List<Account> accounts = accountMapper.selectListByQuery(queryWrapper);
        System.out.println(accounts);
    }



    @Test
    public void testSelectAsToDTO() {
        List<AccountDTO> accountDTOS = accountMapper.selectListByQueryAs(QueryWrapper.create(), AccountDTO.class);
        System.out.println(accountDTOS);
    }



    @Test
    public void testUpdate() {
        List<Account> accounts = accountMapper.selectAll();
        System.out.println(accounts);


        Account account = UpdateEntity.of(Account.class,1);
        account.setUserName("lisi");

        UpdateWrapper.of(account)
            .setRaw("age","age + 1");
        accountMapper.update(account);


        accounts = accountMapper.selectAll();
        System.out.println(accounts);

    }


}
