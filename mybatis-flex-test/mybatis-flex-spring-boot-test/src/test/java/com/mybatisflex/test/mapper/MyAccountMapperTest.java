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

import com.mybatisflex.test.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author 庄佳彬
 * @since 2023/4/24 19:37
 */
@SpringBootTest
class MyAccountMapperTest {

    @Autowired
    private MyAccountMapper mapper;

    @Test
    void insertBatch() {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Account account = new Account();
            account.setBirthday(new Date());
            account.setAge(i % 60);
            account.setUserName(String.valueOf(i));
            accounts.add(account);
        }
        //删除初始化数据
        mapper.deleteById(1);
        mapper.deleteById(2);
        try {
            mapper.insertBatch(accounts);
        } catch (Exception e) {
            System.out.println("异常");
        }
        int i = mapper.insertBatch(accounts, 1000);
        assertEquals(10, i);
    }

}
