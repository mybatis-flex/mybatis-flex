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
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.row.RowUtil;
import com.mybatisflex.core.update.RawValue;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
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
public class DbTest273 {

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema_273.sql")
            .addScript("data273.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .start();

        /*
         * 指定当结果集中值为 null 的时候是否调用映射对象的 setter（map 对象时为 put）方法，
         * 这在依赖于 Map.keySet() 或 null 值进行初始化时比较有用。注意基本类型（int、boolean 等）
         * 是不能设置成 null 的。
         */
        Configuration configuration = bootstrap.getConfiguration();
        configuration.setCallSettersOnNulls(true);

        Db.updateBySql("update tb_account set options = null;");
    }

    @SuppressWarnings("all")
    static String tb_account = "tb_account";



    /**
     * https://github.com/mybatis-flex/mybatis-flex/issues/273
     */
    @Test
    public void testDbInsertBatchWithFirstRowColumns() {
        List<Row> rows = new ArrayList<>();

        Row row1 = new Row();
        row1.put("id", 111);
        row1.put("user_name", "张三");
        row1.put("age", 20);
        rows.add(row1);

        Row row2 = new Row();
        row2.put("age", 30);
        row2.put("id", 20);
        row2.put("user_name", "李四");
        rows.add(row2);

        Db.insertBatchWithFirstRowColumns("tb_account", rows);

        Row row3= new Row();
        row3.put("age", 30);
        row3.put("id", 333);
        row3.put("user_name", "李四3");
        Db.insert("tb_account",row3);

        RowUtil.printPretty(Db.selectAll("tb_account"));
    }

    /**
     * https://github.com/mybatis-flex/mybatis-flex/issues/273
     */
    @Test
    public void testDbInsertBatchWithFirstRowColumns02() {
        List<Row> rows = new ArrayList<>();

        Row row1 = Row.ofKey(RowKey.SNOW_FLAKE_ID);
        row1.put("user_name", "张三");
        row1.put("age", 20);
        rows.add(row1);

        Row row2 = Row.ofKey(RowKey.SNOW_FLAKE_ID);
        row2.put("age", 30);
        row2.put("user_name", "李四");
        rows.add(row2);

//        Row row3 = Row.ofKey(RowKey.SNOW_FLAKE_ID);
//        row3.put("age", new RawValue(11));
//        row3.put("user_name", "李四3");
//        rows.add(row3);

        Db.insertBatchWithFirstRowColumns("tb_account", rows);

        RowUtil.printPretty(Db.selectAll("tb_account"));
    }
}
