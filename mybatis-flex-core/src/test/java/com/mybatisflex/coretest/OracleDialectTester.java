package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.impl.OracleDialect;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.table.TableInfoFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        String sql = dialect.forInsertEntityBatch(TableInfoFactory.ofEntityClass(Account.class),accounts);
        System.out.println(sql);
    }


    @Test
    public void testInsertRowBatchSql() {
        List<Row> accounts = new ArrayList<>();
        Row account1 = new Row();
        account1.set("username","michael1");
        account1.set("age",18);
        account1.set("sex",1);
        accounts.add(account1);

        Row account2 = new Row();
        account2.set("username","michael2");
        account2.set("age",18);
        account2.set("sex",1);
        accounts.add(account2);

        Row account3 = new Row();
        account3.set("username","michael3");
        account3.set("age",18);
        account3.set("sex",1);
        accounts.add(account3);


        IDialect dialect = new OracleDialect();
        String sql = dialect.forInsertBatchWithFirstRowColumns(null,"tb_account",accounts);
        System.out.println(sql);
    }

}
