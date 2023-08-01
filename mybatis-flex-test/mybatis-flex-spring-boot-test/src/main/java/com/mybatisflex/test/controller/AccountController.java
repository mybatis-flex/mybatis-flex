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
package com.mybatisflex.test.controller;

import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.test.mapper.AccountMapper;
import com.mybatisflex.test.mapper.MyAccountMapper;
import com.mybatisflex.test.model.Account;
import com.mybatisflex.test.model.AccountDto;
import com.mybatisflex.test.service.AccountService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@UseDataSource("ds3333")
public class AccountController {

    @Resource
    AccountMapper accountMapper;


    @Resource
    MyAccountMapper myAccountMapper;


    @Resource
    AccountService accountService;


    @PostMapping("/account/add")
    String add(@RequestBody Account account) {
        accountMapper.insert(account);
        return "add ok!";
    }


    @GetMapping("/account/byName/{name}")
    Page<AccountDto> selectName(@PathVariable("name") String name) {
//        return myAccountMapper.selectByName(name);

        QueryWrapper qw = QueryWrapper.create()
            .where(Account::getAge).eq(18)
            .and(Account::getId).ge(0);

        Page<AccountDto> accountPage = myAccountMapper.xmlPaginate("selectByName", Page.of(1, 10), qw);
        return accountPage;
    }


    @GetMapping("/account/byId/{id}")
    Account selectId(@PathVariable("id") Object id) {
        return myAccountMapper.selectById(id);
    }


    @GetMapping("/account/{id}")
    @Transactional
    public Account selectOne(@PathVariable("id") Long id) {

//        Account account = new Account();
//        account.setId(1L);
//        account.setUserName("heihei");
//        accountMapper.update(account);

        Row row1 = Db.selectOneById(null, "tb_account", "id", 1);
        System.out.println(">>>>>>> row1: " + row1);

        Row row2 = Db.selectOneById(null, "tb_account", "id", 2);
        System.out.println(">>>>>>> row2: " + row2);

        Account account = new Account();
        account.setId(2L);
        account.setUserName("haha1111");
        accountMapper.update(account);

        //嵌套事务
        accountService.update2();

        return accountMapper.selectOneById(id);
    }


    @GetMapping("/all")
    List<Account> all() {
        return accountMapper.selectAll();
    }


    @GetMapping("/paginate")
    Page<Account> paginate(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
        return accountMapper.paginate(pageNumber, pageSize, QueryWrapper.create());
    }


    @GetMapping("/ds")
    @UseDataSource("ds2222")
    public String ds() {
        return ">>>>>ds: " + DataSourceKey.get();
    }

}
