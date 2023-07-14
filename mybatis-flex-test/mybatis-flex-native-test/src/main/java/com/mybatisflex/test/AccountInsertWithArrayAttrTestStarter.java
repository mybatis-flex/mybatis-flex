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
import com.mybatisflex.mapper.Account05Mapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

public class AccountInsertWithArrayAttrTestStarter {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema05.sql")
            .addScript("data05.sql")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(Account05Mapper.class)
            .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);
//
//        String insertSql = "INSERT INTO `tb_account`(`id`,`user_name`, `age`, `birthday`, `options`, `is_delete`, `data_scope`) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        Db.insertBySql(insertSql,null,"lisi",null,null,null,null,null);


        Account05Mapper accountMapper = bootstrap.getMapper(Account05Mapper.class);

        Account05 account = new Account05();
        account.setId(3L);
        account.setUserName("lisi");
        account.setDataScope(new Long[]{1L, 2L});

        accountMapper.insertWithPk(account, false);

    }

}
