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
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

public class MaskManagerTest {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("db1")
            .addScript("schema.sql")
            .addScript("data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(AccountMapper.class)
            .start();

        //获取 mapper
        AccountMapper mapper = MybatisFlexBootstrap.getInstance().getMapper(AccountMapper.class);
        List<Account> accounts = mapper.selectAll();
//        List<Account> accounts = MaskManager.withoutMask(mapper::selectAll);
        System.out.println(accounts);
    }

}
