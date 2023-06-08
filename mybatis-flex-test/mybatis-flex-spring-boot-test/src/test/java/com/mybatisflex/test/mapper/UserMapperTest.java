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

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.model.UserInfo;
import com.mybatisflex.test.model.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.mybatisflex.test.model.table.GoodTableDef.GOOD;
import static com.mybatisflex.test.model.table.OrderGoodTableDef.ORDER_GOOD;
import static com.mybatisflex.test.model.table.OrderTableDef.ORDER;
import static com.mybatisflex.test.model.table.RoleTableDef.ROLE;
import static com.mybatisflex.test.model.table.UserOrderTableDef.USER_ORDER;
import static com.mybatisflex.test.model.table.UserRoleTableDef.USER_ROLE;
import static com.mybatisflex.test.model.table.UserTableDef.USER;

/**
 * @author 王帅
 * @since 2023-06-07
 */
@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelectOne() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(USER.USER_ID, USER.USER_NAME, ROLE.ALL_COLUMNS)
                .from(USER.as("u"))
                .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
                .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
                .where(USER.USER_ID.eq(3));
        System.out.println(queryWrapper.toSQL());
        UserVO userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO.class);
//        UserVO1 userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO1.class);
//        UserVO2 userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO2.class);
//        UserVO3 userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO3.class);
        System.err.println(userVO);
    }

    @Test
    void testFieldQuery() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(USER.USER_ID, USER.USER_NAME)
                .from(USER.as("u"))
                .where(USER.USER_ID.eq(3));
        System.out.println(queryWrapper.toSQL());
        List<UserVO> userVOs = userMapper.selectListByQueryAs(queryWrapper, UserVO.class,
                fieldQueryBuilder -> fieldQueryBuilder
                        .field(UserVO::getRoleList)
                        .queryWrapper(user -> QueryWrapper.create()
                                .select()
                                .from(ROLE)
                                .where(ROLE.ROLE_ID.in(
                                                select(USER_ROLE.ROLE_ID)
                                                        .from(USER_ROLE)
                                                        .where(USER_ROLE.USER_ID.eq(user.getUserId())
                                                        )
                                        )
                                )
                        )
        );
        System.err.println(userVOs);
    }

    @Test
    void testSelectList() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(USER.USER_ID, USER.USER_NAME, ROLE.ALL_COLUMNS)
                .from(USER.as("u"))
                .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
                .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
                .where(USER.USER_ID.eq(3));
        System.out.println(queryWrapper.toSQL());
        List<UserVO> userVOS = userMapper.selectListByQueryAs(queryWrapper, UserVO.class);
        userVOS.forEach(System.err::println);
    }

    @Test
    void testComplexSelectList() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(USER.ALL_COLUMNS, ROLE.ALL_COLUMNS, ORDER.ALL_COLUMNS, GOOD.ALL_COLUMNS)
                .from(USER.as("u"))
                .leftJoin(USER_ROLE).as("ur").on(USER.USER_ID.eq(USER_ROLE.USER_ID))
                .leftJoin(ROLE).as("r").on(ROLE.ROLE_ID.eq(USER_ROLE.ROLE_ID))
                .leftJoin(USER_ORDER).as("uo").on(USER.USER_ID.eq(USER_ORDER.USER_ID))
                .leftJoin(ORDER).as("o").on(ORDER.ORDER_ID.eq(USER_ORDER.ORDER_ID))
                .leftJoin(ORDER_GOOD).as("og").on(ORDER.ORDER_ID.eq(ORDER_GOOD.ORDER_ID))
                .leftJoin(GOOD).as("g").on(GOOD.GOOD_ID.eq(ORDER_GOOD.GOOD_ID));
        System.err.println(queryWrapper.toSQL());
        List<UserInfo> userInfos = userMapper.selectListByQueryAs(queryWrapper, UserInfo.class);
        userInfos.forEach(System.err::println);
    }

}