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
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowUtil;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 王帅
 * @since 2023-10-11
 */
public class DbTest {

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setLogImpl(StdOutImpl.class)
            .setDataSource(dataSource)
            .start();

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
            Account account2 = UpdateEntity.of(Account.class, 2);
            account2.setAge(2);
            UpdateWrapper updateWrapper = UpdateWrapper.of(account2);
            updateWrapper.setRaw("age", "age+1");
            accounts.add(account2);
            Account account3 = new Account();
            account3.setId(3L);
            account3.setAge(4);
            accounts.add(account3);
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

}
