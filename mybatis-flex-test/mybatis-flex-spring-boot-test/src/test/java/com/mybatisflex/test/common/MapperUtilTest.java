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
import com.mybatisflex.core.util.MapperUtil;
import org.junit.jupiter.api.Test;

import static com.mybatisflex.test.model.table.RoleTableDef.ROLE;
import static com.mybatisflex.test.model.table.UserRoleTableDef.USER_ROLE;
import static com.mybatisflex.test.model.table.UserTableDef.USER;

/**
 * @author 王帅
 * @since 2023-06-09
 */
class MapperUtilTest {

    @Test
    void testOptimizeCountQueryWrapper() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(USER.USER_ID, USER.USER_NAME, ROLE.ALL_COLUMNS)
            .from(USER.as("u"))
            .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
            .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
            .where(USER.USER_ID.eq(3))
            .and(USER_ROLE.ROLE_ID.eq(6))
            .groupBy(ROLE.ROLE_ID);
        System.out.println(queryWrapper.toSQL());
        System.out.println(MapperUtil.rawCountQueryWrapper(queryWrapper).toSQL());
        System.out.println(MapperUtil.optimizeCountQueryWrapper(queryWrapper).toSQL());
    }

}
