package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Assert;
import org.junit.Test;

import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

public class QueryMethodsTest {

    @Test
    public void testGetDate() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ID)
            .from(ACCOUNT)
            .where(ACCOUNT.BIRTHDAY.lt(QueryMethods.getDate()));
        DialectFactory.setHintDbType(DbType.SQLSERVER);
        Assert.assertEquals("SELECT [id] FROM [tb_account] WHERE [birthday] < GETDATE()", queryWrapper.toSQL());
        DialectFactory.clearHintDbType();
    }

    @Test
    public void testSysDate() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ID)
            .from(ACCOUNT)
            .where(ACCOUNT.BIRTHDAY.lt(QueryMethods.sysDate()));
        DialectFactory.setHintDbType(DbType.ORACLE);
        Assert.assertEquals("SELECT ID FROM TB_ACCOUNT WHERE BIRTHDAY < SYSDATE()", queryWrapper.toSQL());
        DialectFactory.clearHintDbType();
    }

}
