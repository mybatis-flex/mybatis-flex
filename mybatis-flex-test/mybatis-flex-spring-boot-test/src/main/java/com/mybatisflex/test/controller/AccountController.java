package com.mybatisflex.test.controller;

import com.mybatisflex.test.mapper.AccountMapper;
import com.mybatisflex.test.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

//@RequestMapping("/accounts")
@RestController
public class AccountController {

    @Autowired
    AccountMapper accountMapper;

    @GetMapping("/account/{id}")
    Account selectOne(@PathVariable("id") Long id) {
        return accountMapper.selectOneById(id);
    }
}
