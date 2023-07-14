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

import com.mybatisflex.core.query.FunctionQueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.StringQueryColumn;
import org.junit.Test;

import static com.mybatisflex.core.query.QueryMethods.*;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

/**
 * @author 王帅
 * @since 2023-06-28
 */
public class FunctionSqlTest {

    @Test
    public void test() {
        String sql = QueryWrapper.create()
            .select(new FunctionQueryColumn("NOW").as("n1"))
            .select(new FunctionQueryColumn("NOW", new StringQueryColumn("")).as("n2"))
            .select(new FunctionQueryColumn("CONCAT", ACCOUNT.USER_NAME, ACCOUNT.AGE).as("c1"))
            .from(ACCOUNT)
            .toSQL();
        System.out.println(sql);
    }

    @Test
    public void test02() {
        String sql = QueryWrapper.create()
            .select(concatWs(string("abc"), ACCOUNT.USER_NAME, ACCOUNT.BIRTHDAY))
            .select(abs(number(-3)))
            .from(ACCOUNT)
            .where(not(ACCOUNT.ID.eq(1)))
            .toSQL();
        System.out.println(sql);
    }

}
