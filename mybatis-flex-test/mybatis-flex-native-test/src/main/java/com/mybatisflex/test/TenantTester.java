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
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.mapper.TenantAccountMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

public class TenantTester {

    public static void main(String[] args) {

        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema03.sql")
            .addScript("data03.sql")
            .build();

        MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(TenantAccountMapper.class)
            .start();


        //输出日志
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());

        //配置 tenantFactory
        TenantManager.setTenantFactory(() -> new Object[]{1, 2});

        TenantAccountMapper mapper = MybatisFlexBootstrap.getInstance()
            .getMapper(TenantAccountMapper.class);

        mapper.selectAll().forEach(System.out::println);

//        mapper.selectListByQuery(QueryWrapper.create()
//            .select(TENANT_ACCOUNT.ALL_COLUMNS)
//            .from(TENANT_ACCOUNT.as("c"), ACCOUNT.as("b"))
//            .where(TENANT_ACCOUNT.ID.eq(ACCOUNT.ID))
//            .and(TENANT_ACCOUNT.ID.eq(1))
//            .unionAll(select(TENANT_ACCOUNT.ALL_COLUMNS).from(TENANT_ACCOUNT)
//                .where(TENANT_ACCOUNT.ID.eq(2))
//            )
//        );

//         mapper.deleteBatchByIds(Arrays.asList(1, 2));

//
//        //SELECT * FROM `tb_account` WHERE `tenant_id` =  1
//        List<TenantAccount> tenantAccounts = mapper.selectAll();
//        System.out.println(tenantAccounts);
//
//        //SELECT * FROM `tb_account` WHERE `id` = 1 AND `tenant_id` IN (1)
//        TenantAccount tenantAccount1 = mapper.selectOneById(1);
//        System.out.println(tenantAccount1);
//
//        //SELECT * FROM `tb_account` WHERE `id` = 2 AND `tenant_id` IN (1)
//        TenantAccount tenantAccount2 = mapper.selectOneById(2);
//        System.out.println(tenantAccount2);
//
//        //DELETE FROM `tb_account` WHERE `id` >= 100 AND `tenant_id` =  1
//        mapper.deleteByCondition(TENANT_ACCOUNT.ID.ge(100));
//
//        // UPDATE `tb_account` SET `age` = 10 WHERE `id` >=  100  AND `tenant_id` =  1
//        TenantAccount updateAccount = new TenantAccount();
//        updateAccount.setAge(10);
//        mapper.updateByCondition(updateAccount,TENANT_ACCOUNT.ID.ge(100));
//
//        //SELECT * FROM `tb_account` WHERE `id` >=  100  AND `tenant_id` =  1  LIMIT 1
//        mapper.selectOneByCondition(TENANT_ACCOUNT.ID.ge(100));
//
//        //SELECT * FROM `tb_account` WHERE `id` >=  100  AND `tenant_id` =  1  LIMIT 10
//        mapper.selectListByCondition(TENANT_ACCOUNT.ID.ge(100),10);
    }

}
