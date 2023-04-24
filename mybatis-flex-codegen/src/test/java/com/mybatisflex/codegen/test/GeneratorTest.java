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
package com.mybatisflex.codegen.test;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TableConfig;
import com.zaxxer.hikari.HikariDataSource;

public class GeneratorTest {


//    @Test
    public void testGenerator() {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/jbootadmin?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

//        JdbcTypeMapping.registerMapping(BigInteger.class, Long.class);
//        JdbcTypeMapping.registerMapping(Integer.class, Long.class);

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/src/test/java");
//        globalConfig.setTablePrefix("tb_");
//        globalConfig.setEntityWithLombok(true);

        globalConfig.setEntitySupperClass(BaseEntity.class);

        //设置只生成哪些表
        globalConfig.addGenerateTable("account", "account_session");

        //设置 entity 的包名
        globalConfig.setEntityPackage("com.test.entity");
        globalConfig.setEntityClassPrefix("My");
        globalConfig.setEntityClassSuffix("Entity");

        //是否生成 mapper 类，默认为 false
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setMapperClassPrefix("Flex");
        globalConfig.setMapperClassSuffix("Dao");

        //设置 mapper 类的包名
        globalConfig.setMapperPackage("com.test.mapper");
        globalConfig.setMapperSupperClass(MyBaseMapper.class);


        TableConfig tableConfig = new TableConfig();
        tableConfig.setTableName("account");
        tableConfig.setUpdateListenerClass(MyUpdateListener.class);
        globalConfig.addTableConfig(tableConfig);


        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("tenant_id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.addColumnConfig("account", columnConfig);


        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //开始生成代码
        generator.generate();
    }
}
