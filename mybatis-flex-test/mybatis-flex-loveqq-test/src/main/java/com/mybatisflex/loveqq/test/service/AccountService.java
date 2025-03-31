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

package com.mybatisflex.loveqq.test.service;

import com.kfyty.loveqq.framework.core.autoconfig.annotation.Service;
import com.mybatisflex.loveqq.test.mapper.AccountMapper;
import com.mybatisflex.loveqq.test.model.Account;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class AccountService {


    @Resource
    AccountMapper accountMapper;


    @Transactional
    public void update2() {
        int x = 1 / 0;
        Account account = new Account();
        account.setId(2L);
        account.setUserName("haha");
        accountMapper.update(account);
    }

    @Transactional(rollbackFor = Exception.class, timeout = 3)
    public void transactionTimeTest() throws InterruptedException {
        Account account = new Account();
        account.setId(100L);
        account.setUserName("aliothmoon");
        accountMapper.insert(account);
        TimeUnit.SECONDS.sleep(5);
        accountMapper.selectOneById(account.getId());
    }

}
