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
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import org.junit.Assert;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

public class InsertWithPkTestStarter {

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
            .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);


        AccountMapper accountMapper = bootstrap.getMapper(AccountMapper.class);
        accountMapper.selectAll().forEach(System.out::println);

        System.out.println(">>>>>>>>>>>>>>>delete 1");
        LogicDeleteManager.execWithoutLogicDelete(() -> accountMapper.deleteById(1));
        accountMapper.selectAll().forEach(System.out::println);

        System.out.println(">>>>>>>>>>>>>>>insertWithPk");
        Account account = new Account();
        account.setId(1L);
        account.setUserName("michael1");
        int rows = accountMapper.insertWithPk(account);
        Assert.assertEquals(1, rows);
        accountMapper.selectAll().forEach(System.out::println);
    }

}
