/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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

    /**
     * 测试 (sql1) union (sql2)
     */
    @Test
    void testOptimizeCountQueryWrapperOfUnion1() {
        //简单union
        //SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_id` = 1 order by user_id desc
        QueryWrapper union1 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.USER_ID.eq(1)).orderBy(USER.USER_ID.desc());
        //SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_name` LIKE '%test%'
        QueryWrapper union2 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.USER_NAME.like("test"));

        QueryWrapper query1 = union1.union(union2);

        String sql = MapperUtil.optimizeCountQueryWrapper(query1).toSQL();
        //SELECT COUNT(*) AS `total` FROM ((SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_id` = 1) UNION (SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_name` LIKE '%test%')) AS `t`
        System.out.println(sql);
    }

    /**
     * 测试 (sql1 ) union (sql2 with group by)
     */
    @Test
    void testOptimizeCountQueryWrapperOfUnion2() {
        //with group by union
        //SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_id` = 1 order by user_id desc
        QueryWrapper union1 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.USER_ID.eq(1)).orderBy(USER.USER_ID.desc());
        //SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_name` LIKE '%test%' group by user_id, user_name
        QueryWrapper union2 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.USER_NAME.like("test")).groupBy(USER.USER_ID, USER.USER_NAME);

        QueryWrapper query1 = union1.union(union2);

        //SELECT COUNT(*) AS `total` FROM ((SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_id` = 1) UNION (SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_name` LIKE '%test%' GROUP BY `user_id`, `user_name`)) AS `t`
        String sql = MapperUtil.optimizeCountQueryWrapper(query1).toSQL();
        System.out.println(sql);
    }

    /**
     * 测试 (sql1) union (sql2 union sql3)
     */
    @Test
    void testOptimizeCountQueryWrapperOfUnion3() {
        //with sub query union
        //SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_id` = 1 order by user_id desc
        QueryWrapper union1 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.USER_ID.eq(1)).orderBy(USER.USER_ID.desc());
        //SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_name` LIKE '%test%' group by user_id, user_name
        QueryWrapper union2 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.USER_NAME.like("test")).orderBy(USER.USER_NAME.desc());

        QueryWrapper union3 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.PASSWORD.isNull()).orderBy(USER.USER_NAME.desc());


        QueryWrapper query1 = union1.union(union2.union(union3));

        //SELECT COUNT(*) AS `total` FROM ((SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_id` = 1) UNION ((SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_name` LIKE '%test%') UNION (SELECT `user_id`, `user_name` FROM `tb_user` WHERE `password` IS NULL ))) AS `t`
        String sql = MapperUtil.optimizeCountQueryWrapper(query1).toSQL();

        System.out.println(sql);
    }

    @Test
    void testOptimizeCountQueryWrapperOfUnion4() {
        //with sub query union
        //SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_id` = 1 order by user_id desc
        QueryWrapper union1 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.USER_ID.eq(1)).orderBy(USER.USER_ID.desc());
        //SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_name` LIKE '%test%' group by user_id, user_name
        QueryWrapper union2 = QueryWrapper.create().select(USER.USER_ID, USER.USER_NAME).from(USER).where(USER.USER_NAME.like("test")).orderBy(USER.USER_NAME.desc());

        QueryWrapper union3 = QueryWrapper.create().from(union2).as("a");


        QueryWrapper query1 = union1.union(union3);

        //SELECT COUNT(*) AS `total` FROM ((SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_id` = 1) UNION ((SELECT `user_id`, `user_name` FROM `tb_user` WHERE `user_name` LIKE '%test%') UNION (SELECT `user_id`, `user_name` FROM `tb_user` WHERE `password` IS NULL ))) AS `t`
        String sql = MapperUtil.optimizeCountQueryWrapper(query1).toSQL();

        System.out.println(sql);
    }

}
