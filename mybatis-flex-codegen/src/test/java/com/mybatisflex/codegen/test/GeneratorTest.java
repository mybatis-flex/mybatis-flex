/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TableConfig;
import com.mybatisflex.codegen.config.TableDefConfig;
import com.mybatisflex.codegen.constant.TemplateConst;
import com.mybatisflex.core.mask.Masks;
import com.mybatisflex.spring.service.impl.CacheableServiceImpl;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;

import java.util.function.UnaryOperator;

public class GeneratorTest {


    //    @Test
    public void testCodeGen1() {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flex_test?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        GlobalConfig globalConfig = new GlobalConfig();

        // 用户信息表，用于存放用户信息。 -> 用户信息
        UnaryOperator<String> tableFormat = (e) -> e.split("，")[0].replace("表", "");

        // 设置注解生成配置
        globalConfig.setAuthor("Michael Yang");
        globalConfig.setTableCommentFormat(tableFormat);

        // 设置生成文件目录和根包
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/src/test/java");
        globalConfig.setMapperXmlPath(System.getProperty("user.dir") + "/src/test/java/resources/mapper");
        globalConfig.setBasePackage("com.test");

        // 设置表前缀和只生成哪些表
        globalConfig.setTablePrefix("sys_", "tb_");
//        globalConfig.setGenerateTable("sys_user","tb_account");

        // 设置模板路径
//        globalConfig.setEntityTemplatePath("D:\\Documents\\配置文件\\entity.tpl");

        // 配置生成 entity
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntitySuperClass(BaseEntity.class);

        // 配置生成 mapper
        globalConfig.setMapperGenerateEnable(true);
        // 配置生成 service
        globalConfig.setServiceGenerateEnable(true);
        // 配置生成 serviceImpl
        globalConfig.setServiceImplGenerateEnable(true);
        // 配置生成 controller
        globalConfig.setControllerGenerateEnable(true);
        // 配置生成 tableDef
        globalConfig.setTableDefGenerateEnable(true);
        // 配置生成 mapperXml
        globalConfig.setMapperXmlGenerateEnable(true);
        // 配置生成 package-info.java
        globalConfig.setPackageInfoGenerateEnable(true);

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 开始生成代码
        generator.generate();
    }

    // @Test
    public void testCodeGen2() {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("12345678");

        GlobalConfig globalConfig = new GlobalConfig();

        // 用户信息表，用于存放用户信息。 -> 用户信息
        UnaryOperator<String> tableFormat = (e) -> e.split("，")[0].replace("表", "");

        // 设置注解生成配置
        globalConfig.getJavadocConfig()
            .setAuthor("王帅")
            .setTableCommentFormat(tableFormat);

        // 设置生成文件目录和根包
        globalConfig.getPackageConfig()
            .setSourceDir(System.getProperty("user.dir") + "/src/test/java")
            .setMapperXmlPath(System.getProperty("user.dir") + "/src/test/resources/mapper")
            .setBasePackage("com.test");

        // 设置表前缀和只生成哪些表
        globalConfig.getStrategyConfig()
            .setTablePrefix("sys_")
            .setGenerateTable("sys_user");

        // 设置模板路径
        // globalConfig.getTemplateConfig()
        //        .setEntity("D:\\Documents\\配置文件\\entity.tpl");

        // 配置生成 entity
        globalConfig.enableEntity()
            .setOverwriteEnable(true)
            .setWithLombok(true)
            .setWithSwagger(true)
            .setSuperClass(BaseEntity.class);

        // 配置生成 mapper
        globalConfig.enableMapper();
        // 配置生成 service
        globalConfig.enableService();
        // 配置生成 serviceImpl
        globalConfig.enableServiceImpl()
            .setSuperClass(CacheableServiceImpl.class)
            .setCacheExample(true);
        // 配置生成 controller
        globalConfig.enableController();
        // 配置生成 tableDef
        globalConfig.enableTableDef();
        // 配置生成 mapperXml
        globalConfig.enableMapperXml();
        // 配置生成 package-info.java
        globalConfig.enablePackageInfo();

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 开始生成代码
        generator.generate();
    }

    //    @Test
    public void testCodeGen3() {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        // 通过 datasource 和 globalConfig 创建代码生成器
        new Generator(dataSource, globalConfig()).generate();
        new Generator(dataSource, globalConfig()).generate();
        new Generator(dataSource, globalConfig()).generate();
    }

    private GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();

        // 用户信息表，用于存放用户信息。 -> 用户信息
        UnaryOperator<String> tableFormat = (e) -> e.split("，")[0].replace("表", "");
        // 属性添加句号
        UnaryOperator<String> columnFormat = (e) -> e.concat("。");

        // 设置注解生成配置
        globalConfig.getJavadocConfig()
            .setAuthor("王帅")
            .setTableCommentFormat(tableFormat)
            .setColumnCommentFormat(columnFormat);

