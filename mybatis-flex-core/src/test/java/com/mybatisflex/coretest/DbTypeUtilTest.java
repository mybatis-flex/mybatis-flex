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

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DbTypeUtil;
import org.junit.Assert;
import org.junit.Test;

import static com.mybatisflex.core.dialect.DbType.SQLSERVER_2005;

public class DbTypeUtilTest {

    @Test
    public void testParseUrl() {
        String url01 = "jdbc:sqlserver://127.0.0.1";
        DbType dbType01 = DbTypeUtil.parseDbType(url01);
        System.out.println(dbType01);
        Assert.assertEquals(dbType01, SQLSERVER_2005);
    }

}
