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

package com.mybatisflex.test.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.mybatisflex.core.query.QueryMethods.count;
import static com.mybatisflex.core.query.QueryMethods.distinct;
import static com.mybatisflex.test.model.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.model.table.RoleTableDef.ROLE;
import static com.mybatisflex.test.model.table.UserRoleTableDef.USER_ROLE;
import static com.mybatisflex.test.model.table.UserTableDef.USER;

/**
 * @author 王帅
 * @since 2023-06-09
 */
class CloneTest {

    @Test
    void test03() {
        String jsonString = JSON.toJSONString(newQueryWrapper(), JSONWriter.Feature.FieldBased, JSONWriter.Feature.ReferenceDetection);
        System.out.println(jsonString);
    }

    @Test
    void test() {
        QueryWrapper queryWrapper = newQueryWrapper();
        Class<QueryWrapper> queryWrapperClass = QueryWrapper.class;
        int count = 10000;

        /*
         * new: 41ms
         * clone: 65ms
         * fastjson: 620ms
         * serial: 2003ms
         */
        calcTime(count, "new", this::newQueryWrapper);
        calcTime(count, "clone", queryWrapper::clone);
        calcTime(count, "fastjson", () -> SerialUtil.cloneObject(queryWrapper, queryWrapperClass));
        calcTime(count, "serial", () -> SerialUtil.cloneObject(queryWrapper));
    }

    @Test
    void test02() {
        QueryWrapper queryWrapper = newQueryWrapper();
        QueryWrapper queryWrapper1 = queryWrapper.clone();
        QueryWrapper queryWrapper2 = SerialUtil.cloneObject(queryWrapper);
//        QueryWrapper queryWrapper3 = SerialUtil.cloneObject(queryWrapper, QueryWrapper.class);
        System.err.println(SerialUtil.toJSONString(queryWrapper));
        System.out.println(queryWrapper.toSQL());
        System.out.println(queryWrapper1.toSQL());
        System.out.println(queryWrapper2.toSQL());
//        System.out.println(queryWrapper3.toSQL());

        Assertions.assertEquals(queryWrapper.toSQL(), queryWrapper1.toSQL());
        Assertions.assertEquals(queryWrapper.toSQL(), queryWrapper2.toSQL());
//        Assertions.assertEquals(queryWrapper.toSQL(), queryWrapper3.toSQL());
    }

    private void calcTime(int count, String type, Supplier<QueryWrapper> supplier) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            supplier.get();
        }
        long end = System.currentTimeMillis();
        System.out.println(type + ": " + (end - start) + "ms");
    }

    private QueryWrapper newQueryWrapper() {
        return QueryWrapper.create()
            .select(count(distinct(USER.USER_ID))/*case_()
                                .when(USER.USER_ID.eq(3)).then("x3")
                                .when(USER.USER_ID.eq(5)).then("x4")
                                .end(),
                        distinct(USER.USER_ID.add(4)),
                        USER.USER_NAME,
                        ROLE.ALL_COLUMNS*/)
            .from(USER.as("u"))
            .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
            .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
            .where(USER.USER_ID.eq(3))
            .and(ROLE.ROLE_NAME.in(Arrays.asList(1, 2, 3)))
            .or(ROLE.ROLE_ID.ge(USER.USER_ID))
            .groupBy(ROLE.ROLE_NAME)
            .having(ROLE.ROLE_ID.ge(7))
            .orderBy(ROLE.ROLE_NAME.asc());
    }

    @Test
    void test04() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(ACCOUNT)
            .select(ACCOUNT.DEFAULT_COLUMNS)
            .where(ACCOUNT.ID.eq(1));

        QueryWrapper clone = queryWrapper.clone();

        CPI.setSelectColumns(clone, null);

        clone.select(ACCOUNT.ID, ACCOUNT.USER_NAME);

        String sql1 = queryWrapper.toSQL();
        String sql2 = clone.toSQL();

        System.out.println(sql1);
        System.out.println(sql2);

        Assertions.assertNotEquals(sql1, sql2);
    }

}
