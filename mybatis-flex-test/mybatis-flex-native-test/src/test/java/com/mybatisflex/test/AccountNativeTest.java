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

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.mybatis.Mappers;
import com.mybatisflex.core.query.If;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.DbChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.mapper.ArticleMapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;
import java.util.Objects;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.table.ArticleTableDef.ARTICLE;


public class AccountNativeTest implements WithAssertions {

    private EmbeddedDatabase dataSource;
    private AccountMapper accountMapper;
    private ArticleMapper articleMapper;

    private static final String DATA_SOURCE_KEY = "auto_increment";

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
        FlexGlobalConfig.getDefaultConfig().setLogicDeleteColumn("is_delete");
    }

    @Before
    public void init() {
        this.dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("auto_increment_key_schema.sql")
            .addScript("auto_increment_key_data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
            .setDataSource(DATA_SOURCE_KEY, this.dataSource)
            .setLogImpl(StdOutImpl.class)
            .addMapper(AccountMapper.class)
            .addMapper(ArticleMapper.class)
            .start();

        DataSourceKey.use(DATA_SOURCE_KEY);

        accountMapper = bootstrap.getMapper(AccountMapper.class);
        articleMapper = bootstrap.getMapper(ArticleMapper.class);
    }

    @After
    public void destroy() {
        this.dataSource.shutdown();
        DataSourceKey.clear();
    }

    @Test
    public void testSelect() {
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NONE);
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(ACCOUNT.ALL_COLUMNS)
            .from(ACCOUNT)
            .where(ACCOUNT.ID.eq(null))
            .and(ACCOUNT.AGE.ge(18, false))
            .and(QueryCondition.createEmpty())
            .and(ACCOUNT.USER_NAME.isNotNull());
        List<Account> accounts = accountMapper.selectListByQuery(queryWrapper);
        assertThat(accounts).hasSize(0);
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NULL);
    }

    @Test
    public void testLogicDelete() {
        List<Account> accounts = accountMapper.selectAll();
        assertThat(accounts).hasSize(2)
            .extracting(Account::getId, Account::getUserName)
            .containsExactly(tuple(1L, "张*"), tuple(2L, "王麻**叔"));
    }

    @Test
    public void testExecutor() {
        List<Account> accountList = DbChain.table("tb_account")
            .select(ACCOUNT.ALL_COLUMNS)
            .from(ACCOUNT)
            .where(ACCOUNT.ID.ge(1))
            .listAs(Account.class);
        assertThat(accountList).hasSize(2)
            .extracting(Account::getId, Account::getUserName)
            .containsExactly(tuple(1L, "张*"), tuple(2L, "王麻**叔"));

        AccountMapper accountBaseMapper = (AccountMapper) Mappers.ofEntityClass(Account.class);

        AccountMapper accountMapper = Mappers.ofMapperClass(AccountMapper.class);
        assertThat(accountBaseMapper).isSameAs(accountMapper);

        Account account = accountBaseMapper.selectOneById(1);
        assertThat(account).isNotNull()
            .extracting(Account::getId, Account::getUserName)
            .containsExactly(1L, "张*");
    }

    @Test
    public void testLambda() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(Account::getId).ge(100)
            .and(Account::getUserName).like("michael")
            .or(Account::getUserName).like(null, If::notNull);
        String expectSql = "SELECT * FROM  WHERE `id` >= 100 AND `user_name` LIKE '%michael%'";
        assertThat(queryWrapper.toSQL()).isEqualTo(expectSql);
    }

    @Test
    public void testTenant() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        // id >= 1
        queryWrapper.where(Account::getId).ge(1);
        List<Account> accounts = accountMapper.selectListByQuery(queryWrapper);
        assertThat(accounts).hasSize(2)
            .extracting(Account::getId, Account::getUserName)
            .containsExactly(tuple(1L, "张*"), tuple(2L, "王麻**叔"));
    }

    @Test
    public void testLeftJoinForLogicDelete() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.from(ARTICLE)
            .leftJoin(ACCOUNT).on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
            .where(ARTICLE.ID.ge(1));
        String expectSql = "SELECT * FROM `tb_article` " +
                           "LEFT JOIN `tb_account` ON `tb_article`.`account_id` = `tb_account`.`id` " +
                           "WHERE `tb_article`.`id` >= 1";
        assertThat(queryWrapper.toSQL()).isEqualTo(expectSql);
        List<Article> accounts = articleMapper.selectListByQuery(queryWrapper);
        assertThat(accounts).hasSize(3)
            .extracting(Article::getId, Article::getAccountId, Article::getTitle)
            .containsExactly(tuple(1L, 1L, "标题1"), tuple(2L, 2L, "标题2"), tuple(3L, 1L, "标题3"));
    }

    /**
     * issues  https://gitee.com/mybatis-flex/mybatis-flex/issues/I7QD29
     */
    @Test
    public void testGiteeIssue_I7QD29() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.from(ACCOUNT)
            .leftJoin(ARTICLE).as("a1").on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
            .leftJoin(ARTICLE).as("a2").on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
            .where(ACCOUNT.ID.ge(1));
        List<Article> accounts = articleMapper.selectListByQuery(queryWrapper);
        accounts = articleMapper.selectListByQuery(queryWrapper);
        String expectSql = "SELECT * FROM `tb_account` " +
            "LEFT JOIN `tb_article` AS `a1` ON (`tb_account`.`id` = `a1`.`account_id`) AND `a1`.`is_delete` = 0 " +
            "LEFT JOIN `tb_article` AS `a2` ON (`tb_account`.`id` = `a1`.`account_id`) AND `a2`.`is_delete` = 0 " +
            "WHERE (`tb_account`.`id` >= 1) AND `tb_account`.`is_delete` = 0";
