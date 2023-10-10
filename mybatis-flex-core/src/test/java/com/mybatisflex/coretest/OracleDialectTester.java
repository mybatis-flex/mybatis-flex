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

package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.impl.OracleDialect;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.table.TableInfoFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

public class OracleDialectTester {


    @Test
    public void testSelectSql() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT);

        IDialect dialect = new OracleDialect();
        String sql = dialect.forSelectByQuery(query);
        System.out.println(sql);
        assertEquals("SELECT * FROM TB_ACCOUNT", sql);
    }

    @Test
    public void testInsertBatchSql() {
        List<Account> accounts = new ArrayList<>();
        Account account1 = new Account();
        account1.setUserName("michael1");
        account1.setAge(18);
        account1.setSex(1);
        accounts.add(account1);

        Account account2 = new Account();
        account2.setUserName("michael2");
        account2.setAge(19);
        account2.setSex(2);
        accounts.add(account2);

        Account account3 = new Account();
        account3.setUserName("michael3");
        account3.setAge(20);
        account3.setSex(3);
        accounts.add(account3);


        IDialect dialect = new OracleDialect();
        String sql = dialect.forInsertEntityBatch(TableInfoFactory.ofEntityClass(Account.class), accounts);
        System.out.println(sql);
        assertEquals("INSERT ALL  " +
            "INTO TB_ACCOUNT (ID, USER_NAME, BIRTHDAY, SEX, AGE, IS_NORMAL, IS_DELETE) VALUES (?, ?, ?, ?, ?, ?, ?) " +
            "INTO TB_ACCOUNT (ID, USER_NAME, BIRTHDAY, SEX, AGE, IS_NORMAL, IS_DELETE) VALUES (?, ?, ?, ?, ?, ?, ?) " +
            "INTO TB_ACCOUNT (ID, USER_NAME, BIRTHDAY, SEX, AGE, IS_NORMAL, IS_DELETE) VALUES (?, ?, ?, ?, ?, ?, ?) " +
            "SELECT 1 FROM DUAL", sql);
    }


    @Test
    public void testInsertRowBatchSql() {
        List<Row> accounts = new ArrayList<>();
        Row account1 = new Row();
        account1.set("username", "michael1");
        account1.set("age", 18);
        account1.set("sex", 1);
        accounts.add(account1);

        Row account2 = new Row();
        account2.set("username", "michael2");
        account2.set("age", 18);
        account2.set("sex", 1);
        accounts.add(account2);

        Row account3 = new Row();
        account3.set("username", "michael3");
        account3.set("age", 18);
        account3.set("sex", 1);
        accounts.add(account3);


        IDialect dialect = new OracleDialect();
        String sql = dialect.forInsertBatchWithFirstRowColumns(null, "tb_account", accounts);
        System.out.println(sql);
        assertEquals("INSERT ALL  " +
            "INTO TB_ACCOUNT (USERNAME, AGE, SEX) VALUES (?, ?, ?) " +
            "INTO TB_ACCOUNT (USERNAME, AGE, SEX) VALUES (?, ?, ?) " +
            "INTO TB_ACCOUNT (USERNAME, AGE, SEX) VALUES (?, ?, ?) " +
            "SELECT 1 FROM DUAL", sql);
    }

}
