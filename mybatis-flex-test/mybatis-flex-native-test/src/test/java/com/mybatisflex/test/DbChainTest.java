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
import com.mybatisflex.core.row.DbChain;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.row.RowUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * @author 王帅
 * @since 2023-07-23
 */
public class DbChainTest {

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql")
            .build();

        MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .start();

        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }

    @Test
    public void testSave() {
        boolean saved = DbChain.table("tb_account")
            .setId(RowKey.AUTO)
            .set("user_name", "王帅")
            .set("age", 18)
            .set("birthday", new Date())
            .save();

        Row row = DbChain.table("tb_account")
            .where("user_name = ?", "王帅")
            .one();

        System.out.println(row);
    }

    @Test
    public void testUpdate() {
        boolean updated = DbChain.table("tb_account")
            .setId(RowKey.AUTO, 1)
            .set("age", 1000)
            .updateById();

        Row row = DbChain.table("tb_account")
            .where("id = ?", 1)
            .one();

        System.out.println(row);
    }

    @Test
    public void testRemove() {
        DbChain.table("tb_account")
            .where("id = ?", 1)
            .remove();

        long count = DbChain.table("tb_account").count();

        List<Row> tb_account = DbChain.table("tb_account").list();
        RowUtil.printPretty(tb_account);

        System.out.println(">>>>>>testRemove count: " + count);
    }

    @Test
    public void testList() {
        DbChain.table("tb_account")
            .list()
            .forEach(System.out::println);
    }

}
