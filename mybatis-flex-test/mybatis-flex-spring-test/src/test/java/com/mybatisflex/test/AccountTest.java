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
public class AccountTest implements WithAssertions {

    @Autowired
    AccountMapper accountMapper;

    @Test
    public void testSelectOne() {
        Account account = accountMapper.selectOneById(1);
        assertThat(account).isNotNull()
            .satisfies(a -> assertThat(a.getId()).isEqualTo(1));
    }

    @Test
    public void testSelectByQuery() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(ACCOUNT.AGE.eq(18));
        List<Account> accounts = accountMapper.selectListByQuery(queryWrapper);
        assertThat(accounts.size()).isEqualTo(1);
        assertThat(accounts.get(0).getAge()).isEqualTo(18);
    }

    @Test
    public void testSelectOneByRow() {
        Row row = Db.selectOneById(null, "tb_account", "id", 1);
        System.out.println(row);
    }

    @Test
    public void testLambda() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select()
            .from(ACCOUNT)
            .where(Account::getAge).in(
                QueryWrapper.create().select(ACCOUNT.AGE).from(ACCOUNT).where(ACCOUNT.AGE.ge(18))
            );
        System.out.println(queryWrapper.toSQL());
    }

}
