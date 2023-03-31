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

import com.mybatisflex.core.datasource.RoutingDataSource;
import com.mybatisflex.spring.FlexSqlSessionFactoryBean;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
public class MultiDataSourceAutoConfiguration extends MybatisFlexAutoConfiguration {

    private List<SqlSessionFactory> sqlSessionFactories = new ArrayList<>();
    private List<DataSource> dataSources = new ArrayList<>();


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


    private void initDataSources(Map<String, Map<String, String>> datasourceMap) {
        if (datasourceMap != null) {
            datasourceMap.forEach((s, dsp) -> {
                DataSource dataSource = new DataSourceBuilder(dsp).build();
                SqlSessionFactory sqlSessionFactory = buildSqlSessionFactory(s, dataSource);
                sqlSessionFactories.add(sqlSessionFactory);
            });
        }
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
        return sqlSessionFactories.isEmpty() ? null : sqlSessionFactories.get(0);
    }


    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        return dataSources.isEmpty() ? null : dataSources.get(0);
    }


}
