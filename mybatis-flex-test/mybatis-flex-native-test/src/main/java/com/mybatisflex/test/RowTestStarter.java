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
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.row.RowUtil;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

public class RowTestStarter {

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema_row.sql")
            .addScript("data_row.sql")
            .build();

        MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .setLogImpl(StdOutImpl.class)
            .start();

        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }


    @Test
    public void testSetRaw(){
        Row row = new Row();
        row.set("user_name","michael");
        row.setRaw("birthday","now()");

        Db.insert("tb_account",row);
        List<Row> rowList = Db.selectAll("tb_account");
        RowUtil.printPretty(rowList);
    }

    @Test
    public void testCustomRowKey(){
        RowKey rowKey = RowKey.of("id", KeyType.Generator, KeyGenerators.flexId);

        Row row = Row.ofKey(rowKey);
        row.set("user_name","michael");
        row.setRaw("birthday","now()");

        Db.insert("tb_account",row);
        List<Row> rowList = Db.selectAll("tb_account");
        RowUtil.printPretty(rowList);
    }

}
