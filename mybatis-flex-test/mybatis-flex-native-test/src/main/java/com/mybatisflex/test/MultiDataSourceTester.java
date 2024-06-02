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
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

public class MultiDataSourceTester {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("db1")
            .addScript("schema.sql")
            .addScript("data.sql").setScriptEncoding("UTF-8")
            .build();

        HikariDataSource dataSource2 = new HikariDataSource();
        dataSource2.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flex_test?characterEncoding=utf-8");
        dataSource2.setUsername("root");
        dataSource2.setPassword("123456");

        MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(AccountMapper.class)
            .addDataSource("ds2", dataSource2)
            .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);

        Db.tx(() -> {
            Db.selectAll("tb_account");
            DataSourceKey.use("ds2");
            Db.selectAll("tb_account");
            return true;
        });


        //默认查询 db1
        System.out.println("\n------ds1");
        List<Row> rows1 = Db.selectAll(null, "tb_account");
        RowUtil.printPretty(rows1);


//        System.out.println("\n------ds2");
//        List<Row> rows =  DataSourceKey.use("ds2"
//                , () -> Db.selectAll("tb_account"));
//        RowUtil.printPretty(rows);


//        //查询数据源 ds2
        System.out.println("\n------ds2");
        DataSourceKey.use("ds2");
        List<Row> rows = Db.selectAll(null, "tb_account");
        RowUtil.printPretty(rows);
//
//        boolean success = Db.tx(() -> {
//            Db.updateById("tb_account",Row.ofKey("id",1)
//                    .set("user_name","测试的user"));
//            return false;
//        });
//        System.out.println("tx: " + success);
//        DataSourceKey.clear();

//        AccountMapper mapper = MybatisFlexBootstrap.getInstance().getMapper(AccountMapper.class);
//        List<Account> accounts = mapper.selectAll();
//        System.out.println(accounts);

    }

}
