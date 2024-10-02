/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.mapper.Entity04Mapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

public class QueryWrapperTest {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema04.sql")
            .addScript("data04.sql")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(Entity04Mapper.class)
            .start();

        // 开启审计功能
        AuditManager.setAuditEnable(true);

        // 设置 SQL 审计收集器
        AuditManager.setMessageCollector(new ConsoleMessageCollector());

        Entity04Mapper mapper = bootstrap.getMapper(Entity04Mapper.class);

        Entity04 entity04 = new Entity04();
        entity04.setId("3");
        entity04.setAge(200);
        entity04.setOptions(Collections.singletonMap("key", "value"));

        mapper.insertSelective(entity04);

        // 调用 Fastjson2TypeHandler 处理 options 属性拼接到查询语句中
        QueryWrapper queryWrapper = QueryWrapper.create(entity04);
        List<Entity04> entity04s = mapper.selectListByQuery(queryWrapper);
        System.out.println(entity04);
        System.out.println(entity04s.get(0));
        System.out.println(entity04.toString().equals(entity04s.get(0).toString()));
    }

}
