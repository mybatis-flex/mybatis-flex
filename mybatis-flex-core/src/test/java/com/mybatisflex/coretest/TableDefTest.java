package com.mybatisflex.coretest;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.coretest.table.AccountTableDef;
import org.junit.Assert;
import org.junit.Test;

public class TableDefTest {

    @Test
    public void testTableDefAlias() {
        AccountTableDef account1 = AccountTableDef.ACCOUNT;
        AccountTableDef account2 = AccountTableDef.ACCOUNT.withAlias("a2");
        String sql = new QueryWrapper().select(account1.AGE, account2.ID)
            .from(account1)
            .join(account2).on(account1.ID.eq(account2.ID))
            .toSQL();
        System.out.println(sql);
        String expected = "SELECT `tb_account`.`age`, `a2`.`id` FROM `tb_account` " +
            "JOIN `tb_account` AS `a2` ON `tb_account`.`id` = `a2`.`id`";
        Assert.assertEquals(expected, sql);
    }
}
