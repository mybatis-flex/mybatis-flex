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
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.querywrapper.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.row.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MybatisFlexStarter {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
                .setDataSource(dataSource)
                .start();


        //查询 ID 为 1 的数据
        Row row = bootstrap.execute(RowMapper.class, rowMapper ->
                rowMapper.selectOneById("tb_account", "id", 1));
        System.out.println(row);



        //新增一条数据，自增
        Row newRow = Row.ofKey(RowKey.ID_AUTO) // id 自增
                .set("user_name", "lisi")
                .set("age", 22)
                .set("birthday", new Date());
        bootstrap.execute(RowMapper.class, rowMapper ->
                rowMapper.insertRow("tb_account", newRow));


        //新增后，查看newRow 的 id，会自动被赋值
        System.out.println(">>>>>>newRow.id: " + newRow.get("id"));



        List<Row> newRowList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Row insertRow = Row.ofKey(RowKey.ID_AUTO) // id 自增
                    .set("user_name", "new_user_"+i)
                    .set("age", 22)
                    .set("birthday", new Date());
            newRowList.add(insertRow);
        }

        //批量插入数据
        bootstrap.execute(RowMapper.class, rowMapper ->
                rowMapper.insertBatchWithFirstRowColumns("tb_account",newRowList));


        //查询全部数据
        List<Row> rows = bootstrap.execute(RowMapper.class, rowMapper ->
                rowMapper.selectAll("tb_account"));

        System.out.println("rows count: " + rows.size()); //8
        System.out.println(rows);


        //分页查询，第 2 页，每页 3 条数据
        Page<Row> rowPage = bootstrap.execute(RowMapper.class, rowMapper ->
                rowMapper.paginate("tb_account", 2, 3, QueryWrapper.create()));
        System.out.println(rowPage);
    }
}
