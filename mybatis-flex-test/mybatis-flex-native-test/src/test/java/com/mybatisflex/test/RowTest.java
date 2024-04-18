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

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;


public class RowTest implements WithAssertions {

    private static final String DATA_SOURCE_KEY = "row";

    private EmbeddedDatabase dataSource;

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }

    @Before
    public void init() {
        dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema_row.sql")
            .addScript("data_row.sql")
                .setScriptEncoding("UTF-8")
            .build();

        new MybatisFlexBootstrap().setDataSource(DATA_SOURCE_KEY, dataSource)
            .setLogImpl(StdOutImpl.class)
            .start();

        DataSourceKey.use(DATA_SOURCE_KEY);
    }

    @After
    public void destroy() {
        this.dataSource.shutdown();
        DataSourceKey.clear();
    }

    @Test
    public void testSetRaw() {
        Row row = new Row();
        row.set("user_name", "michael");
        row.setRaw("birthday", "now()");

        Db.insert("tb_account", row);
        List<Row> rowList = Db.selectAll("tb_account");
        assertThat(rowList).hasSize(3)
            .extracting("USER_NAME")
            .containsOnly("张三", "王麻子叔叔", "michael");
        // 插入的数据没有 id
        assertThat(row).doesNotContainKey("id");
    }

    @Test
    public void testCustomRowKey() {
        RowKey rowKey = RowKey.of("id", KeyType.Generator, KeyGenerators.flexId);

        Row row = Row.ofKey(rowKey);
        row.set("user_name", "michael");
        row.setRaw("birthday", "now()");

        Db.insert("tb_account", row);
        List<Row> rowList = Db.selectAll("tb_account");
        assertThat(rowList).hasSize(3)
            .extracting("USER_NAME")
            .containsOnly("张三", "王麻子叔叔", "michael");
        // 指定了主键生成策略，有 ID 值
        assertThat(row).containsKey("id")
            .extracting("id")
            .isInstanceOf(Long.class);
    }


    // https://gitee.com/mybatis-flex/mybatis-flex/issues/I7W7HQ
    @Test
    public void testGiteeIssue_I7W7HQ() {
        QueryWrapper qw = QueryWrapper.create().select("id, MAX(`tb_account`.`age`)")
            .groupBy("id")
            .from(ACCOUNT);
        List<Row> rowList = Db.selectListByQuery(qw);
        assertThat(rowList).hasSize(2)
            .extracting("MAX(TB_ACCOUNT.AGE)")
            .containsOnly(18, 19);
    }

}
