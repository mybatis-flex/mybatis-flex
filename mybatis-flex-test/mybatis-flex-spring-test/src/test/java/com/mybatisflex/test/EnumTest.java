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

package com.mybatisflex.test;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.test.mapper.AccountMapper;
import com.mybatisflex.test.model.Account;
import com.mybatisflex.test.model.Sex;
import com.mybatisflex.test.model.TbClass;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.mybatisflex.test.tabledef.Tables.ACCOUNT;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class EnumTest implements WithAssertions {

    @Autowired
    AccountMapper accountMapper;

    @Test
    public void test_global_enum_type_handler() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(ACCOUNT).where(ACCOUNT.SEX.in(Sex.MALE, Sex.FEMALE));
        // 注意，此处虽然显示是MALE、FEMALE，但是如果使用了枚举的typeHandler，实际sql执行时会使用typeHandler进行处理
        assertThat(query.toSQL()).isEqualTo("SELECT * FROM `tb_account` WHERE `sex` IN ('MALE', 'FEMALE')");
        List<Account> list = accountMapper.selectListByQuery(query);
        assertThat(list).isNotNull();
        for (Account account : list) {
            assertThat(account.getSex()).isIn(Sex.MALE, Sex.FEMALE);
        }
    }
    @Test
    public void test_create_entity_with_enum_type() {
        Account account = new Account();
        account.setSex(Sex.MALE);
        QueryWrapper queryWrapper = QueryWrapper.create(account);
        String sql = queryWrapper.toSQL();
        System.out.println(sql);
        // 注意，此处虽然显示是MALE，但是如果使用了枚举的typeHandler，实际sql执行时会使用typeHandler进行处理
        assertThat(sql).isEqualTo("SELECT `id`, `user_name`, `age`, `birthday`, `sex` FROM `tb_account` WHERE `sex` = 'MALE'");
        List<Account> list = accountMapper.selectListByQuery(queryWrapper);
        assertThat(list).isNotNull();
        for (Account item : list) {
            assertThat(item.getSex()).isEqualTo(Sex.MALE);
        }
    }

}
