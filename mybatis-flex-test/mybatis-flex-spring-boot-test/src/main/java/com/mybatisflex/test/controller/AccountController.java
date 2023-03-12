/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.test.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.mapper.AccountMapper;
import com.mybatisflex.test.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    @Autowired
    AccountMapper accountMapper;


    @PostMapping("/account/add")
    String add(@RequestBody Account account){
        accountMapper.insert(account);
        return "add ok!";
    }


    @GetMapping("/account/{id}")
    Account selectOne(@PathVariable("id") Long id) {
        return accountMapper.selectOneById(id);
    }


    @GetMapping("/selectListByIds/{id}")
    List<Account> selectListByIds(@PathVariable("id") String id) {
        List<Long> ids = Arrays.stream(id.split(",")).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
        return accountMapper.selectListByIds(ids);
    }


    @GetMapping("/paginate")
    Page<Account> paginate(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "10")  int pageSize) {
        return accountMapper.paginate(pageNumber,pageSize, QueryWrapper.create());
    }
}
