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

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

@SpringBootApplication
@Configuration
@MapperScan("com.mybatisflex.test.mapper")
public class SampleApplication implements CommandLineRunner, ApplicationListener<ContextRefreshedEvent> {


//    @Resource
//    private AccountMapper accountMapper;


    @Override
    public void run(String... args) throws Exception {
//        System.out.println(this.accountMapper.selectOneById(1));
    }


    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("onApplicationEvent");
        // 开启审计功能
        AuditManager.setAuditEnable(true);
        // 设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector((sql, tookTimeMillis) ->
            System.out.println(SqlFormatter.format(sql)));
        AuditManager.setMessageCollector(collector);
    }

}
