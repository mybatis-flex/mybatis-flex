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
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Date;

/**
 * 监听器测试
 *
 * @author snow
 * @since 2023/4/28
 */
public class ListenerTest implements WithAssertions {

    // 注册父类接口监听器
//    @Test
    public void onInsertInterface() throws Exception {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .build();
        // 注册全局监听器
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
        defaultConfig.registerInsertListener(new AgeHandleListener(), AgeAware.class);

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setLogImpl(StdOutImpl.class)
            .setDataSource(dataSource)
            .addMapper(AccountMapper.class)
            .start();

        AccountMapper accountMapper = bootstrap.getMapper(AccountMapper.class);
        Account account = new Account();
        account.setAge(-2);
        account.setUserName("on insert");
        account.setBirthday(new Date());
        accountMapper.insert(account);

        Account one = accountMapper.selectOneById(account.getId());
        System.out.println(one);
//        assertThat(one.getAge()).isEqualTo(1);
    }

}
