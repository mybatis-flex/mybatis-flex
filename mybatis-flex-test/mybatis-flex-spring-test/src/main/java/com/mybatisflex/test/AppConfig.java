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

import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.spring.FlexSqlSessionFactoryBean;
import com.mybatisflex.test.model.EnumTypeHandler;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.mybatisflex.test.mapper")
public class AppConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql").setScriptEncoding("UTF-8")
            .build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        // SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        SqlSessionFactoryBean factoryBean = new FlexSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        FlexConfiguration configuration = new FlexConfiguration();
        configuration.setLogImpl(StdOutImpl.class);
        configuration.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        factoryBean.setConfiguration(configuration);
        return factoryBean.getObject();
    }

    @EventListener(classes = {ContextStartedEvent.class})
    public void handleContextStartedEvent() {
        System.out.println("handleContextStartedEvent listener invoked!");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("onApplicationEvent");
    }

}
