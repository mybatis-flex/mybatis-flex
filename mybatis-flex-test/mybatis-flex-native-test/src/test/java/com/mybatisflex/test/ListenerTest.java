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
import com.mybatisflex.core.datasource.DataSourceKey;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.Date;

/**
 * 监听器测试
 *
 * @author snow
 * @since 2023/4/28
 */
public class ListenerTest implements WithAssertions {

    private static final String DATA_SOURCE_KEY = "listener";

    private AccountMapper accountMapper;
    private EmbeddedDatabase dataSource;

    @Before
    public void init() {
        dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("auto_increment_key_schema.sql").setScriptEncoding("UTF-8")
            .build();
        // 注册全局监听器
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
        // age < 0，将其设置为 0
        defaultConfig.registerInsertListener(new AgeHandleListener(), AgeAware.class);

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
            .setLogImpl(StdOutImpl.class)
            .setDataSource(DATA_SOURCE_KEY, dataSource)
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
    public void onInsertInterface() {
        Account account = new Account();
        account.setAge(-2);
        account.setUserName("on insert");
        Date birthday = new Date();
        account.setBirthday(birthday);
        accountMapper.insert(account);

        Account one = accountMapper.selectOneById(account.getId());
        assertThat(one).isNotNull()
            .extracting(Account::getId, Account::getUserName, Account::getAge, Account::getBirthday)
            .containsExactly(1L, "on******t", 0, birthday);
    }

}
