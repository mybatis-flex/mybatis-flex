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
import com.mybatisflex.core.row.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

public class DbTestStarter {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql").setScriptEncoding("UTF-8")
//                .addScript("data.sql")
            .build();

        MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .start();

        Row row1 = Db.selectOneById(null, "tb_account", "id", 1);
        RowUtil.printPretty(row1);

        //查询全部
        List<Row> rows = Db.selectAll(null, "tb_account");
        RowUtil.printPretty(rows);


        //插入 1 条数据

        Row row = Row.ofKey(RowKey.AUTO);
//        Row row = new Row();
//        row.set("id", 3);
        row.set("user_name", "michael yang");
        row.set("age", 18);
        row.set("birthday", new Date());
        Db.insert(null, "tb_account", row);

        //查看刚刚插入数据的主键 id
        System.out.println(">>>>>>>>>id: " + row.get("id"));

        //INSERT INTO tb_account
        //VALUES (1, '张三', 18, 0,'2020-01-11', null,0),

        Db.updateBatch("insert into tb_account(user_name,age,birthday) values (?,?,?)", new BatchArgsSetter() {
            @Override
            public int getBatchSize() {
                return 10;
            }

            @Override
            public Object[] getSqlArgs(int index) {
                Object[] args = new Object[3];
                args[0] = "michael yang";
                args[1] = 18 + index;
                args[2] = new Date();
                return args;
            }
        });


        //再次查询全部数据
        rows = Db.selectAll(null, "tb_account");
        RowUtil.printPretty(rows);

//        for (Row row2 : rows) {
////            for (String s : row2.keySet()) {
////                if (!s.equalsIgnoreCase("id")) {
////                    row2.set(s, row2.get(s));
////                }
////            }
//            rows.remove("id");
//        }

//        rows.forEach(row2 -> row2.setPrimaryKeys(RowKey.AUTO));
//        rows.forEach(r -> {
//            r.prepareAttrsByKeySet();
//            r.setPrimaryKeys(RowKey.AUTO);
//        });
//        Db.insertBatch(null,"tb_account", rows, 100);

        //再次查询全部数据
        rows = Db.selectAll(null, "tb_account");
        RowUtil.printPretty(rows);
    }

}
