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

package com.mybatisflex.codegen.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.IDialect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class SqliteGeneratorTest {

    //        @Test
    public void testGenerator3() {

        //配置数据源
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl("jdbc:sqlite:sample.db");
//        //dataSource.setUsername("root");
//        //dataSource.setPassword("123456");

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:sqlite:sample.db");
        //dataSource.setUsername("root");
        //dataSource.setPassword("123456");

        createTestTable(dataSource);


        GlobalConfig globalConfig = new GlobalConfig();

        //配置生成文件目录与根包
        globalConfig.getPackageConfig()
            .setSourceDir(System.getProperty("user.dir") + "/src/test/java")
            .setBasePackage("com.test");

        //设置只生成哪些表
        globalConfig.getStrategyConfig()
            .setGenerateTable("person");

        globalConfig.enableEntity()
            .setWithLombok(true);

        //设置生成 mapper 类
        globalConfig.enableMapper();


        Generator generator = new Generator(dataSource, globalConfig, IDialect.SQLITE);

        //开始生成代码
        generator.generate();
    }

    private void createTestTable(DataSource dataSource) {

        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id INTEGER  PRIMARY KEY AUTOINCREMENT, name STRING, mobile varchar(20))");

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