        // 设置生成文件目录和根包
        globalConfig.getPackageConfig()
            .setSourceDir(System.getProperty("user.dir") + "/src/test/java")
            .setMapperXmlPath(System.getProperty("user.dir") + "/src/test/java/resources/mapper")
            .setBasePackage("com.test");

        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("phonenumber");
        columnConfig.setLarge(true);

        TableConfig tableConfig = new TableConfig();
        tableConfig.setTableName("sys_user");
        tableConfig.setColumnConfig(columnConfig);

        ColumnConfig logicDelete = new ColumnConfig();
        logicDelete.setColumnName("del_flag");
        logicDelete.setLogicDelete(true);

        // 设置表前缀和只生成哪些表
        globalConfig.getStrategyConfig()
            .setTablePrefix("sys_")
            .setGenerateTable("sys_user")
            .setColumnConfig(logicDelete)
            .setTableConfig(tableConfig);

        // 配置生成 tableDef
        globalConfig.enableTableDef()
            .setInstanceSuffix("Def")
            .setPropertiesNameStyle(TableDefConfig.NameStyle.LOWER_CAMEL_CASE)
            .setOverwriteEnable(true);

        globalConfig.disableTableDef();

        // 配置生成 entity
        globalConfig.enableEntity()
            .setOverwriteEnable(true)
            .setDataSource("ds1")
            .setWithLombok(true)
            .setWithSwagger(true);

        // 配置生成 mapper
        globalConfig.enableMapper()
            .setOverwriteEnable(true)
            .setMapperAnnotation(true);

        return globalConfig;
    }

    //    @Test
    public void testCodeGen4() {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        GlobalConfig globalConfig = new GlobalConfig();

        // 用户信息表，用于存放用户信息。 -> 用户信息
        UnaryOperator<String> tableFormat = (e) -> e.split("，")[0].replace("表", "");

        // 设置注解生成配置
        globalConfig.getJavadocConfig()
            .setAuthor("王帅")
            .setTableCommentFormat(tableFormat);

        // 设置生成文件目录和根包
        globalConfig.getPackageConfig()
            .setSourceDir(System.getProperty("user.dir") + "/src/test/java")
            .setMapperXmlPath(System.getProperty("user.dir") + "/src/test/resources/mapper")
            .setBasePackage("com.test");

        // 设置模板路径
        globalConfig.getTemplateConfig()
            .setServiceImpl(TemplateConst.SERVICE_IMPL_SOLON);

        // 设置表前缀和只生成哪些表
        globalConfig.getStrategyConfig()
            .setTablePrefix("sys_")
            .setGenerateTable("sys_user");

        // 配置生成 entity
        globalConfig.enableEntity()
            .setOverwriteEnable(true)
            .setWithLombok(false)
            .setWithActiveRecord(true);

        // 配置生成 service
        globalConfig.enableMapper();
        globalConfig.enableService();
        globalConfig.enableServiceImpl();

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 开始生成代码
        generator.generate();
    }

//    @Test
    public void testCodeGen5() {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("12345678");

        GlobalConfig globalConfig = new GlobalConfig();

        // 用户信息表，用于存放用户信息。 -> 用户信息
        UnaryOperator<String> tableFormat = (e) -> e.split("，")[0].replace("表", "");

        // 设置注解生成配置
        globalConfig.getJavadocConfig()
            .setAuthor("王帅")
            .setTableCommentFormat(tableFormat);

        // 设置生成文件目录和根包
        globalConfig.getPackageConfig()
            .setSourceDir(System.getProperty("user.dir") + "/src/test/java")
            .setMapperXmlPath(System.getProperty("user.dir") + "/src/test/resources/mapper")
            .setBasePackage("com.test");

        // 设置表前缀和只生成哪些表
        globalConfig.getStrategyConfig()
            .setTablePrefix("sys_")
            .setGenerateTable("sys_user", "sys_role");

        // 全局表配置
        TableConfig tableConfig = TableConfig.builder()
            .camelToUnderline(false)
            .mapperGenerateEnable(false)
            .build();

        // sys_user 字段单独配置
        ColumnConfig userColumnConfig = ColumnConfig.create()
            .setColumnName("update_time")
            .setOnUpdateValue("NOW()");

        ColumnConfig userNameConfig = ColumnConfig.create()
            .setColumnName("user_name")
            .setMaskType(Masks.CHINESE_NAME);

        // sys_user 表单独配置
        TableConfig userTableConfig = TableConfig.create()
            .setTableName("sys_user")
            .setColumnConfig(userNameConfig)
            .setColumnConfig(userColumnConfig);

        // 全局字段配置
        ColumnConfig columnConfig = ColumnConfig.builder()
            .columnName("create_time")
            .onInsertValue("NOW()")
            .build();

        globalConfig.getStrategyConfig()
            .setTableConfig(tableConfig)
            .setTableConfig(userTableConfig)
            .setColumnConfig(columnConfig);

        // 配置生成 entity
        globalConfig.enableEntity()
            // .setAlwaysGenColumnAnnotation(true)
            .setOverwriteEnable(true)
            .setWithLombok(true);

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 开始生成代码
        generator.generate();
    }

}
