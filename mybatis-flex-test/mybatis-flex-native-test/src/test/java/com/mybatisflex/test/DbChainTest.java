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

import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.row.DbChain;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 王帅
 * @since 2023-07-23
 */
public class DbChainTest implements WithAssertions {

    private static final String[] PROPERTIES = new String[]{"ID", "USER_NAME", "AGE", "BIRTHDAY"};
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String ENVIRONMENT_ID = "db_chain";

    private EmbeddedDatabase database;

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }

    @Before
    public void init() {
        this.database = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("auto_increment_key_schema.sql")
            .addScript("auto_increment_key_data.sql").setScriptEncoding("UTF-8")
            .build();

        // Environment environment = new Environment(ENVIRONMENT_ID, new JdbcTransactionFactory(), this.database);
        // FlexConfiguration configuration = new FlexConfiguration(environment);
        // configuration.addMapper(RowMapper.class);
        // FlexGlobalConfig flexGlobalConfig = new FlexGlobalConfig();
        // flexGlobalConfig.setConfiguration(configuration);
        // flexGlobalConfig.setSqlSessionFactory(new DefaultSqlSessionFactory(configuration));
        // FlexGlobalConfig.setConfig(environment.getId(), flexGlobalConfig, false);
    }

    @After
    public void destroy() {
        // 用一个销毁一个，保证每个测试方法的都是一个新的
        this.database.shutdown();
    }

    @Test
    public void testSave() {
        Date birthday = new Date();
        boolean saved = DbChain.table("tb_account")
            .setId(RowKey.AUTO)
            .set("user_name", "王帅")
            .set("age", 18)
            .set("birthday", birthday)
            .save();
        assertThat(saved).isTrue();

        Row row = DbChain.table("tb_account")
            .where("user_name = ?", "王帅")
            .one();

        assertThat(row).extracting(PROPERTIES)
            .containsExactly(3, "王帅", 18, new Timestamp(birthday.getTime()));
    }

    @Test
    @SneakyThrows
    public void testUpdate() {
        boolean updated = DbChain.table("tb_account")
            .setId(RowKey.AUTO, 1)
            .set("age", 1000)
            .updateById();
        assertThat(updated).isTrue();

        Row row = DbChain.table("tb_account")
            .where("id = ?", 1)
            .one();

        Date date = DATE_FORMAT.parse("2020-01-11");
        assertThat(row).extracting(PROPERTIES)
            .containsExactly(1, "张三", 1000, new Timestamp(date.getTime()));
    }

    @Test
    @SneakyThrows
    public void testRemove() {
        DbChain.table("tb_account")
            .where("id = ?", 1)
            .remove();

        long count = DbChain.table("tb_account").count();
        assertThat(count).isEqualTo(1L);

        List<Row> tb_account = DbChain.table("tb_account").list();

        assertThat(tb_account).hasSize(1)
            .extracting(PROPERTIES)
            .containsExactly(tuple(2, "王麻子叔叔", 19, new Timestamp(DATE_FORMAT.parse("2021-03-21").getTime())));
    }

    @Test
    public void testList() {
        List<Row> list = DbChain.table("tb_account").list();
        assertThat(list).hasSize(2)
            .extracting("ID", "USER_NAME")
            .containsExactly(tuple(1, "张三"), tuple(2, "王麻子叔叔"));
    }
}
