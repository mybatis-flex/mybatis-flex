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
package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.row.RowUtil;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;

public class RowTestStarter {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
//                .setLogImpl(StdOutImpl.class)
                .start();

        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());

//        Page<Row> rowPage = Db.paginate("flex","tb_account", 1, 10, QueryWrapper.create().hint("USE_MERGE"));
//        System.out.println(rowPage);


        //查询 ID 为 1 的数据
//        Row row = Db.selectOneById("tb_account", "id", 1);
//        System.out.println(row);


//        QueryWrapper query = new QueryWrapper();
//        query.select().from(ACCOUNT).leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID));
//        List<Row> rows = Db.selectListByQuery(query);
//       RowUtil.printPretty(rows);
//
//        System.out.println("--------");
//
//        List<Account> accounts = RowUtil.toEntityList(rows, Account.class,0);
//        System.out.println(accounts);
//
//        List<Article> articles = RowUtil.toEntityList(rows, Article.class, 1);
//        System.out.println(articles);


        List<Row> rowList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Row row = Row.ofKey(RowKey.AUTO);
            row.set(ACCOUNT.USER_NAME, "zhang" + i);
            row.set(ACCOUNT.AGE, 18);
            rowList.add(row);
        }

        Db.insertBatch("tb_account", rowList);

        for (Row row : rowList) {
            System.out.println(">>>>>>>id: " + row.get("id"));
        }

        List<Row> rows1 = Db.selectAll("tb_account");
        RowUtil.printPretty(rows1);

        System.out.println("//////update....");

        Row row = Row.ofKey("id", 5);
        row.setRaw("age", "age + 5");
        Db.updateById("tb_account", row);

        Row row1 = Db.selectOneById("tb_account", "id", 5);
        RowUtil.printPretty(row1);


        System.out.println("////////////insert");
        List<Row> rowList1 = Db.selectAll("tb_account");
        rowList1.forEach(new Consumer<Row>() {
            @Override
            public void accept(Row row) {
                row.remove("ID");
            }
        });

        Db.insertBatch("tb_account", rowList1);

        List<Row> rowList2 = Db.selectAll("tb_account");
        RowUtil.printPretty(rowList2);


    }
}
