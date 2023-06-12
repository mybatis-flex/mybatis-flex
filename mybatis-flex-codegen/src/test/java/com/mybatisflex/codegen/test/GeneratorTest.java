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

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.spring.service.impl.CacheableServiceImpl;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;

import java.util.function.UnaryOperator;

public class GeneratorTest {


    //@Test
    public void testCodeGen1() {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("12345678");

        GlobalConfig globalConfig = new GlobalConfig();

        //用户信息表，用于存放用户信息。 -> 用户信息
        UnaryOperator<String> tableFormat = (e) -> e.split("，")[0].replace("表", "");

        //设置注解生成配置
        globalConfig.setAuthor("Michael Yang");
        globalConfig.setTableCommentFormat(tableFormat);

        //设置生成文件目录和根包
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/src/test/java");
        globalConfig.setMapperXmlPath(System.getProperty("user.dir") + "/src/test/java/resources/mapper");
        globalConfig.setBasePackage("com.test");

        //设置表前缀和只生成哪些表
        globalConfig.setTablePrefix("sys_");
        globalConfig.setGenerateTable("sys_user");

        //设置模板路径
        globalConfig.setEntityTemplatePath("D:\\Documents\\配置文件\\entity.tpl");

        //配置生成 entity
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntitySupperClass(BaseEntity.class);

        //配置生成 mapper
        globalConfig.setMapperGenerateEnable(true);
        //配置生成 service
        globalConfig.setServiceGenerateEnable(true);
        //配置生成 serviceImpl
        globalConfig.setServiceImplGenerateEnable(true);
        //配置生成 controller
        globalConfig.setControllerGenerateEnable(true);
        //配置生成 tableDef
        globalConfig.setTableDefGenerateEnable(true);
        //配置生成 mapperXml
        globalConfig.setMapperXmlGenerateEnable(true);
        //配置生成 package-info.java
        globalConfig.setPackageInfoGenerateEnable(true);

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //开始生成代码
        generator.generate();
    }

    @Test
    public void testCodeGen2() {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("12345678");

        GlobalConfig globalConfig = new GlobalConfig();

        //用户信息表，用于存放用户信息。 -> 用户信息
        UnaryOperator<String> tableFormat = (e) -> e.split("，")[0].replace("表", "");

        //设置注解生成配置
        globalConfig.getJavadocConfig()
                .setAuthor("王帅")
                .setTableCommentFormat(tableFormat);

        //设置生成文件目录和根包
        globalConfig.getPackageConfig()
                .setSourceDir(System.getProperty("user.dir") + "/src/test/java")
                .setMapperXmlPath(System.getProperty("user.dir") + "/src/test/java/resources/mapper")
                .setBasePackage("com.test");

        //设置表前缀和只生成哪些表
        globalConfig.getStrategyConfig()
                .setTablePrefix("sys_")
                .setGenerateTable("sys_user");

        //设置模板路径
        //globalConfig.getTemplateConfig()
        //        .setEntity("D:\\Documents\\配置文件\\entity.tpl");

        //配置生成 entity
        globalConfig.enableEntity()
                .setOverwriteEnable(true)
                .setWithLombok(true)
                .setSupperClass(BaseEntity.class);

        //配置生成 mapper
        globalConfig.enableMapper();
        //配置生成 service
        globalConfig.enableService();
        //配置生成 serviceImpl
        globalConfig.enableServiceImpl()
                .setSupperClass(CacheableServiceImpl.class)
                .setCacheExample(true);
        //配置生成 controller
        globalConfig.enableController();
        //配置生成 tableDef
        globalConfig.enableTableDef();
        //配置生成 mapperXml
        globalConfig.enableMapperXml();
        //配置生成 package-info.java
        globalConfig.enablePackageInfo();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //开始生成代码
        generator.generate();
    }

}
