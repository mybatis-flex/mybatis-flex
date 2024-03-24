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

package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.coretest.auth.AuthDialectImpl;
import com.mybatisflex.coretest.user.User;
import org.junit.Before;
import org.junit.Test;

/**
 * null值策略测试
 *
 * @author Lighfer
 * @since 2024/03/24
 */
public class NullStrategyTest {
    private IDialect dialect;

    @Before
    public void init() {
        DialectFactory.registerDialect(DbType.MYSQL, new AuthDialectImpl());
        dialect = DialectFactory.getDialect();
    }

    @Test
    public void test() {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(User.class);

        assert "INSERT INTO `t_user`(`name`, `created_date`, `never_ignore`) VALUES (?, ?, ?)"
            .equals(dialect.forInsertEntity(tableInfo, new User("flex"), true));
        assert "INSERT INTO `t_user`(`created_date`, `never_ignore`) VALUES (?, ?)"
            .equals(dialect.forInsertEntity(tableInfo, new User(), true));

        System.out.println(dialect.forUpdateEntity(tableInfo, new User("flex"), true));
        assert "UPDATE `t_user` SET `name` = ? , `modified_date` = ? , `never_ignore` = ?  WHERE `id` = ? "
            .equals(dialect.forUpdateEntity(tableInfo, new User("flex"), true));
        assert "UPDATE `t_user` SET `modified_date` = ? , `never_ignore` = ?  WHERE `id` = ? "
            .equals(dialect.forUpdateEntity(tableInfo, new User(), true));
    }
}
