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
import org.noear.solon.annotation.*;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.sql.SqlUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller
@UseDataSource("ds1")
public class AccountController {

    @Inject
    AccountMapper accountMapper;


    @Inject
    MyAccountMapper myAccountMapper;


    @Inject
    AccountService accountService;

    @Inject
    SqlUtils jdbcTemplate;


    @Post
    @Mapping("/account/add")
    @Tran
    public String add(@Body Account account) throws SQLException {
        Integer count1 = jdbcTemplate.sql("select count(*) from tb_account").queryValue();
        System.out.println(">>>>count1: " + count1);

        DataSourceKey.use("ds2");
        int update1 = jdbcTemplate.sql("INSERT INTO `flex_test`.`tb_account` ( `user_name`, `age`, `birthday`, `is_delete`) VALUES ( '王五', 18, '2023-07-04 15:00:26', 0)").update();
        System.out.println(">>>>>>>>>update1: " + update1);

        DataSourceKey.use("ds1");
        accountMapper.insert(account);

        DataSourceKey.use("ds2");
        accountMapper.insert(account);

//        if (true) {
//            throw new RuntimeException("ex");
//        }


        return "add ok!";
    }


    @Mapping("/account/byName/{name}")
    Page<AccountDto> selectName(@Path("name") String name) {
//        return myAccountMapper.selectByName(name);

        QueryWrapper qw = QueryWrapper.create()
            .where(Account::getAge).eq(18)
            .and(Account::getId).ge(0);

        Page<AccountDto> accountPage = myAccountMapper.xmlPaginate("selectByName", Page.of(1, 10), qw);
        return accountPage;
    }


    @Mapping("/account/byId/{id}")
    Account selectId(@Path("id") Object id) {
        return myAccountMapper.selectById(id);
    }


    @Mapping("/account/{id}")
    @Tran
    public Account selectOne(@Path("id") Long id) {

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

        try {
            //嵌套事务
            accountService.update2();
        }catch (Exception e){
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            System.out.println(">>>>>>e"+e);
        }


        return accountMapper.selectOneById(id);
    }


    @Mapping("/all")
    List<Account> all() {
        return accountMapper.selectAll();
    }


    @Mapping("/paginate")
    Page<Account> paginate(@Param(defaultValue = "1") int pageNumber, @Param(defaultValue = "10") int pageSize) {
        return accountMapper.paginate(pageNumber, pageSize, QueryWrapper.create());
    }


    @Mapping("/ds")
    @UseDataSource("ds2222")
    public String ds() {
        return ">>>>>ds: " + DataSourceKey.get();
    }


    @Mapping("/multids")
    @Tran
    @UseDataSource("ds1")
    public String multids(){
        Db.selectAll("tb_account");

        DataSourceKey.use("ds2");

        Db.selectAll("tb_account");

        Db.updateById("tb_account",Row.ofKey("id",1).set("user_name","newUserName"));
        return "ok";
    }



    @Mapping("/map/{id}")
//    @Transactional
    public Map map(@Path("id") Long id) {
        return myAccountMapper.selectMapById(id);
    }

}
