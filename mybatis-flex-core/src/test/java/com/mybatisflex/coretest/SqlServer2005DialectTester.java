package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Test;

import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

public class SqlServer2005DialectTester {


    @Test
    public void testSelectSql() {
        QueryWrapper query = new QueryWrapper()
                .select()
                .from(ACCOUNT)
            .where(ACCOUNT.ID.in("100","200"))
            .and(ACCOUNT.SEX.eq(1))
            .orderBy(ACCOUNT.ID.desc())
                .limit(10,10);

        IDialect dialect = new CommonsDialectImpl(KeywordWrap.SQUARE_BRACKETS,LimitOffsetProcessor.SQLSERVER_2005);
        String sql = dialect.forSelectByQuery(query);
        System.out.println(sql);
    }


}
