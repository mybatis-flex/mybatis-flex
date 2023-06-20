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
package com.mybatisflex.spring.boot;

import com.mybatisflex.core.datasource.DataSourceBuilder;
import com.mybatisflex.core.datasource.FlexDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;


/**
 * 多数据源的配置支持
 */
@ConditionalOnMybatisFlexDatasource()
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MybatisFlexProperties.class)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class, name = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure")
public class MultiDataSourceAutoConfiguration {

    private final Map<String, Map<String, String>> dataSourceProperties;


    public MultiDataSourceAutoConfiguration(MybatisFlexProperties properties) {
        dataSourceProperties = properties.getDatasource();
    }


    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {

        FlexDataSource flexDataSource = null;

        if (dataSourceProperties != null && !dataSourceProperties.isEmpty()) {
            for (Map.Entry<String, Map<String, String>> entry : dataSourceProperties.entrySet()) {
                DataSource dataSource = new DataSourceBuilder(entry.getValue()).build();
                if (flexDataSource == null) {
                    flexDataSource = new FlexDataSource(entry.getKey(), dataSource);
                } else {
                    flexDataSource.addDataSource(entry.getKey(), dataSource);
                }
            }
        }

        return flexDataSource;
    }


}
