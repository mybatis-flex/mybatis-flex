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
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.mybatisflex.test.table.Tables.ACCOUNT;

public class EntityTestStarter {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
                .addMapper(AccountMapper.class)
                .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

//设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);

//        //查询 ID 为 1 的数据
//        Account account = bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.selectOneById(1));
//        System.out.println(account);

        AccountMapper accountMapper = bootstrap.getMapper(AccountMapper.class);
//        Account account = accountMapper.selectOneById(1);



//        QueryWrapper query = QueryWrapper.create().where(SYS_CONFIG.TYPE.eq(type).when(StrChecker.isNotBlank(type)))
//                .and(SYS_CONFIG.NAME.like(word).when(StrChecker.isNotBlank(word))
//                        .or(SYS_CONFIG.CODE.like(word).when(StrChecker.isNotBlank(word)))
//                        .or(SYS_CONFIG.VALUE.like(word).when(StrChecker.isNotBlank(word)))
//                        .or(SYS_CONFIG.TYPE.like(word).when(StrChecker.isNotBlank(word)))
//                );

        List<Account> accounts = accountMapper.selectListByQuery(
                select().where(ACCOUNT.AGE.ge(18).when(false))
                        .and(ACCOUNT.USER_NAME.like("aaaa").when(false)
                                .or(ACCOUNT.USER_NAME.like("aaaa").when(false))
                                .or(ACCOUNT.USER_NAME.like("aaaa").when(false))
                                .or(ACCOUNT.USER_NAME.like("aaaa").when(false))
                        )
        );
        System.out.println(accounts);
//
//        long l = accountMapper.selectCountByQuery(QueryWrapper.create());
//        System.out.println("count: "+ l);

//        System.out.println(account);
//
//        List<Account> allAccount = bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.selectListByQuery(QueryWrapper.create()));
//        System.out.println(allAccount);
//
//
//        Account newAccount = new Account();
//        newAccount.setUserName("lisi");
//        newAccount.setAge(18);
//        newAccount.setBirthday(new Date());
//        bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.insert(newAccount));
//
//        //新增后自动回填主键
//        System.out.println("newAccount.id >>>>>> " + newAccount.getId());
//
//
//        List<Account> newAccountList = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            Account insertAccount = new Account();
//            insertAccount.setUserName("new_user_" + i);
//            insertAccount.setAge(22);
//            insertAccount.setBirthday(new Date());
//            newAccountList.add(insertAccount);
//        }
//
//        //批量插入数据
//        bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.insertBatch(newAccountList));
//
//
//        bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.deleteById(1));
//
//
//        bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.deleteBatchByIds(Arrays.asList(1, 2, 3)));
//
//
//        Map<String, Object> where = new HashMap<>();
//        where.put("id", 2);
//        bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.deleteByMap(where));
//
//
//        Account updateAccount1 = UpdateEntity.wrap(Account.class);
//        updateAccount1.setId(5L);
//        updateAccount1.setUserName(null);
//        updateAccount1.setAge(60);
//        bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.update(updateAccount1, false));
//
//
//        Account updateAccount2 = UpdateEntity.wrap(Account.class);
//        updateAccount2.setId(6L);
//        updateAccount2.setAge(40);
//        bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.update(updateAccount2));
//
//
//        List<Account> allAccounts = bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.selectListByQuery(QueryWrapper.create()));
//        System.out.println(allAccounts); //count 5
//
//
//        //分页查询，第 2 页，每页 3 条数据
//        Page<Account> accountPage = bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.paginate(2, 3, QueryWrapper.create()));
//        System.out.println(accountPage);
//
//
//        Account optionsAccount = new Account();
//        optionsAccount.setUserName("optionstest");
//        optionsAccount.addOption("c1", 11);
//        optionsAccount.addOption("c2", "zhang");
//        optionsAccount.addOption("c3", new Date());
//
//
//        bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.insert(optionsAccount));
//        System.out.println(">>>>>>> optionsAccount: " + optionsAccount.getId());
//
//
//        Account selectOptionsAccount = bootstrap.execute(AccountMapper.class, accountMapper ->
//                accountMapper.selectOneById(optionsAccount.getId()));
//        System.out.println(selectOptionsAccount);

    }
}
