/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.spring.boot;

import cn.beecp.BeeDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.core.datasource.RoutingDataSource;
import com.mybatisflex.core.util.StringUtil;
import com.mybatisflex.spring.FlexSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 多数据源的配置支持
 */
@org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnPropertyEmpty("spring.datasource.url")
@EnableConfigurationProperties(MybatisFlexProperties.class)
@AutoConfigureAfter({MybatisLanguageDriverAutoConfiguration.class})
public class MultiDataSourceAutoConfiguration extends MybatisFlexAutoConfiguration {

    private List<SqlSessionFactory> sqlSessionFactories = new ArrayList<>();
    private List<DataSource> dataSources = new ArrayList<>();


    private static Map<String, String> dataSourceAlias = new HashMap<>();

    static {
        dataSourceAlias.put("druid", "com.alibaba.druid.pool.DruidDataSource");
        dataSourceAlias.put("hikari", "com.zaxxer.hikari.HikariDataSource");
        dataSourceAlias.put("hikaricp", "com.zaxxer.hikari.HikariDataSource");
        dataSourceAlias.put("bee", "cn.beecp.BeeDataSource");
        dataSourceAlias.put("beecp", "cn.beecp.BeeDataSource");
        dataSourceAlias.put("dbcp", "org.apache.commons.dbcp2.BasicDataSource");
        dataSourceAlias.put("dbcp2", "org.apache.commons.dbcp2.BasicDataSource");
    }


    public MultiDataSourceAutoConfiguration(MybatisFlexProperties properties
            , ObjectProvider<Interceptor[]> interceptorsProvider
            , ObjectProvider<TypeHandler[]> typeHandlersProvider
            , ObjectProvider<LanguageDriver[]> languageDriversProvider
            , ResourceLoader resourceLoader
            , ObjectProvider<DatabaseIdProvider> databaseIdProvider
            , ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider
            , ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers) {
        super(properties, interceptorsProvider, typeHandlersProvider, languageDriversProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider, sqlSessionFactoryBeanCustomizers);

        initDataSources(properties.getDatasource());
    }


    private void initDataSources(Map<String, DataSourceProperty> datasourceMap) {
        datasourceMap.forEach((s, dsp) -> {
            SqlSessionFactory sqlSessionFactory = buildSqlSessionFactory(s, createDataSource(dsp));
            sqlSessionFactories.add(sqlSessionFactory);
        });
    }


    private DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        String type = dataSourceProperty.getType();
        if (StringUtil.isBlank(type)) {
            type = detectDataSourceClass();
        }
        if (StringUtil.isBlank(type)) {
            throw new IllegalArgumentException("The dataSource cannot be null or empty.");
        }

        switch (type) {
            case "druid":
            case "com.alibaba.druid.pool.DruidDataSource":
                return createDruidDataSource(dataSourceProperty);
            case "hikari":
            case "hikaricp":
            case "com.zaxxer.hikari.HikariDataSource":
                return createHikariDataSource(dataSourceProperty);
            case "bee":
            case "beecp":
            case "cn.beecp.BeeDataSource":
                return createBeeDataSource(dataSourceProperty);
            case "dbcp":
            case "dbcp2":
            case "org.apache.commons.dbcp2.BasicDataSource":
                return createDbcpDataSource(dataSourceProperty);
            default:
                throw new IllegalArgumentException("Cannot Support the dataSource type:" + dataSourceProperty.getType());
        }
    }


    private String detectDataSourceClass() {
        String[] detectClassNames = new String[]{
                "com.alibaba.druid.pool.DruidDataSource",
                "com.zaxxer.hikari.HikariDataSource",
                "cn.beecp.BeeDataSource",
                "org.apache.commons.dbcp2.BasicDataSource",
        };

        for (String detectClassName : detectClassNames) {
            String result = doDetectDataSourceClass(detectClassName);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private String doDetectDataSourceClass(String className) {
        try {
            Class.forName(className);
            return className;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private DataSource createDbcpDataSource(DataSourceProperty dataSourceProperty) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(dataSourceProperty.getUrl());
        ds.setUsername(dataSourceProperty.getUsername());
        ds.setPassword(dataSourceProperty.getPassword());
        ds.setDriverClassName(dataSourceProperty.getDriverClassName());
        return ds;
    }

    private DataSource createBeeDataSource(DataSourceProperty dataSourceProperty) {
        BeeDataSource ds = new BeeDataSource();
        ds.setJdbcUrl(dataSourceProperty.getUrl());
        ds.setUsername(dataSourceProperty.getUsername());
        ds.setPassword(dataSourceProperty.getPassword());
        ds.setDriverClassName(dataSourceProperty.getDriverClassName());
        return ds;
    }


    private DataSource createDruidDataSource(DataSourceProperty dataSourceProperty) {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(dataSourceProperty.getUrl());
        ds.setUsername(dataSourceProperty.getUsername());
        ds.setPassword(dataSourceProperty.getPassword());
        ds.setDriverClassName(dataSourceProperty.getDriverClassName());
        return ds;
    }


    private DataSource createHikariDataSource(DataSourceProperty dataSourceProperty) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dataSourceProperty.getUrl());
        ds.setUsername(dataSourceProperty.getUsername());
        ds.setPassword(dataSourceProperty.getPassword());
        if (StringUtil.isNotBlank(dataSourceProperty.getDriverClassName())) {
            ds.setDriverClassName(dataSourceProperty.getDriverClassName());
        }
        return ds;
    }


    public SqlSessionFactory buildSqlSessionFactory(String environmentId, DataSource dataSource) {
//    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        SqlSessionFactoryBean factory = new FlexSqlSessionFactoryBean();

        dataSource = new RoutingDataSource(environmentId, dataSource);
        dataSources.add(dataSource);

        factory.setDataSource(new RoutingDataSource(environmentId, dataSource));
//        factory.setEnvironment(environmentId);

        if (properties.getConfiguration() == null || properties.getConfiguration().getVfsImpl() == null) {
            factory.setVfs(SpringBootVFS.class);
        }
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.typeHandlers)) {
            factory.setTypeHandlers(this.typeHandlers);
        }
        Resource[] mapperLocations = this.properties.resolveMapperLocations();
        if (!ObjectUtils.isEmpty(mapperLocations)) {
            factory.setMapperLocations(mapperLocations);
        }
        Set<String> factoryPropertyNames = Stream
                .of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());
        Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
        if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
            // Need to mybatis-spring 2.0.2+
            factory.setScriptingLanguageDrivers(this.languageDrivers);
            if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
                defaultLanguageDriver = this.languageDrivers[0].getClass();
            }
        }
        if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
            // Need to mybatis-spring 2.0.2+
            factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
        }
        applySqlSessionFactoryBeanCustomizers(factory);
        try {
            return factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory() {
        return sqlSessionFactories.get(0);
    }


    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        return dataSources.get(0);
    }


}
