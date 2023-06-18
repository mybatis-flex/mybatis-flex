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

import javax.sql.DataSource;
import java.util.Map;


/**
 * 多数据源的配置支持
 */
@org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnMybatisFlexDatasource()
@EnableConfigurationProperties(MybatisFlexProperties.class)
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
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
            for (String key : dataSourceProperties.keySet()) {
                DataSource dataSource = new DataSourceBuilder(dataSourceProperties.get(key)).build();
                if (flexDataSource == null) {
                    flexDataSource = new FlexDataSource(key, dataSource);
                } else {
                    flexDataSource.addDataSource(key, dataSource);
                }
            }
        }

        return flexDataSource;
    }


}