//            "WHERE `tb_account`.`id` >= 1";
        //SELECT * FROM `tb_account`
        // LEFT JOIN `tb_article` AS `a1` ON (`tb_account`.`id` = `a1`.`account_id`) AND `a1`.`is_delete` = 0
        // LEFT JOIN `tb_article` AS `a2` ON (`tb_account`.`id` = `a1`.`account_id`) AND `a2`.`is_delete` = 0
        // WHERE `tb_account`.`id` >= 1
        System.out.println("aa>>11:  \"" + queryWrapper.toSQL()+"\"");
        // SELECT * FROM `tb_account`
        // LEFT JOIN `tb_article` AS `a1` ON (`tb_account`.`id` = `a1`.`account_id`) AND `a1`.`is_delete` = 0
        // LEFT JOIN `tb_article` AS `a2` ON (`tb_account`.`id` = `a1`.`account_id`) AND `a2`.`is_delete` = 0
        // WHERE `tb_account`.`id` >= 1
//        assertThat(queryWrapper.toSQL()).isEqualTo(expectSql);
        assertThat(accounts).hasSize(9);
    }

    /**
     * issues https://gitee.com/mybatis-flex/mybatis-flex/issues/I7VAG8
     */
    @Test
    public void testGiteeIssue_I7VAG8() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper
            .select(ACCOUNT.ID, ACCOUNT.AGE, ARTICLE.TITLE)
            .from(ACCOUNT)
            .leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
            .where(ACCOUNT.ID.ge(1));
        String expectSql = "SELECT `tb_account`.`id` AS `account_id`, `tb_account`.`age` AS `my_age`, `tb_article`.`title` " +
                           "FROM `tb_account` " +
                           "LEFT JOIN `tb_article` " +
                           "ON `tb_account`.`id` = `tb_article`.`account_id` " +
                           "WHERE `tb_account`.`id` >= 1";
        assertThat(queryWrapper.toSQL()).isEqualTo(expectSql);
        List<Account> accounts = accountMapper.selectListByQuery(queryWrapper);
        assertThat(accounts).hasSize(2)
            .extracting(Account::getId, Account::getAge, Account::getTitle)
            .containsExactly(tuple(1L, 18, "标题1"), tuple(2L, 19, "标题2"));
    }


    /**
     * issues https://gitee.com/mybatis-flex/mybatis-flex/issues/I7RE0J
     */
    @Test
    @Ignore
    public void testGiteeIssue_I7RE0J() {
        Account account = new Account();
        account.setId(1L);
        account = UpdateWrapper.of(account)
            .set(Account::getId, 1)
            .set(Account::getAge, 20)
            // 设置 Ignore 字段，会被自动忽略
            .setRaw(Account::getTitle, "xxxx")
            .toEntity();
        // todo title not found
        accountMapper.update(account);
    }


    @Test
    public void testSelectAsToDTO() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select(ACCOUNT.ALL_COLUMNS, ACCOUNT.USER_NAME.as(AccountDTO::getTestOtherField))
            .from(ACCOUNT)
            .leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID));
        String expectSql = "SELECT `tb_account`.*, `tb_account`.`user_name` AS `test_other_field` " +
                           "FROM `tb_account` " +
                           "LEFT JOIN `tb_article` " +
                           "ON `tb_account`.`id` = `tb_article`.`account_id`";
        assertThat(queryWrapper.toSQL()).isEqualTo(expectSql);
        List<AccountDTO> accountDTOS = accountMapper.selectListByQueryAs(queryWrapper, AccountDTO.class);
        assertThat(accountDTOS).hasSize(2)
            .extracting(AccountDTO::getId, AccountDTO::getUserName)
            .containsExactly(tuple(1L, "张*"), tuple(2L, "王麻**叔"));
    }


    @Test
    public void testUpdate1() {
        List<Account> accounts = accountMapper.selectAll();
        assertThat(accounts).hasSize(2)
            .extracting(Account::getId, Account::getAge)
            .containsExactly(tuple(1L, 18), tuple(2L, 19));


        Account account = UpdateEntity.of(Account.class, 1);
        account.setUserName("lisi");

        UpdateWrapper.of(account)
            .setRaw("age", "age + 1");
        accountMapper.update(account);


        accounts = accountMapper.selectAll();
        assertThat(accounts).hasSize(2)
            .filteredOn(i -> Objects.equals(1L, i.getId()))
            .extracting(Account::getUserName, Account::getAge)
            .containsExactly(tuple("**si", 19));
    }


    @Test
    public void testUpdate2() {
        List<Account> accounts = accountMapper.selectAll();
        assertThat(accounts).hasSize(2)
            .filteredOn(i -> Objects.equals(1L, i.getId()))
            .extracting(Account::getUserName)
            .containsExactly("张*");

        UpdateChain.of(Account.class)
            .set(Account::getUserName, "zhangsan123")
            .where(Account::getId).eq(1)
            .limit(1)
            .remove();

        accounts = accountMapper.selectAll();
        assertThat(accounts).hasSize(1)
            .singleElement()
            .extracting(Account::getId, Account::getUserName)
            .containsExactly(2L, "王麻**叔");
    }

    /**
     * https://gitee.com/mybatis-flex/mybatis-flex/issues/I7L6DF
     */
    @Test
    public void testGiteeIssue_I7L6DF() {
        List<Account> accounts = accountMapper.selectAll();
        assertThat(accounts).hasSize(2);


        Account account = new Account();
        account.setId(4L);
        account.setUserName("test04");
        int rows = accountMapper.insertSelectiveWithPk(account);
        assertThat(rows).isEqualTo(1);

        accounts = accountMapper.selectAll();
        assertThat(accounts).hasSize(3)
            .filteredOn(i -> Objects.equals(4L, i.getId()))
            .extracting(Account::getUserName)
            .containsExactly("te***4");
    }


    @Test
    public void testInsertWithRaw() {
        Account account = new Account();
        account.setUserName("michael");

        Account newAccount = UpdateWrapper.of(account)
            .setRaw(Account::getBirthday, "now()")
            .toEntity();
        accountMapper.insert(newAccount);
        Account result = accountMapper.selectOneByEntityId(newAccount);
        assertThat(result).isNotNull()
            .extracting(Account::getId, Account::getUserName, Account::getBirthday)
            .contains(3L, Index.atIndex(0))
            .contains("mi****l", Index.atIndex(1))
            .allMatch(Objects::nonNull);
    }
}
