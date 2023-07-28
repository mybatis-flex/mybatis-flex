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

package com.mybatisflex.test.mapper;

import com.mybatisflex.core.mybatis.Mappers;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.model.Good;
import com.mybatisflex.test.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.mybatisflex.test.model.table.GoodTableDef.GOOD;
import static com.mybatisflex.test.model.table.RoleTableDef.ROLE;
import static com.mybatisflex.test.model.table.UserRoleTableDef.USER_ROLE;
import static com.mybatisflex.test.model.table.UserTableDef.USER;

/**
 * @author 王帅
 * @since 2023-07-23
 */
@SpringBootTest
class ActiveRecordTest {

    @Test
    void testMapper() {
        Good good = Good.create();

        good.setPrice(28.0);

        GoodMapper goodMapper = (GoodMapper) Mappers.ofEntityClass(Good.class);

        goodMapper.selectListByQuery(QueryWrapper.create(good));
    }

    @Test
    void testInsert() {
        boolean saved = Good.create()
            .setPrice(28.0)
            .setName("摆渡人")
            .save();

        Assertions.assertTrue(saved);
    }

    @Test
    void testUpdate() {
        Good.create()
            .setGoodId(11)
            .setPrice(38.0)
            .updateById();
    }

    @Test
    void testDelete() {
        boolean removed = Good.create()
            .setGoodId(1)
            .removeById();

        Assertions.assertTrue(removed);
    }

    @Test
    void testSelectById() {
        Good good = Good.create()
            .setGoodId(11)
            .oneById();

        System.out.println(good);
    }

    @Test
    void testSelectOne() {
        Good good1 = Good.create()
            .setName("摆渡人")
            .one();

        Good good2 = Good.create()
            .where(GOOD.NAME.eq("摆渡人"))
            .one();

        Good good3 = Good.create()
            .where(Good::getName).eq("摆渡人")
            .one();

        Assertions.assertEquals(good1, good2);
        Assertions.assertEquals(good1, good3);
    }

    @Test
    void testSelectList() {
        Good.create()
            .where(GOOD.PRICE.ge(28.0))
            .list()
            .forEach(System.out::println);
    }

    @Test
    void testRelation() {
        User user1 = User.create().select(USER.ALL_COLUMNS, ROLE.ALL_COLUMNS)
            .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
            .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
            .where(USER.USER_ID.eq(2))
            .one();

        User user2 = User.create()
            .where(USER.USER_ID.eq(2))
            .oneWithRelations();

        Assertions.assertEquals(user1.toString(), user2.toString());
    }

}
