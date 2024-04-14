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

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.model.OrderInfo;
import com.mybatisflex.test.model.User;
import com.mybatisflex.test.model.UserInfo;
import com.mybatisflex.test.model.UserVO;
import com.mybatisflex.test.model.UserVO1;
import com.mybatisflex.test.model.UserVO2;
import com.mybatisflex.test.model.UserVO3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.mybatisflex.test.model.table.GoodTableDef.GOOD;
import static com.mybatisflex.test.model.table.IdCardTableDef.ID_CARD;
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
@SuppressWarnings("all")
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
       // UserVO userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO.class);
       // UserVO1 userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO1.class);
       // UserVO2 userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO2.class);
        UserVO3 userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO3.class);
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
                .prevent(true)
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
    void testSelectListNoJoin() {
        List<User> users = userMapper.selectListByQueryAs(QueryWrapper.create(), User.class);
        users.forEach(System.err::println);
        List<UserVO> userVOS = userMapper.selectListByQueryAs(QueryWrapper.create(), UserVO.class);
        userVOS.forEach(System.err::println);
    }

    @Test
    void testComplexSelectList() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(USER.ALL_COLUMNS, ID_CARD.ID_NUMBER, ROLE.ALL_COLUMNS, ORDER.ALL_COLUMNS, GOOD.ALL_COLUMNS)
            .from(USER.as("u"))
            .leftJoin(ID_CARD).as("i").on(USER.USER_ID.eq(ID_CARD.ID))
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

    @Test
    void testComplexSelectListRelations() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(USER.ALL_COLUMNS, ID_CARD.ID_NUMBER)
            .from(USER.as("u"))
            .leftJoin(ID_CARD).as("i").on(USER.USER_ID.eq(ID_CARD.ID))
            .where(USER.USER_ID.eq(2));
