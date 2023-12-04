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
import com.mybatisflex.mapper.Account5Mapper;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class AccountInsertWithArrayAttrTest implements WithAssertions {

    private Account5Mapper accountMapper;
    private EmbeddedDatabase dataSource;

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }

    @Before
    public void init() {
        this.dataSource =  new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema05.sql")
            .addScript("data05.sql")
            .build();

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
            .setDataSource(dataSource)
            .addMapper(Account5Mapper.class)
            .start();

        accountMapper = bootstrap.getMapper(Account5Mapper.class);
    }

    @After
    public void destroy() {
        this.dataSource.shutdown();
    }

    @Test
    @Ignore
    public void testInsertWithPk() {
        Account5 account = new Account5();
        account.setId(3L);
        account.setUserName("lisi");
        account.setDataScope(new Long[]{1L, 2L});
        accountMapper.insertWithPk(account, false);

        // todo 查询有问题，会抛出 argument type mismatch
        Account5 result = accountMapper.selectOneById(3L);
        assertThat(result).isNotNull()
            .extracting(Account5::getUserName, Account5::getDataScope)
            .containsExactly("lisi", new Long[]{1L, 2L});
    }

}
