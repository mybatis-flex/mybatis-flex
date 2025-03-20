/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.loveqq.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.boot.data.orm.mybatis.autoconfig.annotation.MapperScan;
import com.kfyty.loveqq.framework.core.autoconfig.CommandLineRunner;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.BootApplication;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnMissingBean;
import com.kfyty.loveqq.framework.core.event.ApplicationListener;
import com.kfyty.loveqq.framework.core.event.ContextRefreshedEvent;
import com.kfyty.loveqq.framework.core.utils.JsonUtil;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.loveqq.test.mapper.AccountMapper;

@BootApplication
@MapperScan("com.mybatisflex.loveqq.test.mapper.**.*")
public class SampleApplication implements CommandLineRunner, ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private AccountMapper accountMapper;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(this.accountMapper.selectOneById(1));
    }

    public static void main(String[] args) {
        K.start(SampleApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("onApplicationEvent");
        // 开启审计功能
        AuditManager.setAuditEnable(true);
        // 设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector((sql, tookTimeMillis) -> System.out.println(sql));
        AuditManager.setMessageCollector(collector);
    }

    @ConditionalOnMissingBean
    @Bean(resolveNested = false, independent = true)
    public ObjectMapper objectMapper() {
        return JsonUtil.configure();
    }
}
