/*
 *    Copyright 2015-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.mybatisflex.spring.boot.v4;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceDecipher;
import com.mybatisflex.core.datasource.DataSourceManager;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.table.DynamicSchemaProcessor;
import com.mybatisflex.core.table.DynamicTableProcessor;
import com.mybatisflex.core.table.TableManager;
import com.mybatisflex.core.tenant.TenantFactory;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.spring.FlexSqlSessionFactoryBean;
import com.mybatisflex.spring.boot.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Mybatis-Flex 的核心配置。
 * <p>
 * 参考 <a href="https://github.com/mybatis/spring-boot-starter/blob/master/mybatis-spring-boot-autoconfigure/src/main/java/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java">
 * MybatisAutoConfiguration.java</a>
 * <p>
 * 为 Mybatis-Flex 开启自动配置功能，主要修改以下几个方面:
 * <p>
 * 1、替换配置为 mybatis-flex 的配置前缀<br>
 * 2、修改 SqlSessionFactory 为 FlexSqlSessionFactoryBean<br>
 * 3、修改 Configuration 为 FlexConfiguration<br>
 *
 * @author Eddú Meléndez
 * @author Josh Long
 * @author Kazuki Shimizu
 * @author Eduardo Macarrón
 * @author michael
 * @author 王帅
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(
    value = {SqlSessionFactory.class, SqlSessionFactoryBean.class},
    name = {
        "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
    }
)
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(MybatisFlexProperties.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class})
public class MybatisFlexAutoConfiguration implements InitializingBean {

    protected static final Logger logger = LoggerFactory.getLogger(MybatisFlexAutoConfiguration.class);

    protected final MybatisFlexProperties properties;

    protected final Interceptor[] interceptors;

    protected final TypeHandler[] typeHandlers;

    protected final LanguageDriver[] languageDrivers;

    protected final ResourceLoader resourceLoader;

    protected final DatabaseIdProvider databaseIdProvider;

    protected final List<ConfigurationCustomizer> configurationCustomizers;

    protected final List<SqlSessionFactoryBeanCustomizer> sqlSessionFactoryBeanCustomizers;

    //数据源解密器
    protected final DataSourceDecipher dataSourceDecipher;

    //动态表名
    protected final DynamicTableProcessor dynamicTableProcessor;

    //动态 schema 处理器
    protected final DynamicSchemaProcessor dynamicSchemaProcessor;

    //多租户
    protected final TenantFactory tenantFactory;

    //自定义逻辑删除处理器
    protected final LogicDeleteProcessor logicDeleteProcessor;

    //初始化监听
    protected final List<MyBatisFlexCustomizer> mybatisFlexCustomizers;


    public MybatisFlexAutoConfiguration(MybatisFlexProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
                                        ObjectProvider<TypeHandler[]> typeHandlersProvider, ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                        ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                        ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                        ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers,
                                        ObjectProvider<DataSourceDecipher> dataSourceDecipherProvider,
                                        ObjectProvider<DynamicTableProcessor> dynamicTableProcessorProvider,
                                        ObjectProvider<DynamicSchemaProcessor> dynamicSchemaProcessorProvider,
                                        ObjectProvider<TenantFactory> tenantFactoryProvider,
                                        ObjectProvider<LogicDeleteProcessor> logicDeleteProcessorProvider,
                                        ObjectProvider<MyBatisFlexCustomizer> mybatisFlexCustomizerProviders
    ) {
        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.typeHandlers = typeHandlersProvider.getIfAvailable();
        this.languageDrivers = languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        this.sqlSessionFactoryBeanCustomizers = sqlSessionFactoryBeanCustomizers.getIfAvailable();

        //数据源解密器
        this.dataSourceDecipher = dataSourceDecipherProvider.getIfAvailable();

        //动态表名
        this.dynamicTableProcessor = dynamicTableProcessorProvider.getIfAvailable();

        //动态 schema 处理器
        this.dynamicSchemaProcessor = dynamicSchemaProcessorProvider.getIfAvailable();

        //多租户
        this.tenantFactory = tenantFactoryProvider.getIfAvailable();

        //逻辑删除处理器
        this.logicDeleteProcessor = logicDeleteProcessorProvider.getIfAvailable();

        //初始化监听器
        this.mybatisFlexCustomizers = mybatisFlexCustomizerProviders.orderedStream().collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        // 检测 MyBatis 原生配置文件是否存在
        checkConfigFileExists();

        // 添加 MyBatis-Flex 全局配置
        if (properties.getGlobalConfig() != null) {
            properties.getGlobalConfig().applyTo(FlexGlobalConfig.getDefaultConfig());
        }

        //数据源解密器
        if (dataSourceDecipher != null) {
            DataSourceManager.setDecipher(dataSourceDecipher);
        }

        // 动态表名配置
        if (dynamicTableProcessor != null) {
            TableManager.setDynamicTableProcessor(dynamicTableProcessor);
        }

        // 动态 schema 处理器配置
        if (dynamicSchemaProcessor != null) {
            TableManager.setDynamicSchemaProcessor(dynamicSchemaProcessor);
        }

        //多租户
        if (tenantFactory != null) {
            TenantManager.setTenantFactory(tenantFactory);
        }

        //逻辑删除处理器
        if (logicDeleteProcessor != null) {
            LogicDeleteManager.setProcessor(logicDeleteProcessor);
        }

        //初始化监听器
        if (mybatisFlexCustomizers != null) {
            mybatisFlexCustomizers.forEach(myBatisFlexCustomizer -> myBatisFlexCustomizer.customize(FlexGlobalConfig.getDefaultConfig()));
        }
    }

    private void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(),
                "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

        SqlSessionFactoryBean factory = new FlexSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
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
        return factory.getObject();
    }

    protected void applyConfiguration(SqlSessionFactoryBean factory) {
        MybatisFlexProperties.CoreConfiguration coreConfiguration = this.properties.getConfiguration();
        FlexConfiguration configuration = null;
        if (coreConfiguration != null || !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new FlexConfiguration();
        }
        if (configuration != null && coreConfiguration != null) {
            coreConfiguration.applyTo(configuration);
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

    protected void applySqlSessionFactoryBeanCustomizers(SqlSessionFactoryBean factory) {
        if (!CollectionUtils.isEmpty(this.sqlSessionFactoryBeanCustomizers)) {
            for (SqlSessionFactoryBeanCustomizer customizer : this.sqlSessionFactoryBeanCustomizers) {
                customizer.customize(factory);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * This will just scan the same base package as Spring Boot does. If you want more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed mappers working correctly, out-of-the-box,
     * similar to using Spring Data JPA repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar
        implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar {

        private BeanFactory beanFactory;
        private Environment environment;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
                return;
            }

            logger.debug("Searching for mappers annotated with @Mapper");

            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
            if (logger.isDebugEnabled()) {
                packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
            }

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
            builder.addPropertyValue("processPropertyPlaceHolders", true);
            builder.addPropertyValue("annotationClass", Mapper.class);
            builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
            BeanWrapper beanWrapper = new BeanWrapperImpl(MapperScannerConfigurer.class);
            Set<String> propertyNames = Stream.of(beanWrapper.getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());
            if (propertyNames.contains("lazyInitialization")) {
                // Need to mybatis-spring 2.0.2+
                builder.addPropertyValue("lazyInitialization", "${mybatis.lazy-initialization:false}");
            }
            if (propertyNames.contains("defaultScope")) {
                // Need to mybatis-spring 2.0.6+
                builder.addPropertyValue("defaultScope", "${mybatis.mapper-default-scope:}");
            }

            // for spring-native
            boolean injectSqlSession = environment.getProperty("mybatis.inject-sql-session-on-mapper-scan", Boolean.class,
                Boolean.TRUE);
            if (injectSqlSession && this.beanFactory instanceof ListableBeanFactory) {
                ListableBeanFactory listableBeanFactory = (ListableBeanFactory) this.beanFactory;
                Optional<String> sqlSessionTemplateBeanName = Optional
                    .ofNullable(getBeanNameForType(SqlSessionTemplate.class, listableBeanFactory));
                Optional<String> sqlSessionFactoryBeanName = Optional
                    .ofNullable(getBeanNameForType(SqlSessionFactory.class, listableBeanFactory));
                if (sqlSessionTemplateBeanName.isPresent() || !sqlSessionFactoryBeanName.isPresent()) {
                    builder.addPropertyValue("sqlSessionTemplateBeanName",
                        sqlSessionTemplateBeanName.orElse("sqlSessionTemplate"));
                } else {
                    builder.addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryBeanName.get());
                }
            }
            builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

            registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        private String getBeanNameForType(Class<?> type, ListableBeanFactory factory) {
            String[] beanNames = factory.getBeanNamesForType(type);
            return beanNames.length > 0 ? beanNames[0] : null;
        }

    }

    /**
     * If mapper registering configuration or mapper scanning configuration not present, this configuration allow to scan
     * mappers based on the same component-scanning path as Spring Boot itself.
     */
    @org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
    @Import(AutoConfiguredMapperScannerRegistrar.class)
    @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

        @Override
        public void afterPropertiesSet() {
            logger.debug(
                "Not found configuration for registering mapper bean using @MapperScan, MapperFactoryBean and MapperScannerConfigurer.");
        }

    }


}
