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
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.test.model.Account;
import com.mybatisflex.test.model.AccountVO;
import com.mybatisflex.test.model.AccountVO2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static com.mybatisflex.core.query.QueryMethods.*;
import static com.mybatisflex.test.model.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.model.table.RoleTableDef.ROLE;
import static com.mybatisflex.test.model.table.UserRoleTableDef.USER_ROLE;
import static com.mybatisflex.test.model.table.UserTableDef.USER;

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
    void testCount() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .groupBy(ACCOUNT.AGE);

        long count = accountMapper.selectCountByQuery(queryWrapper);

        Assertions.assertEquals(2, count);

        queryWrapper = QueryWrapper.create()
            .select(distinct(ACCOUNT.AGE))
            .from(ACCOUNT);

        count = accountMapper.selectCountByQuery(queryWrapper);

//        Assertions.assertEquals(2, count);
    }

    /**
     * 测试db执行的情况下, sql日志打印情况
     */
    @Test
    void testDbSqlLogger() {
        QueryWrapper wrapper = QueryWrapper.create()
            .select(ACCOUNT.ALL_COLUMNS)
            .from(ACCOUNT);

        Db.selectOneByQuery(wrapper);
    }

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
//        account.setGender(Gender.MALE);
        accountMapper.update(account);
    }

    @Test
    void testSelectList() {
        List<Account> accounts = accountMapper.selectListByQuery(null);
        System.out.println(accounts);
        List<Row> account = Db.selectListByQuery("tb_account", null);
        System.out.println(account);
    }

    @Test
    void testUpdateAll() {
        Account account = new Account();
        account.setAge(10);
        Assertions.assertThrows(Exception.class, () ->
            accountMapper.updateByQuery(account, QueryWrapper.create()));
    }

    @Test
    void testDeleteAll() {
        Assertions.assertThrows(Exception.class, () ->
            accountMapper.deleteByQuery(QueryWrapper.create()));
    }

    @Test
    void testAs() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ID,
                ACCOUNT.AGE,
                USER.USER_ID,
                USER.USER_NAME)
            .from(ACCOUNT.as("a"), USER.as("u"))
            .where(ACCOUNT.ID.eq(1))
            .limit(1);
        AccountVO2 account = accountMapper.selectOneByQueryAs(queryWrapper, AccountVO2.class);
        System.out.println(account);
    }

    @Test
    void testAs0() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ID.as("account_id"),
                ACCOUNT.AGE,
                concat(column("'account name: '"), ACCOUNT.USER_NAME).as("user_name"),
                USER.USER_ID,
                concat(column("'user name: '"), USER.USER_NAME).as("1_account_name"))
            .from(ACCOUNT.as("a"), USER.as("u"))
            .where(ACCOUNT.ID.eq(1))
            .limit(1);
        AccountVO2 account = accountMapper.selectOneByQueryAs(queryWrapper, AccountVO2.class);
        System.out.println(account);
    }

    @Test
    void testAs1() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ID.as(AccountVO2::getId),
                ACCOUNT.AGE,
                concat(column("'account name: '"), ACCOUNT.USER_NAME).as(AccountVO2::getUserName),
                USER.USER_ID,
                concat(column("'user name: '"), USER.USER_NAME).as("1_account_name"))
            .from(ACCOUNT.as("a"), USER.as("u"))
            .where(ACCOUNT.ID.eq(1))
            .limit(1);
        AccountVO2 account = accountMapper.selectOneByQueryAs(queryWrapper, AccountVO2.class);
        System.out.println(account);
    }

    @Test
    void testIgnoreColumn() {
        accountMapper.selectListByQuery(QueryWrapper.create());
    }

}
