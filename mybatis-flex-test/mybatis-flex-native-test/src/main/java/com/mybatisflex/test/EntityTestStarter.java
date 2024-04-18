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
package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.query.If;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.RowUtil;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.table.ArticleTableDef.ARTICLE;

public class EntityTestStarter {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(AccountMapper.class)
            .addMapper(MyAccountMapper.class)
            .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);


        AccountMapper accountMapper = bootstrap.getMapper(AccountMapper.class);

        List<Account> accounts = accountMapper.selectAll();
        System.out.println(accounts);

//        QueryWrapper wrapper = QueryWrapper.create().select(ACCOUNT.ID
//                , case_().when(ACCOUNT.ID.ge(2)).then("x2")
//                        .when(ACCOUNT.ID.ge(1)).then("x1")
//                        .else_("x100")
//                        .end().as("xName")
//        ).from(ACCOUNT);
//
//        List<Row> rowList = Db.selectListByQuery(wrapper);
//        RowUtil.printPretty(rowList);
//
//
//        accountMapper.updateNumberAddByQuery("age", 100, QueryWrapper.create().where(ACCOUNT.ID.eq(1)));
//        accountMapper.updateNumberAddByQuery(Account::getAge, -50, QueryWrapper.create().where(ACCOUNT.ID.eq(1)));
//
//
//        Db.updateNumberAddByQuery("tb_account", "age", 30, QueryWrapper.create().where(ACCOUNT.ID.eq(1)));
//        Db.updateNumberAddByQuery("tb_account", "age", -20, QueryWrapper.create().where(ACCOUNT.ID.eq(1)));
//
//
//        List<Account> accounts1 = accountMapper.selectListByQuery(QueryWrapper.create()
//                , accountFieldQueryBuilder -> accountFieldQueryBuilder
//                        .field(Account::getArticles)
//                        .queryWrapper(entity ->
//                                select().from(ARTICLE).where(ARTICLE.ACCOUNT_ID.eq(entity.getId()))
//                        )
//        );
//        System.out.println(accounts1);

//        MyAccountMapper myAccountMapper = bootstrap.getMapper(MyAccountMapper.class);

//        List<Account> accounts1 = myAccountMapper.selectAll();

//        QueryWrapper wrapper = QueryWrapper.create().select().from(ACCOUNT)
//                .and(ACCOUNT.ID.ge(100).and(ACCOUNT.ID.ge(200)))
//                .and(ACCOUNT.ID.ge(100).and(ACCOUNT.ID.ge(200)))
//                .groupBy(ACCOUNT.ID);
//
//        List<Account> accounts = accountMapper.selectListByQuery(wrapper);
//        System.out.println(accounts);
//
//        QueryWrapper wrapper1 = QueryWrapper.create().select().from(ACCOUNT)
////                .leftJoin(ARTICLE).on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID).and(ACCOUNT.ID.ge(100)))
//                .and(ACCOUNT.ID.ge(100).when(false).and(ACCOUNT.ID.ge(100).when(false)));
//
//        Page<Account> accounts1 = accountMapper.paginate(Page.of(1,1),wrapper1);
//        System.out.println(accounts1);
//
//
//        QueryWrapper wrapper2 = QueryWrapper.create().select(ACCOUNT.ID).from(ACCOUNT);
//        List<Object> objects = accountMapper.selectObjectListByQuery(wrapper2);
//        System.out.println(objects);
//
//
//        Object object = accountMapper.selectObjectByQuery(wrapper2);
//        System.out.println(object);
//

        QueryWrapper query1 = QueryWrapper.create();
        query1.where(Account::getId).ge(100)
            .and(Account::getUserName).like("michael")
            .or(Account::getUserName).like(null, If::notNull);
        System.out.println(query1.toSQL());

        QueryWrapper query = QueryWrapper.create()
//                .select(ARTICLE.ALL_COLUMNS)
//                .select(ACCOUNT.USER_NAME.as(ArticleDTO::getAuthorName)
//                        , ACCOUNT.AGE.as(ArticleDTO::getAuthorAge)
//                        , ACCOUNT.BIRTHDAY
//                )
            .from(Article.class)
//                .leftJoin(Account.class).as("a").on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .leftJoin(Account.class).as("a").on(wrapper -> wrapper.where(Account::getId).eq(Article::getAccountId))
            .where(Account::getId).ge(100, If::notNull)
            .and(wrapper -> {
                wrapper.where(Account::getId).ge(100)
                    .or(Account::getAge).gt(200)
                    .and(Article::getAccountId).eq(200)
                    .or(wrapper1 -> {
                        wrapper1.where(Account::getId).like("a", If::hasText);
                    })
                ;
            });
        System.out.println(query.toSQL());
//                .and(query->query.and);
//                .andEq(Account::getId,100);
//                .and(new Brackets(column))
//                .where(column(Account::getId).eq(1).when(If::notEmpty));

