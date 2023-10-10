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
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Assert;
import org.junit.Test;

import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

public class SqlServer2005DialectTester {


    @Test
    public void testSelectSql() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT)
            .where(ACCOUNT.ID.in("100", "200"))
            .and(ACCOUNT.SEX.eq(1))
            .orderBy(ACCOUNT.ID.desc())
            .limit(10, 10);

        IDialect dialect = new CommonsDialectImpl(KeywordWrap.SQUARE_BRACKETS, LimitOffsetProcessor.SQLSERVER_2005);
        String sql = dialect.forSelectByQuery(query);
        System.out.println(sql);
        Assert.assertEquals("WITH temp_datas AS(" +
            "SELECT ROW_NUMBER() OVER ( ORDER BY [id] DESC) as __rn, * FROM [tb_account] WHERE [id] IN (?, ?) AND [sex] = ?" +
            ") " +
            "SELECT * FROM temp_datas WHERE __rn BETWEEN 11 AND 20 ORDER BY __rn", sql);
    }


}
