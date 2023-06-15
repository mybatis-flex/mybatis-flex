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

import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.mybatisflex.core.query.QueryMethods.*;
import static com.mybatisflex.test.model.table.RoleTableDef.ROLE;
import static com.mybatisflex.test.model.table.UserRoleTableDef.USER_ROLE;
import static com.mybatisflex.test.model.table.UserTableDef.USER;

/**
 * @author 王帅
 * @since 2023-06-12
 */
class QueryWrapperTest {

    @Test
    void test01() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(USER.USER_ID, ROLE.ALL_COLUMNS)
                .hint("hint")
                .from(USER.as("u"))
                .leftJoin(USER_ROLE).as("ur").on(USER.USER_ID.eq(USER_ROLE.USER_ID))
                .leftJoin(ROLE).as("r").on(ROLE.ROLE_ID.eq(USER_ROLE.ROLE_ID))
                .where(USER.USER_ID.eq(3))
                .and(USER.USER_ID.eq(ROLE.ROLE_ID))
                .or(USER.USER_ID.in(4, 5, 6))
                .groupBy(ROLE.ROLE_KEY)
                .having(ROLE.ROLE_ID.eq(USER_ROLE.ROLE_ID))
                .orderBy(ROLE.ROLE_NAME.asc());
        System.out.println(queryWrapper.toSQL());
    }

    @Test
    void test02() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(count(distinct(USER.USER_ID)), case_()
                                .when(USER.USER_ID.eq(3)).then("x3")
                                .when(USER.USER_ID.eq(5)).then("x4")
                                .end(),
                        distinct(USER.USER_ID.add(4)),
                        USER.USER_NAME,
                        ROLE.ALL_COLUMNS)
                .from(USER.as("u"))
                .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
                .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
                .where(USER.USER_ID.eq(3))
                .and(ROLE.ROLE_NAME.in(Arrays.asList(1, 2, 3)))
                .or(ROLE.ROLE_ID.ge(USER.USER_ID))
                .groupBy(ROLE.ROLE_NAME)
                .having(ROLE.ROLE_ID.ge(7))
                .orderBy(ROLE.ROLE_NAME.asc());
        System.out.println(queryWrapper.toSQL());
    }

}