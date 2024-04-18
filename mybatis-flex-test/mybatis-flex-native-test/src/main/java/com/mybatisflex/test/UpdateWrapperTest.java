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
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;


public class UpdateWrapperTest {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql").setScriptEncoding("UTF-8")
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


        AccountMapper accountMapper = bootstrap.getMapper(AccountMapper.class);

        List<Account> accounts1 = accountMapper.selectAll();
        System.out.println(accounts1);


        System.out.println("//////////account2");

        Account account = UpdateEntity.of(Account.class, 1);
        UpdateWrapper wrapper = (UpdateWrapper) account;
        wrapper.setRaw("age", "age + 1");
        accountMapper.update(account);

        List<Account> accounts2 = accountMapper.selectAll();
        System.out.println(accounts2);


        System.out.println("//////////account3");

        Account account83 = UpdateEntity.of(Account.class, 1);
        UpdateWrapper<Account> wrapper3 = (UpdateWrapper) account83;
        wrapper3.setRaw(Account::getAge, "age + 1");
        accountMapper.update(account83);

        List<Account> accounts3 = accountMapper.selectAll();
        System.out.println(accounts3);


        System.out.println("//////////account4");

        Account account84 = UpdateEntity.of(Account.class, 1);
        UpdateWrapper wrapper4 = (UpdateWrapper) account84;
        wrapper4.setRaw(ACCOUNT.AGE, ACCOUNT.AGE.add(1));
        accountMapper.update(account84);

        List<Account> accounts4 = accountMapper.selectAll();
        System.out.println(accounts4);
    }

}
