/*
 *  Copyright 1999-2021 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package mybatisflex.test.acount.accountservice.service;

import java.math.BigDecimal;
import mybatisflex.test.acount.accountservice.persistence.Account;
import mybatisflex.test.acount.accountservice.persistence.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private static final String ERROR_USER_ID = "1002";

    @Autowired
    private AccountMapper accountMapper;

    public void debit(String userId, BigDecimal num) {
        Account account = accountMapper.selectByUserId(userId);
        account.setMoney(account.getMoney().subtract(num));
        accountMapper.updateById(account);

        if (ERROR_USER_ID.equals(userId)) {
            throw new RuntimeException("account branch exception");
        }
    }

}
