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
package com.mybatisflex.test;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.bean.LifecycleBean;

@Configuration
public class SampleApp implements LifecycleBean {

    public static void main(String[] args) {
        Solon.start(SampleApp.class, args);
    }

    @Override
    public void postStart() throws Throwable {
        System.out.println("onApplicationEvent");
        // 开启审计功能
        AuditManager.setAuditEnable(true);
        // 设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector((sql, tookTimeMillis) ->
            System.out.println(SqlFormatter.format(sql)));
        AuditManager.setMessageCollector(collector);
    }
}
