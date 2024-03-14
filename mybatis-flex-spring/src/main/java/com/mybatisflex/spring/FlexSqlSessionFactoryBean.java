/*
 * Copyright 2010-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.spring;

import com.github.gavlyukovskiy.boot.jdbc.decorator.DecoratedDataSource;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.mybatis.FlexSqlSessionFactoryBuilder;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;
import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * 参考：https://github.com/mybatis/spring/blob/master/src/main/java/org/mybatis/spring/SqlSessionFactoryBean.java
 *
 * <p>在 MyBatis 官方的 SqlSessionFactoryBean 基础上,替换了  FlexSqlSessionFactoryBean。
 *
 * <p>源于 {@link SqlSessionFactoryBean}，主要是用于构建 {@link com.mybatisflex.core.mybatis.FlexConfiguration }，而不是使用原生的 {@link Configuration}。
 *
 * <p>此代码主要是用于修改 {@link FlexSqlSessionFactoryBean#buildSqlSessionFactory()} 部分。
 *
 * @author Putthiphong Boonphong
 * @author Hunter Presnall
 * @author Eduardo Macarron
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 * @author Jens Schauder
 * @author 王帅
 * @author miachel
 * @author life
 */
public class FlexSqlSessionFactoryBean extends SqlSessionFactoryBean
    implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBean.class);

    private static final String P6SPY_DATA_SOURCE_CLASS = "com.github.gavlyukovskiy.boot.jdbc.decorator.DecoratedDataSource";

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    private Resource configLocation;

    private Configuration configuration;

    private Resource[] mapperLocations;

    private DataSource dataSource;

    private TransactionFactory transactionFactory;

    private Properties configurationProperties;

    //    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new FlexSqlSessionFactoryBuilder();

    private SqlSessionFactory sqlSessionFactory;

    // EnvironmentAware requires spring 3.1
    //    private String environment = SqlSessionFactoryBean.class.getSimpleName();
    private String environment = FlexConsts.NAME;

    private boolean failFast;

    private Interceptor[] plugins;

    private TypeHandler<?>[] typeHandlers;

    private String typeHandlersPackage;

    @SuppressWarnings("rawtypes")
    private Class<? extends TypeHandler> defaultEnumTypeHandler;

    private Class<?>[] typeAliases;

    private String typeAliasesPackage;

    private Class<?> typeAliasesSuperType;

    private LanguageDriver[] scriptingLanguageDrivers;

    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

    // issue #19. No default provider.
    private DatabaseIdProvider databaseIdProvider;

    private Class<? extends VFS> vfs;

    private Cache cache;

    private ObjectFactory objectFactory;

    private ObjectWrapperFactory objectWrapperFactory;

    /**
     * Sets the ObjectFactory.
     *
     * @param objectFactory a custom ObjectFactory
     * @since 1.1.2
     */
    @Override
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    /**
     * Sets the ObjectWrapperFactory.
     *
     * @param objectWrapperFactory a specified ObjectWrapperFactory
     * @since 1.1.2
     */
    @Override
    public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
        this.objectWrapperFactory = objectWrapperFactory;
    }

    /**
     * Gets the DatabaseIdProvider
     *
     * @return a specified DatabaseIdProvider
     * @since 1.1.0
     */
    @Override
    public DatabaseIdProvider getDatabaseIdProvider() {
        return databaseIdProvider;
    }

    /**
     * Sets the DatabaseIdProvider. As of version 1.2.2 this variable is not initialized by default.
     *
     * @param databaseIdProvider a DatabaseIdProvider
     * @since 1.1.0
     */
    @Override
    public void setDatabaseIdProvider(DatabaseIdProvider databaseIdProvider) {
        this.databaseIdProvider = databaseIdProvider;
    }

    /**
     * Gets the VFS.
     *
     * @return a specified VFS
     */
    @Override
    public Class<? extends VFS> getVfs() {
        return this.vfs;
    }

    /**
     * Sets the VFS.
     *
     * @param vfs a VFS
     */
    @Override
    public void setVfs(Class<? extends VFS> vfs) {
        this.vfs = vfs;
    }

    /**
     * Gets the Cache.
     *
     * @return a specified Cache
     */
    @Override
    public Cache getCache() {
        return this.cache;
    }

    /**
     * Sets the Cache.
     *
     * @param cache a Cache
     */
    @Override
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Mybatis plugin list.
     *
     * @param plugins list of plugins
     * @since 1.0.1
     */
    @Override
    public void setPlugins(Interceptor... plugins) {
        this.plugins = plugins;
    }

    /**
     * Packages to search for type aliases.
     *
     * <p>
     * Since 2.0.1, allow to specify a wildcard such as {@code com.example.*.model}.
     *
     * @param typeAliasesPackage package to scan for domain objects
     * @since 1.0.1
     */
    @Override
    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    /**
     * Super class which domain objects have to extend to have a type alias created. No effect if there is no package to
     * scan configured.
     *
     * @param typeAliasesSuperType super class for domain objects
     * @since 1.1.2
     */
    @Override
    public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
        this.typeAliasesSuperType = typeAliasesSuperType;
    }

    /**
     * Packages to search for type handlers.
     *
     * <p>
     * Since 2.0.1, allow to specify a wildcard such as {@code com.example.*.typehandler}.
     *
     * @param typeHandlersPackage package to scan for type handlers
     * @since 1.0.1
     */
    @Override
    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    /**
     * Set type handlers. They must be annotated with {@code MappedTypes} and optionally with {@code MappedJdbcTypes}
     *
     * @param typeHandlers Type handler list
     * @since 1.0.1
     */
    @Override
    public void setTypeHandlers(TypeHandler<?>... typeHandlers) {
        this.typeHandlers = typeHandlers;
    }

    /**
     * Set the default type handler class for enum.
     *
     * @param defaultEnumTypeHandler The default type handler class for enum
     * @since 2.0.5
     */
    @Override
    public void setDefaultEnumTypeHandler(
        Class<? extends TypeHandler> defaultEnumTypeHandler
    ) {
        this.defaultEnumTypeHandler = defaultEnumTypeHandler;
    }

    /**
     * List of type aliases to register. They can be annotated with {@code Alias}
     *
     * @param typeAliases Type aliases list
     * @since 1.0.1
     */
    @Override
    public void setTypeAliases(Class<?>... typeAliases) {
        this.typeAliases = typeAliases;
    }

    /**
     * If true, a final check is done on Configuration to assure that all mapped statements are fully loaded and there is
     * no one still pending to resolve includes. Defaults to false.
     *
     * @param failFast enable failFast
     * @since 1.0.1
     */
    @Override
    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    /**
     * Set the location of the MyBatis {@code SqlSessionFactory} config file. A typical value is
     * "WEB-INF/mybatis-configuration.xml".
     *
     * @param configLocation a location the MyBatis config file
     */
    @Override
    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Set a customized MyBatis configuration.
     *
     * @param configuration MyBatis configuration
     * @since 1.3.0
     */
    @Override
    public void setConfiguration(Configuration configuration) {
        if (configuration != null && !(configuration instanceof FlexConfiguration)) {
            throw new IllegalArgumentException("Only support FlexConfiguration.");
        }

        this.configuration = configuration;
    }

    /**
     * Set locations of MyBatis mapper files that are going to be merged into the {@code SqlSessionFactory} configuration
     * at runtime.
     * <p>
     * This is an alternative to specifying "&lt;sqlmapper&gt;" entries in an MyBatis config file. This property being
     * based on Spring's resource abstraction also allows for specifying resource patterns here: e.g.
     * "classpath*:sqlmap/*-mapper.xml".
     *
     * @param mapperLocations location of MyBatis mapper files
     */
    @Override
    public void setMapperLocations(Resource... mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    /**
     * Set optional properties to be passed into the SqlSession configuration, as alternative to a
     * {@code &lt;properties&gt;} tag in the configuration xml file. This will be used to resolve placeholders in the
     * config file.
     *
     * @param sqlSessionFactoryProperties optional properties for the SqlSessionFactory
     */
    @Override
    public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
        this.configurationProperties = sqlSessionFactoryProperties;
    }

    /**
     * Set the JDBC {@code DataSource} that this instance should manage transactions for. The {@code DataSource} should
     * match the one used by the {@code SqlSessionFactory}: for example, you could specify the same JNDI DataSource for
     * both.
     * <p>
     * A transactional JDBC {@code Connection} for this {@code DataSource} will be provided to application code accessing
     * this {@code DataSource} directly via {@code DataSourceUtils} or {@code DataSourceTransactionManager}.
     * <p>
     * The {@code DataSource} specified here should be the target {@code DataSource} to manage transactions for, not a
     * {@code TransactionAwareDataSourceProxy}. Only data access code may work with
     * {@code TransactionAwareDataSourceProxy}, while the transaction manager needs to work on the underlying target
     * {@code DataSource}. If there's nevertheless a {@code TransactionAwareDataSourceProxy} passed in, it will be
     * unwrapped to extract its target {@code DataSource}.
     *
     * @param dataSource a JDBC {@code DataSource}
     */
    @Override
    public void setDataSource(DataSource dataSource) {
        if (dataSource instanceof TransactionAwareDataSourceProxy) {
            // If we got a TransactionAwareDataSourceProxy, we need to perform
            // transactions for its underlying target DataSource, else data
            // access code won't see properly exposed transactions (i.e.
            // transactions for the target DataSource).
            this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
        } else {
            this.dataSource = dataSource;
        }
    }

    /**
     * Sets the {@code SqlSessionFactoryBuilder} to use when creating the {@code SqlSessionFactory}.
     * <p>
     * This is mainly meant for testing so that mock SqlSessionFactory classes can be injected. By default,
     * {@code SqlSessionFactoryBuilder} creates {@code DefaultSqlSessionFactory} instances.
     *
     * @param sqlSessionFactoryBuilder a SqlSessionFactoryBuilder
     */
    @Override
    public void setSqlSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
        this.sqlSessionFactoryBuilder = sqlSessionFactoryBuilder;
    }

    /**
     * Set the MyBatis TransactionFactory to use. Default is {@code SpringManagedTransactionFactory}
     * <p>
     * The default {@code SpringManagedTransactionFactory} should be appropriate for all cases: be it Spring transaction
     * management, EJB CMT or plain JTA. If there is no active transaction, SqlSession operations will execute SQL
     * statements non-transactionally.
     *
     * <b>It is strongly recommended to use the default {@code TransactionFactory}.</b> If not used, any attempt at
     * getting an SqlSession through Spring's MyBatis framework will throw an exception if a transaction is active.
     *
     * @param transactionFactory the MyBatis TransactionFactory
     * @see SpringManagedTransactionFactory
     */
    @Override
    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    /**
     * <b>NOTE:</b> This class <em>overrides</em> any {@code Environment} you have set in the MyBatis config file. This is
     * used only as a placeholder name. The default value is {@code SqlSessionFactoryBean.class.getSimpleName()}.
     *
     * @param environment the environment name
     */
    @Override
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * Set scripting language drivers.
     *
     * @param scriptingLanguageDrivers scripting language drivers
     * @since 2.0.2
     */
    @Override
    public void setScriptingLanguageDrivers(LanguageDriver... scriptingLanguageDrivers) {
        this.scriptingLanguageDrivers = scriptingLanguageDrivers;
    }

    /**
     * Set a default scripting language driver class.
     *
     * @param defaultScriptingLanguageDriver A default scripting language driver class
     * @since 2.0.2
     */
    @Override
    public void setDefaultScriptingLanguageDriver(Class<? extends LanguageDriver> defaultScriptingLanguageDriver) {
        this.defaultScriptingLanguageDriver = defaultScriptingLanguageDriver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(dataSource, "Property 'dataSource' is required");
        notNull(sqlSessionFactoryBuilder, "Property 'sqlSessionFactoryBuilder' is required");
        state(
            (configuration == null && configLocation == null) || !(configuration != null && configLocation != null),
            "Property 'configuration' and 'configLocation' can not specified with together"
        );

        this.sqlSessionFactory = buildSqlSessionFactory();
    }

    /**
     * Build a {@code SqlSessionFactory} instance.
     * <p>
     * The default implementation uses the standard MyBatis {@code XMLConfigBuilder} API to build a
     * {@code SqlSessionFactory} instance based on a Reader. Since 1.3.0, it can be specified a {@link Configuration}
     * instance directly(without config file).
     *
     * @return SqlSessionFactory
     * @throws Exception if configuration is failed
     */
    @Override
    protected SqlSessionFactory buildSqlSessionFactory() throws Exception {
        final Configuration targetConfiguration;

        XMLConfigBuilder xmlConfigBuilder = null;
        if (this.configuration != null) {
            targetConfiguration = this.configuration;

            if (targetConfiguration.getVariables() == null) {
                targetConfiguration.setVariables(this.configurationProperties);
            } else if (this.configurationProperties != null) {
                targetConfiguration.getVariables().putAll(this.configurationProperties);
            }
        } else if (this.configLocation != null) {
            xmlConfigBuilder = new XMLConfigBuilder(FlexConfiguration.class, this.configLocation.getInputStream(), null, this.configurationProperties);

            targetConfiguration = xmlConfigBuilder.getConfiguration();
        } else {
            LOGGER.debug(
                () -> "Property 'configuration' or 'configLocation' not specified, using default Flex Configuration");

            //            targetConfiguration = new Configuration();
            targetConfiguration = new FlexConfiguration();

            Optional.ofNullable(this.configurationProperties).ifPresent(targetConfiguration::setVariables);
        }

        Optional.ofNullable(this.objectFactory).ifPresent(targetConfiguration::setObjectFactory);
        Optional.ofNullable(this.objectWrapperFactory).ifPresent(targetConfiguration::setObjectWrapperFactory);
        Optional.ofNullable(this.vfs).ifPresent(targetConfiguration::setVfsImpl);

        if (hasLength(this.typeAliasesPackage)) {
            scanClasses(this.typeAliasesPackage, this.typeAliasesSuperType).stream()
                .filter(clazz -> !clazz.isAnonymousClass()).filter(clazz -> !clazz.isInterface())
                .filter(clazz -> !clazz.isMemberClass()).forEach(targetConfiguration.getTypeAliasRegistry()::registerAlias);
        }

        if (!isEmpty(this.typeAliases)) {
            Stream.of(this.typeAliases).forEach(typeAlias -> {
                targetConfiguration.getTypeAliasRegistry().registerAlias(typeAlias);

                LOGGER.debug(() -> "Registered type alias: '" + typeAlias + "'");
            });
        }

        if (!isEmpty(this.plugins)) {
            Stream.of(this.plugins).forEach(plugin -> {
                targetConfiguration.addInterceptor(plugin);

                LOGGER.debug(() -> "Registered plugin: '" + plugin + "'");
            });
        }

        if (hasLength(this.typeHandlersPackage)) {
            scanClasses(this.typeHandlersPackage, TypeHandler.class).stream().filter(clazz -> !clazz.isAnonymousClass())
                .filter(clazz -> !clazz.isInterface()).filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .forEach(targetConfiguration.getTypeHandlerRegistry()::register);
        }

        if (!isEmpty(this.typeHandlers)) {
            Stream.of(this.typeHandlers).forEach(typeHandler -> {
                targetConfiguration.getTypeHandlerRegistry().register(typeHandler);

                LOGGER.debug(() -> "Registered type handler: '" + typeHandler + "'");
            });
        }

        targetConfiguration.setDefaultEnumTypeHandler(defaultEnumTypeHandler);

        if (!isEmpty(this.scriptingLanguageDrivers)) {
            Stream.of(this.scriptingLanguageDrivers).forEach(languageDriver -> {
                targetConfiguration.getLanguageRegistry().register(languageDriver);

                LOGGER.debug(() -> "Registered scripting language driver: '" + languageDriver + "'");
            });
        }

        Optional.ofNullable(this.defaultScriptingLanguageDriver)
            .ifPresent(targetConfiguration::setDefaultScriptingLanguage);

        if (this.databaseIdProvider != null) {// fix #64 set databaseId before parse mapper xmls
            try {
                targetConfiguration.setDatabaseId(this.databaseIdProvider.getDatabaseId(this.dataSource));
            } catch (SQLException e) {
                throw new IOException("Failed getting a databaseId", e);
            }
        }

        Optional.ofNullable(this.cache).ifPresent(targetConfiguration::addCache);

        if (xmlConfigBuilder != null) {
            try {
                xmlConfigBuilder.parse();

                LOGGER.debug(() -> "Parsed configuration file: '" + this.configLocation + "'");
            } catch (Exception ex) {
                throw new IOException("Failed to parse config resource: " + this.configLocation, ex);
            } finally {
                ErrorContext.instance().reset();
            }
        }

        // 事务由 flex 管理了，无需使用 SpringManagedTransactionFactory，否则会造成在同一个事务下，无法切换数据源的问题
        // fixed https://gitee.com/mybatis-flex/mybatis-flex/issues/I70QWU
        // 兼容SpringManagedTransactionFactory否则在使用JdbcTemplate,多数据源使用JdbcTemplate报错
        // fixed https://gitee.com/mybatis-flex/mybatis-flex/issues/I7HJ4J

        FlexDataSource flexDataSource;

        if (dataSource instanceof FlexDataSource) {
            flexDataSource = (FlexDataSource) dataSource;
        } else {
            flexDataSource = new FlexDataSource(FlexConsts.NAME, dataSource);

            // 解决 p6spy 下多数据源获取 DbType 失败的问题
            // 首先判断一下是否引入了 p6spy 的包，避免非 p6spy 环境报异常
            if (ClassUtils.isPresent(P6SPY_DATA_SOURCE_CLASS, this.getClass().getClassLoader())) {
                if (dataSource instanceof DecoratedDataSource) {
                    DecoratedDataSource decoratedDataSource = (DecoratedDataSource) dataSource;

                    // 取出被 p6spy 包装的真实数据源
                    if (decoratedDataSource.getRealDataSource() instanceof FlexDataSource) {
                        FlexDataSource realDataSource = (FlexDataSource) decoratedDataSource.getRealDataSource();

                        // 将原始 dbTypeHashMap 附加回来
                        flexDataSource.getDbTypeHashMap().putAll(realDataSource.getDbTypeHashMap());
                    }
                }
            }
        }

        targetConfiguration.setEnvironment(
            new Environment(
                this.environment,
                this.transactionFactory == null ? new FlexTransactionFactory() : this.transactionFactory,
                flexDataSource
            )
        );

        // 需先构建 sqlSessionFactory，再去初始化 mapperLocations
        // 因为 xmlMapperBuilder.parse() 用到 FlexGlobalConfig， FlexGlobalConfig 的初始化是在 sqlSessionFactory 的构建方法里进行的
        // fixed https://gitee.com/mybatis-flex/mybatis-flex/issues/I6X59V
        SqlSessionFactory sqlSessionFactory = this.sqlSessionFactoryBuilder.build(targetConfiguration);

        if (this.mapperLocations != null) {
            if (this.mapperLocations.length == 0) {
                LOGGER.warn(() -> "Property 'mapperLocations' was specified but matching resources are not found.");
            } else {
                for (Resource mapperLocation : this.mapperLocations) {
                    if (mapperLocation == null) {
                        continue;
                    }
                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(
                            mapperLocation.getInputStream(),
                            targetConfiguration,
                            mapperLocation.toString(),
                            targetConfiguration.getSqlFragments()
                        );

                        xmlMapperBuilder.parse();
                    } catch (Exception e) {
                        throw new IOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                    } finally {
                        ErrorContext.instance().reset();
                    }

                    LOGGER.debug(() -> "Parsed mapper file: '" + mapperLocation + "'");
                }
            }
        } else {
            LOGGER.debug(() -> "Property 'mapperLocations' was not specified.");
        }

        return sqlSessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlSessionFactory getObject() throws Exception {
        if (this.sqlSessionFactory == null) {
            afterPropertiesSet();
        }

        return this.sqlSessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends SqlSessionFactory> getObjectType() {
        return this.sqlSessionFactory == null ? SqlSessionFactory.class : this.sqlSessionFactory.getClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (failFast) {
            // fail-fast -> check all statements are completed
            this.sqlSessionFactory.getConfiguration().getMappedStatementNames();
        }
    }

    private Set<Class<?>> scanClasses(String packagePatterns, Class<?> assignableType) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        String[] packagePatternArray = tokenizeToStringArray(packagePatterns, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);

        for (String packagePattern : packagePatternArray) {
            Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(
                ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(packagePattern)
                + "/**/*.class"
            );

            for (Resource resource : resources) {
                try {
                    ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Resources.classForName(classMetadata.getClassName());

                    if (assignableType == null || assignableType.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                    }
                } catch (Throwable e) {
                    LOGGER.warn(() -> "Cannot load the '" + resource + "'. Cause by " + e.toString());
                }
            }
        }

        return classes;
    }

}
