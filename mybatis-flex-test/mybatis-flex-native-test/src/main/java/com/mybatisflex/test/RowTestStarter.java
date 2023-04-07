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
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.row.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Date;

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
                rowMapper.insert("tb_account", newRow));

//
//        //新增后，查看newRow 的 id，会自动被赋值
//        System.out.println(">>>>>>newRow.id: " + newRow.get("id"));
//
//
//        bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.insertBySql("insert into tb_account(user_name,age,birthday) values (?,?,?)"
//                        , "张三"
//                        , 18
//                        , new Date()));
//
//
//        List<Row> newRowList = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            Row insert = Row.ofKey(RowKey.ID_AUTO) //id 自增
//                    .set("user_name", "new_user_" + i)
//                    .set("age", 22)
//                    .set("birthday", new Date());
//            newRowList.add(insert);
//        }
//
//        //批量插入数据
//        bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.insertBatchWithFirstRowColumns("tb_account", newRowList));
//
//
//        //根据主键 ID 删除数据
//        bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.deleteById("tb_account", Row.ofKey(RowKey.ID_AUTO, 1)));
//
//
//        //根据原生 SQL 删除数据
//        bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.deleteBySql("delete from tb_account where id  = ? ", 2));
//
//
//        //根据主键 列表 删除数据
//        bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.deleteBatchByIds("tb_account", "id", Arrays.asList(2, 3, 4)));
//
//
//        Map<String, Object> where = new HashMap<>();
//        where.put("id", 2);
//        //根据 map 删除数据
//        bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.deleteByMap("tb_account", where));
//
//
//        //更新数据
//        Row updateRow = Row.ofKey(RowKey.ID_AUTO, 6)
//                .set("user_name", "newNameTest");
//        bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.updateById("tb_account", updateRow));
//
//
//        //更新数据
//        bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.updateBySql("update tb_account set user_name = ? where id = ?", "李四", 7));
//
//
//        //查询全部数据
//        List<Row> rows = bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.selectAll("tb_account"));
//
//
//        System.out.println("rows count: " + rows.size()); // 7
//        System.out.println(rows);
//
//
//        //分页查询，第 2 页，每页 4 条数据
//        Page<Row> rowPage = bootstrap.execute(RowMapper.class, rowMapper ->
//                rowMapper.paginate("tb_account", 2, 4, QueryWrapper.create()));
//        System.out.println(rowPage);
    }
}
