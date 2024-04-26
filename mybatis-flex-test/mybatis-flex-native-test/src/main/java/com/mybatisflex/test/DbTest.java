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
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowUtil;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 王帅
 * @since 2023-10-11
 */
@SuppressWarnings("all")
public class DbTest {

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            // .setLogImpl(StdOutImpl.class)
            .setDataSource(dataSource)
            .start();

        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());

        /*
         * 指定当结果集中值为 null 的时候是否调用映射对象的 setter（map 对象时为 put）方法，
         * 这在依赖于 Map.keySet() 或 null 值进行初始化时比较有用。注意基本类型（int、boolean 等）
         * 是不能设置成 null 的。
         */
        Configuration configuration = bootstrap.getConfiguration();
        configuration.setCallSettersOnNulls(true);
    }

    @SuppressWarnings("all")
    static String tb_account = "tb_account";

    @Test
    public void test01() {
        Db.updateBySql("update tb_account set options = null;");

        List<Row> rows = Db.selectAll(tb_account);

        rows.stream()
            .map(row -> row.get("OPTIONS"))
            .forEach(Assert::assertNull);
    }

    @Test
    public void test02() {
        Map map = Db.selectFirstAndSecondColumnsAsMap(QueryWrapper.create().from(tb_account));
        Map map2 = Db.selectFirstAndSecondColumnsAsMap("select * from tb_account");
        System.out.println(map);
        System.out.println(map2);
        assert map.equals(map2);

    }

    @Test
    public void test03() {
        try {
            Account account = UpdateEntity.of(Account.class, 1);
            account.setAge(1);
            List<Account> accounts = new ArrayList<>();
            accounts.add(account);
            Account account82 = UpdateEntity.of(Account.class, 2);
            account82.setAge(2);
            UpdateWrapper updateWrapper = UpdateWrapper.of(account82);
            updateWrapper.setRaw("age", "age+1");
            accounts.add(account82);
            Account account83 = new Account();
            account83.setId(3L);
            account83.setAge(4);
            accounts.add(account83);
            Db.updateEntitiesBatch(accounts);
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    public void testTypeHandler() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select("*")
            .from("tb_account")
            .where("age = ?", 3);

        List<Row> rows = Db.selectListByQuery(queryWrapper);

        RowUtil.printPretty(rows);
    }

    @Test
    public void testMapArgs() {
        Map<String, Integer> map = Collections.singletonMap("age", 18);
        List<Row> rowList1 = Db.selectListBySql("select * from tb_account where age > #{age}", map);
        List<Row> rowList2 = Db.selectListBySql("select * from tb_account where age > ${age}", map);
        List<Row> rowList3 = Db.selectListBySql("select * from tb_account where age > ?", 18);
        List<Row> rowList4 = Db.selectListBySql("select * from tb_account where age > 18");
        List<Row> rowList5 = Db.selectListBySql("select * from tb_account where age > 18", (Object[]) null);
        Assert.assertEquals(rowList1.toString(), rowList2.toString());
        Assert.assertEquals(rowList1.toString(), rowList3.toString());
        Assert.assertEquals(rowList1.toString(), rowList4.toString());
        Assert.assertEquals(rowList1.toString(), rowList5.toString());
        Assert.assertThrows(PersistenceException.class,
            () -> Db.selectListBySql("select * from tb_account where age > #{age} and id > ?", map, 1));
    }

    @Test
    public void testRowUpdate() {
        Row row = new Row();
        row.setRaw("age", QueryWrapper.create()
            .select("age")
            .from("tb_account")
            .where("id = ?", 1));
        Assert.assertEquals(1, Db.updateByQuery("tb_account", row, QueryWrapper.create().where("id = ?", 1)));
    }

    @Test
    public void testRowInsert() {
        Row row = new Row();
        row.setRaw("age", QueryWrapper.create()
            .select("age")
            .from("tb_account")
            .where("id = ?", 1));
        Assert.assertEquals(1, Db.insert("tb_account", row));
    }

}