//                .where(ARTICLE.ID.ge(0).or(ACCOUNT.ID.ge(0)));
        // .where(Account::getId).eq(1).and()
        //   where(eq(Account::getId,1))


//
//        List<ArticleDTO> articleDTOS = accountMapper.selectListByQueryAs(asWrapper, ArticleDTO.class);
//        System.out.println(articleDTOS);
//        Page<ArticleDTO> paginate = accountMapper.paginateAs(Page.of(1, 1), asWrapper1, ArticleDTO.class);
//        System.out.println(paginate);
//
//
//
        QueryWrapper asWrapper = QueryWrapper.create()
            .select(ARTICLE.ALL_COLUMNS, ACCOUNT.ALL_COLUMNS)
            .from(ARTICLE)
            .leftJoin(ACCOUNT).on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .where(ARTICLE.ID.ge(0).or(ACCOUNT.ID.ge(0)));

        RowUtil.printPretty(Db.selectListByQuery(asWrapper));
//
//        List<ArticleDTO> articleDTOS = accountMapper.selectListByQueryAs(asWrapper, ArticleDTO.class);
//        System.out.println(articleDTOS);
//        Page<ArticleDTO01> paginate = accountMapper.paginateAs(Page.of(1, 10), asWrapper, ArticleDTO01.class);
//        System.out.println(paginate);

        Db.tx(() -> {
            Cursor<Account> accounts1 = accountMapper.selectCursorByQuery(asWrapper);
            System.out.println(accounts1.isOpen());
            for (Account account : accounts1) {
                System.out.println(accounts1.isOpen());
                System.out.println(account);
            }
            System.out.println(accounts1.isOpen());
            return true;
        });

//        Cursor<Account> accounts = accountMapper.selectCursorByQuery(asWrapper);
//        System.out.println(accounts.isOpen());
//        for (Account account : accounts) {
//            System.out.println(accounts.isOpen());
//            System.out.println(account);
//        }
//        System.out.println(accounts.isOpen());


//        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.where(ACCOUNT.ID.in(
//                    select(ACCOUNT.ID).from(ACCOUNT).where(ACCOUNT.ID.in(
//                            select(ACCOUNT.ID).from(ACCOUNT)
//                    )
//                )
//        ));
//
//        Page<Account> paginate = accountMapper.paginate(1,10,queryWrapper);
//        System.out.println(paginate);


//        QueryWrapper query = QueryWrapper.create()
//                .where(ACCOUNT.ID.in(
//                                select(ACCOUNT.ID).from(ACCOUNT)
//                                        .where(ACCOUNT.ID.eq(1))
//                        ).and("1 = 1")
//                );
//
//        List<Account> accounts = accountMapper.selectListByQuery(query);
//        System.out.println(accounts);


//        List<Account> accounts = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Account account = new Account();
//            account.setUserName("test" + i);
//            accounts.add(account);
//        }
//
//        accountMapper.insertBatch(accounts);


//        Account account = accountMapper.selectOneById(1);
//        System.out.println(account);
//
//        account.setSex(SexEnum.TYPE3);
//        accountMapper.update(account);
//        account = accountMapper.selectOneById(1);
//        System.out.println(account);


//        QueryWrapper query = QueryWrapper.create().where(SYS_CONFIG.TYPE.eq(type).when(StrChecker.isNotBlank(type)))
//                .and(SYS_CONFIG.NAME.like(word).when(StrChecker.isNotBlank(word))
//                        .or(SYS_CONFIG.CODE.like(word).when(StrChecker.isNotBlank(word)))
//                        .or(SYS_CONFIG.VALUE.like(word).when(StrChecker.isNotBlank(word)))
//                        .or(SYS_CONFIG.TYPE.like(word).when(StrChecker.isNotBlank(word)))
//                );

//        List<Account> accounts = accountMapper.selectListByQuery(
//                select().where(ACCOUNT.AGE.ge(18).when(false))
//                        .and(ACCOUNT.USER_NAME.like("aaaa").when(false)
//                                .or(ACCOUNT.USER_NAME.like("aaaa").when(false))
//                                .or(ACCOUNT.USER_NAME.like("aaaa").when(false))
//                                .or(ACCOUNT.USER_NAME.like("aaaa").when(false))
//                        )
//        );
//        System.out.println(accounts);


//        Page<Account> paginate = accountMapper.paginate(1, 10, QueryWrapper.create());
//        System.out.println(paginate);
//
//        List<Account> accounts = accountMapper.selectListByQuery(
//                select().where(ACCOUNT.AGE.ge(18))
//                        .and(ACCOUNT.USER_NAME.like(null))
//                        .and(ACCOUNT.ID.ge(null))
//        );
//        System.out.println(accounts);
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
