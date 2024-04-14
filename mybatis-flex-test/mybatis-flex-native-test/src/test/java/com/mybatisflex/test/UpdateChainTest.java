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
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.SneakyThrows;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;
import static com.mybatisflex.test.table.ArticleTableDef.ARTICLE;

public class UpdateChainTest implements WithAssertions {

    private static final String DATA_SOURCE_KEY = "ds2";

    private AccountMapper accountMapper;

    private EmbeddedDatabase dataSource;

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }

    @Before
    public void init() {
        this.dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .setScriptEncoding("UTF-8")
                .build();

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
                .setDataSource(DATA_SOURCE_KEY, this.dataSource)
                .setLogImpl(StdOutImpl.class)
                .addMapper(AccountMapper.class)
                .start();

        DataSourceKey.use(DATA_SOURCE_KEY);
        accountMapper = bootstrap.getMapper(AccountMapper.class);
    }

    @After
    public void destroy() {
        this.dataSource.shutdown();
        DataSourceKey.clear();
    }

    @Test
    public void testUpdateAll() {
        assertThatThrownBy(() -> {
            UpdateChain.of(accountMapper)
                .set(Account::getAge, 11)
                .update();
        });
    }

    @Test
    @SneakyThrows
    public void testUpdateChain() {
        UpdateChain.of(Account.class)
                .set(Account::getUserName, "张三")
                .setRaw(Account::getAge, "age + 1")
                .where(Account::getId).eq(1)
                .update();

        Account account = accountMapper.selectOneById(1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String toParse = "2020-01-11";
        assertThat(account).isNotNull()
                .extracting(
                        Account::getUserName, Account::getAge,
                        Account::getSex, Account::getBirthday,
                        Account::getOptions, Account::getDelete,
                        Account::getArticles, Account::getTitle)
                .containsExactly(
                        "张*", 19,
                        SexEnum.TYPE1, format.parse(toParse),
                        Collections.singletonMap("key", "value1"), false,
                        Collections.emptyList(), null);
    }

    @Test
    public void testUpdateChain1() {
        UpdateChain.of(Account.class)
                .set(Account::getAge, ACCOUNT.AGE.add(1))
                .where(Account::getId).ge(100)
                .and(Account::getAge).eq(18)
                .update();

        List<Account> list = QueryChain.of(accountMapper).list();
        assertThat(list).hasSize(2)
                .extracting(Account::getId, Account::getUserName, Account::getAge)
                .containsExactly(tuple(1L, "张*", 18), tuple(2L, "王麻**叔", 19));
    }

    @Test
    public void testUpdateChainToSql() {
        String sql = UpdateChain.of(Account.class)
                .set(ACCOUNT.AGE, 18)
                .set(Article::getAccountId, 4)
                .leftJoin(ARTICLE).as("ar").on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
                .where(ACCOUNT.ID.eq(4))
                .toSQL();

        String expectSQL = "UPDATE `tb_account` " +
                "LEFT JOIN `tb_article` AS `ar` ON `tb_account`.`id` = `ar`.`account_id` " +
                "SET `age` = 18 , `accountId` = 4  WHERE `id` = 4";

        assertThat(sql).isEqualTo(expectSQL);
    }

}
