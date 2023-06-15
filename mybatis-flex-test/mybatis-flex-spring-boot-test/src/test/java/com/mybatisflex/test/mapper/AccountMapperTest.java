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
import com.mybatisflex.test.model.Account;
import com.mybatisflex.test.model.AccountVO;
import com.mybatisflex.test.model.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static com.mybatisflex.test.model.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.model.table.RoleTableDef.ROLE;
import static com.mybatisflex.test.model.table.UserRoleTableDef.USER_ROLE;

/**
 * @author 王帅
 * @since 2023-06-13
 */
@SpringBootTest
@SuppressWarnings("all")
class AccountMapperTest {

    @Autowired
    private AccountMapper accountMapper;

    @Test
    void testInsert() {
        Account account = new Account();
        account.setBirthday(new Date());
        account.setUserName("张三");
        account.setAge(18);
        accountMapper.insert(account);
    }

    @Test
    void testUpdate() {
        Account account = new Account();
        account.setId(1L);
        account.setAge(58);
        accountMapper.update(account);
    }

    @Test
    void testDelete() {
        accountMapper.deleteById(1L);
    }

    @Test
    void testSelect() {
        accountMapper.selectListByQuery(QueryWrapper.create()).forEach(System.err::println);
    }

    @Test
    void testListString() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(ACCOUNT.ALL_COLUMNS, ROLE.ROLE_NAME.as("roles"))
                .from(ACCOUNT)
                .leftJoin(USER_ROLE).on(USER_ROLE.USER_ID.eq(ACCOUNT.ID))
                .leftJoin(ROLE).on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID));
        accountMapper.selectListByQuery(queryWrapper).forEach(System.err::println);
        accountMapper.selectListByQueryAs(queryWrapper, AccountVO.class).forEach(System.err::println);
    }

    @Test
    void testGenericEntity() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(ACCOUNT.ALL_COLUMNS, ROLE.ALL_COLUMNS)
                .from(ACCOUNT)
                .leftJoin(USER_ROLE).on(USER_ROLE.USER_ID.eq(ACCOUNT.ID))
                .leftJoin(ROLE).on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID));
        accountMapper.selectListByQueryAs(queryWrapper, AccountVO.class).forEach(System.err::println);
    }

    @Test
    void testEnum() {
        Account account = new Account();
        account.setId(1L);
        account.setGender(Gender.MALE);
        accountMapper.update(account);
    }

}