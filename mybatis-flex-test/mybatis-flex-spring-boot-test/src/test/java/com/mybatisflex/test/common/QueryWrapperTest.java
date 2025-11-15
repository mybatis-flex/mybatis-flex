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

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.RawQueryTable;
import com.mybatisflex.test.model.table.RoleTableDef;
import com.mybatisflex.test.model.table.UserTableDef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        Assertions.assertEquals("SELECT /*+ hint */ `u`.`user_id`, `r`.* " +
                "FROM `tb_user` AS `u` " +
                "LEFT JOIN `tb_user_role` AS `ur` ON `u`.`user_id` = `ur`.`user_id` " +
                "LEFT JOIN `tb_role` AS `r` ON `r`.`role_id` = `ur`.`role_id` " +
                "WHERE `u`.`user_id` = 3 AND `u`.`user_id` = `r`.`role_id` OR `u`.`user_id` IN (4, 5, 6) " +
                "GROUP BY `r`.`role_key` " +
                "HAVING `r`.`role_id` = `ur`.`role_id` " +
                "ORDER BY `r`.`role_name` ASC"
            , queryWrapper.toSQL());

        System.out.println(queryWrapper.toSQL());


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

        Assertions.assertEquals("SELECT COUNT(DISTINCT `u`.`user_id`), " +
                "CASE WHEN `u`.`user_id` = 3 THEN 'x3' WHEN `u`.`user_id` = 5 " +
                "THEN 'x4' END, DISTINCT `u`.`user_id` + 4, `u`.`user_name`, `r`.* " +
                "FROM `tb_user` AS `u` " +
                "LEFT JOIN `tb_user_role` AS `ur` ON `ur`.`user_id` = `u`.`user_id` " +
                "LEFT JOIN `tb_role` AS `r` ON `ur`.`role_id` = `r`.`role_id` " +
                "WHERE `u`.`user_id` = 3 AND `r`.`role_name` IN (1, 2, 3) OR `r`.`role_id` >= `u`.`user_id` " +
                "GROUP BY `r`.`role_name` " +
                "HAVING `r`.`role_id` >= 7 " +
                "ORDER BY `r`.`role_name` ASC"
            , queryWrapper.toSQL());

        System.out.println(queryWrapper.toSQL());
    }

    @Test
    void test03() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(USER.as("u"))
            .leftJoin(USER_ROLE).as("ur").on(USER.USER_ID.eq(USER_ROLE.USER_ID))
            .where(QueryCondition.createEmpty())
            .and(USER.USER_ID.eq(1).or(USER.USER_ID.in(
                QueryWrapper.create().select(USER_ROLE.USER_ID).from(USER_ROLE)))
            )
            .and(USER_ROLE.USER_ID.eq(1));
        System.out.println(queryWrapper.toSQL());
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        boolean contained = CPI.containsTable(whereQueryCondition, "tb_user_role");

        Assertions.assertTrue(contained);
    }

    @Test
    void test04() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select("a.*")
            .from(new RawQueryTable("(select * from app)").as("a"));

        Assertions.assertEquals("SELECT a.* FROM (select * from app) AS `a`"
            , queryWrapper.toSQL());

        System.out.println(queryWrapper.toSQL());
    }

    @Test
    void test05() {
        RoleTableDef r = ROLE.as("r");
        UserTableDef u = USER.as("u");

        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(USER.USER_NAME)
            .from(USER)
            .leftJoin(u).on(u.USER_ID.eq(USER.USER_ID))
            .where(USER.USER_ID.eq(1))
            // 子查询里面用了父查询里面的表
            .and(column(select(r.ROLE_ID).from(r).where(u.USER_ID.eq(r.ROLE_ID))).le(2));

        String sql = SqlFormatter.format(queryWrapper.toSQL());
        System.out.println(sql);

        Assertions.assertEquals("SELECT\n" +
            "  ` tb_user `.` user_name `\n" +
            "FROM\n" +
            "  ` tb_user `\n" +
            "  LEFT JOIN ` tb_user ` AS ` u ` ON ` u `.` user_id ` = ` tb_user `.` user_id `\n" +
            "WHERE\n" +
            "  ` tb_user `.` user_id ` = 1\n" +
            "  AND (\n" +
            "    SELECT\n" +
            "      ` r `.` role_id `\n" +
            "    FROM\n" +
            "      ` tb_role ` AS ` r `\n" +
            "    WHERE\n" +
            "      ` u `.` user_id ` = ` r `.` role_id `\n" +
            "  ) <= 2", sql);
    }

    @Test
    void test06() {
        List<Integer> ids = Collections.emptyList();

        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_EMPTY);
        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(USER)
            .where(USER.USER_ID.in(ids, v -> !v.isEmpty()))
            .and(USER.USER_ID.eq(null, true))
            .and(USER.USER_NAME.eq("", true));
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NULL);

        String sql = SqlFormatter.format(queryWrapper.toSQL());
        System.out.println(sql);

        Assertions.assertEquals("SELECT\n" +
            "  *\n" +
            "FROM\n" +
            "  ` tb_user `\n" +
            "WHERE\n" +
            "  ` user_id ` = null\n" +
            "  AND ` user_name ` = ''", sql);
    }

    @Test
    void test07() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(USER.as("u"))
            .leftJoin(USER_ROLE).as("ur").on(USER.USER_ID.eq(USER_ROLE.USER_ID))
            .where(QueryCondition.createEmpty())
            .and(USER.USER_ID.eq(1).or(USER.USER_ID.in(
                QueryWrapper.create().select(USER_ROLE.USER_ID).from(USER_ROLE)))
            )
            .and(
                exists(selectOne().from(ROLE)
                    .where(ROLE.ROLE_ID.eq(USER_ROLE.ROLE_ID)))
            )
            .and(USER_ROLE.USER_ID.eq(1));
        System.out.println(queryWrapper.toSQL());
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        boolean contained = CPI.containsTable(whereQueryCondition, "tb_user_role");

        Assertions.assertTrue(contained);
    }

}
