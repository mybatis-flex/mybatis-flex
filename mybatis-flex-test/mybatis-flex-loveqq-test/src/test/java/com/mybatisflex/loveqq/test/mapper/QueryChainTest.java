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

package com.mybatisflex.loveqq.test.mapper;

import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.mybatisflex.core.field.QueryBuilder;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.loveqq.test.LoveqqExtension;
import com.mybatisflex.loveqq.test.model.Gender;
import com.mybatisflex.loveqq.test.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.mybatisflex.loveqq.test.model.table.RoleTableDef.ROLE;
import static com.mybatisflex.loveqq.test.model.table.UserRoleTableDef.USER_ROLE;
import static com.mybatisflex.loveqq.test.model.table.UserTableDef.USER;

/**
 * @author 王帅
 * @since 2023-08-08
 */
@Component
@ExtendWith(LoveqqExtension.class)
class QueryChainTest {

    @Autowired
    UserMapper userMapper;

    @Test
    void testFields() {
        QueryBuilder<User> builder = user ->
            QueryWrapper.create()
                .select()
                .from(ROLE)
                .where(ROLE.ROLE_ID.in(
                    QueryWrapper.create()
                        .select(USER_ROLE.ROLE_ID)
                        .from(USER_ROLE)
                        .where(USER_ROLE.USER_ID.eq(user.getUserId()))
                ));

        User user1 = QueryChain.of(userMapper)
            .where(USER.USER_ID.eq(1))
            .withFields()
            .fieldMapping(User::getRoleList, builder)
            .one();

        User user2 = User.create()
            .where(USER.USER_ID.eq(1))
            .withFields()
            .fieldMapping(User::getRoleList, builder)
            .one();

        Assertions.assertEquals(user1.toString(), user2.toString());
    }

    @Test
    void testRelations() {
        User user1 = QueryChain.of(userMapper)
            .where(USER.USER_ID.eq(2))
            .withRelations()
            .one();

        User user2 = User.create()
            .where(USER.USER_ID.eq(2))
            .withRelations()
            .one();

        Assertions.assertEquals(user1.toString(), user2.toString());
    }

    @Test
    void testToSql() {
        String sql = UpdateChain.create(userMapper)
            .set(USER.USER_NAME, "张三")
            .set(User::getUserId, Gender.MALE)
            .where(USER.USER_ID.eq(1))
            .toSQL();

        System.out.println(sql);
    }

}
