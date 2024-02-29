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
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.RowUtil;
import com.mybatisflex.core.util.SqlUtil;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author 王帅
 * @since 2023-08-25
 */
public class BatchTester {

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql").setScriptEncoding("UTF-8")
                .build();

        MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
                .setLogImpl(StdOutImpl.class)
                .addMapper(AccountMapper.class)
                .start();
    }

    @Test
    public void testBatch() {
        List<Account> accounts = initAccounts();
        int[] ints = Db.executeBatch(accounts, AccountMapper.class, AccountMapper::insertSelective);
        System.out.println(Arrays.toString(ints));
        System.out.println(SqlUtil.toBool(ints));
        RowUtil.printPretty(Db.selectAll("tb_account"));
    }

    private static List<Account> initAccounts() {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Account account = new Account();
            account.setUserName("wangshuai" + i);
            account.setAge(168 + i);
            account.setBirthday(new Date());
            accounts.add(account);
        }
        return accounts;
    }

}