//        List<UserInfo> userInfos = userMapper.selectListWithRelationsByQueryAs(queryWrapper, UserInfo.class);
//        userInfos.forEach(System.err::println);
        UserInfo userInfo = userMapper.selectOneWithRelationsByQueryAs(queryWrapper, UserInfo.class);
        System.out.println(userInfo);
    }

    @Test
    void testComplexSelectListFields() {
        List<UserInfo> userInfos = userMapper.selectListByQueryAs(QueryWrapper.create(), UserInfo.class,
            c -> c.field(UserInfo::getIdNumber).queryWrapper(userInfo ->
                QueryWrapper.create()
                    .select(ID_CARD.ID_NUMBER)
                    .from(ID_CARD)
                    .where(ID_CARD.ID.eq(userInfo.getUserId()))
            ),
            c -> c.field(UserInfo::getRoleList).prevent().queryWrapper(userInfo ->
                QueryWrapper.create()
                    .select()
                    .from(ROLE.as("r"))
                    .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
                    .where(USER_ROLE.USER_ID.eq(userInfo.getUserId()))
            ),
            c -> c.field(UserInfo::getOrderInfoList).queryWrapper(userInfo ->
                /*QueryWrapper.create()
                    .select()
                    .from(ORDER.as("o"))
                    .leftJoin(USER_ORDER).as("uo").on(USER_ORDER.ORDER_ID.eq(ORDER.ORDER_ID))
                    .where(USER_ORDER.USER_ID.eq(userInfo.getUserId()))*/
                null
            ),
            c -> c.nestedField(OrderInfo::getGoodList).prevent().queryWrapper(orderInfo ->
                QueryWrapper.create()
                    .select()
                    .from(GOOD.as("g"))
                    .leftJoin(ORDER_GOOD).as("og").on(ORDER_GOOD.GOOD_ID.eq(GOOD.GOOD_ID))
                    .where(ORDER_GOOD.ORDER_ID.eq(orderInfo.getOrderId()))
            )
        );
        userInfos.forEach(System.err::println);
    }

    @Test
    void testEquals() {
        QueryWrapper queryWrapper1 = QueryWrapper.create()
            .select(USER.ALL_COLUMNS, ID_CARD.ID_NUMBER, ROLE.ALL_COLUMNS, ORDER.ALL_COLUMNS, GOOD.ALL_COLUMNS)
            .from(USER.as("u"))
            .leftJoin(ID_CARD).as("i").on(USER.USER_ID.eq(ID_CARD.ID))
            .leftJoin(USER_ROLE).as("ur").on(USER.USER_ID.eq(USER_ROLE.USER_ID))
            .leftJoin(ROLE).as("r").on(ROLE.ROLE_ID.eq(USER_ROLE.ROLE_ID))
            .leftJoin(USER_ORDER).as("uo").on(USER.USER_ID.eq(USER_ORDER.USER_ID))
            .leftJoin(ORDER).as("o").on(ORDER.ORDER_ID.eq(USER_ORDER.ORDER_ID))
            .leftJoin(ORDER_GOOD).as("og").on(ORDER.ORDER_ID.eq(ORDER_GOOD.ORDER_ID))
            .leftJoin(GOOD).as("g").on(GOOD.GOOD_ID.eq(ORDER_GOOD.GOOD_ID))
            .orderBy(USER.USER_ID.asc(), ROLE.ROLE_ID.asc(), ORDER.ORDER_ID.asc(), GOOD.GOOD_ID.asc());
        List<UserInfo> userInfos1 = userMapper.selectListByQueryAs(queryWrapper1, UserInfo.class);

        QueryWrapper queryWrapper2 = QueryWrapper.create()
            .select(USER.ALL_COLUMNS, ID_CARD.ID_NUMBER)
            .from(USER.as("u"))
            .leftJoin(ID_CARD).as("i").on(USER.USER_ID.eq(ID_CARD.ID));
        List<UserInfo> userInfos2 = userMapper.selectListWithRelationsByQueryAs(queryWrapper2, UserInfo.class);

        List<UserInfo> userInfos3 = userMapper.selectListByQueryAs(QueryWrapper.create(), UserInfo.class,
            c -> c.field(UserInfo::getIdNumber).queryWrapper(userInfo ->
                QueryWrapper.create()
                    .select(ID_CARD.ID_NUMBER)
                    .from(ID_CARD)
                    .where(ID_CARD.ID.eq(userInfo.getUserId()))
            ),
            c -> c.field(UserInfo::getRoleList).prevent().queryWrapper(userInfo ->
                QueryWrapper.create()
                    .select()
                    .from(ROLE.as("r"))
                    .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
                    .where(USER_ROLE.USER_ID.eq(userInfo.getUserId()))
                    .orderBy(ROLE.ROLE_ID.asc())
            ),
            c -> c.field(UserInfo::getOrderInfoList).queryWrapper(userInfo ->
                QueryWrapper.create()
                    .select()
                    .from(ORDER.as("o"))
                    .leftJoin(USER_ORDER).as("uo").on(USER_ORDER.ORDER_ID.eq(ORDER.ORDER_ID))
                    .where(USER_ORDER.USER_ID.eq(userInfo.getUserId()))
                    .orderBy(ORDER.ORDER_ID.asc())
            ),
            c -> c.nestedField(OrderInfo::getGoodList).prevent().queryWrapper(orderInfo ->
                QueryWrapper.create()
                    .select()
                    .from(GOOD.as("g"))
                    .leftJoin(ORDER_GOOD).as("og").on(ORDER_GOOD.GOOD_ID.eq(GOOD.GOOD_ID))
                    .where(ORDER_GOOD.ORDER_ID.eq(orderInfo.getOrderId()))
                    .orderBy(GOOD.GOOD_ID.asc())
            )
        );

        Assertions.assertEquals(userInfos1.toString(), userInfos2.toString());
        Assertions.assertEquals(userInfos1.toString(), userInfos3.toString());
    }

    @Test
    void testCircularReference() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(USER.USER_ID, USER.USER_NAME, ROLE.ALL_COLUMNS)
            .from(USER.as("u"))
            .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
            .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
            .where(USER.USER_ID.eq(1));
        List<UserVO1> userVO1s = userMapper.selectListByQueryAs(queryWrapper, UserVO1.class);
        System.err.println(userVO1s);
    }

    @Test
    void testPage() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(USER.USER_ID, USER.USER_NAME, ROLE.ALL_COLUMNS)
            .from(USER.as("u"))
            .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
            .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID));
        System.err.println(queryWrapper.toSQL());
        Page<UserVO> page = new Page<>();
        page.setOptimizeCountQuery(false);
        int pageNumber = 0;
        do {
            page = userMapper.paginateAs(page, queryWrapper, UserVO.class);
            System.err.println(page);
            page.setPageNumber(page.getPageNumber() + 1);
        } while (page.hasNext());
    }

    @Test
    void testListString() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(USER.USER_ID,
                USER.USER_NAME,
                ROLE.ROLE_NAME.as("roles"),
                ROLE.ROLE_ID.as("role_ids"))
            .from(USER.as("u"))
            .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
            .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
            .where(USER.USER_ID.eq(2));
        UserVO2 user = userMapper.selectOneByQueryAs(queryWrapper, UserVO2.class);
        System.err.println(user);
        user = userMapper.selectOneByQueryAs(queryWrapper, UserVO2.class);
        System.err.println(user);
    }

//    @Test
//    public void testFieldBindRelations() {
//        List<UserVO5> userVO5List = userMapper.selectListWithRelationsByQueryAs(QueryWrapper.create(), UserVO5.class);
//        System.out.println(userVO5List);
//    }

}
