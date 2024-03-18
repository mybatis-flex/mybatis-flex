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
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.mapper.TenantAccountMapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

public class TenantManagerTester {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema03.sql")
            .addScript("data03.sql")
            .build();

        MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(TenantAccountMapper.class)
            .setLogImpl(StdOutImpl.class)
            .start();

        // 配置 tenantFactory
        TenantManager.setTenantFactory(() -> new Object[]{1});

        MybatisFlexBootstrap.getInstance()
            .getMapper(TenantAccountMapper.class)
            .selectAll()
            .forEach(System.out::println);
    }

}
