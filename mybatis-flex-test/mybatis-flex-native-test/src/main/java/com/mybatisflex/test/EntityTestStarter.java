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
package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.querywrapper.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.*;

public class EntityTestStarter {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
                .setDataSource(dataSource)
                .setLogImpl(StdOutImpl.class)
                .addMapper(AccountMapper.class)
                .start();


//        //查询 ID 为 1 的数据
        Account account = bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.selectOneById(1));
        System.out.println(account);


        Account newAccount = new Account();
        newAccount.setUserName("lisi");
        newAccount.setAge(18);
        newAccount.setBirthday(new Date());
        bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.insert(newAccount));

        //新增后自动回填主键
        System.out.println("newAccount.id >>>>>> " + newAccount.getId());


        List<Account> newAccountList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Account insertAccount = new Account();
            insertAccount.setUserName("new_user_" + i);
            insertAccount.setAge(22);
            insertAccount.setBirthday(new Date());
            newAccountList.add(insertAccount);
        }

        //批量插入数据
        bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.insertBatch(newAccountList));


        bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.deleteById(1));


        bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.deleteBatchByIds(Arrays.asList(1, 2, 3)));


        Map<String, Object> where = new HashMap<>();
        where.put("id", 2);
        bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.deleteByMap(where));


        Account updateAccount1 = UpdateEntity.wrap(Account.class);
        updateAccount1.setId(5L);
        updateAccount1.setUserName(null);
        updateAccount1.setAge(60);
        bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.update(updateAccount1, false));


        Account updateAccount2 = UpdateEntity.wrap(Account.class);
        updateAccount2.setId(6L);
        updateAccount2.setAge(40);
        bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.update(updateAccount2));


        List<Account> allAccounts = bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.selectListByQuery(QueryWrapper.create()));
        System.out.println(allAccounts); //count 5


        //分页查询，第 2 页，每页 3 条数据
        Page<Account> accountPage = bootstrap.execute(AccountMapper.class, accountMapper ->
                accountMapper.paginate(2, 3, QueryWrapper.create()));
        System.out.println(accountPage);

    }
}
