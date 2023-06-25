package com.mybatisflex.test.service;

import com.mybatisflex.test.mapper.AccountMapper;
import com.mybatisflex.test.model.Account;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Component
public class AccountService {


    @Resource
    AccountMapper accountMapper;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update2() {
        Account account = new Account();
        account.setId(2L);
        account.setUserName("haha");
        accountMapper.update(account);
    }
}
