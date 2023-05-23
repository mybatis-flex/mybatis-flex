package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DbTypeUtil;
import org.junit.Test;

public class DbTypeUtilTest {

    @Test
    public void testParseUrl(){
        String url01 = "jdbc:sqlserver://127.0.0.1";
        DbType dbType01 = DbTypeUtil.parseDbType(url01);
        System.out.println(dbType01);
    }
}
