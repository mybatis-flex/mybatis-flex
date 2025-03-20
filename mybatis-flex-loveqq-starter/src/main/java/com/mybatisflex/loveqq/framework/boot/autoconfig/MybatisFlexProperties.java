/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.loveqq.framework.boot.autoconfig;

import com.kfyty.loveqq.framework.boot.data.orm.mybatis.autoconfig.MybatisProperties;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.ConfigurationProperties;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.NestedConfigurationProperty;
import com.kfyty.loveqq.framework.core.lang.util.Mapping;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import lombok.Data;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Mybatis-Flex 的配置属性。
 * 参考自 mybatis-flex-spring-boot-starter
 *
 * @author kfyty725
 */
@Data
@Component
@ConfigurationProperties("mybatis-flex")
public class MybatisFlexProperties {
    /**
     * 默认数据源 key
     */
    private String defaultDatasourceKey;

    /**
     * <p>多数据源的配置。
     *
     * <p>
     * mybatis-flex.datasource.ds1.url=***<br>
     * mybatis-flex.datasource.ds2.url=***
     */
    private Map<String, Map<String, String>> datasource;

    /**
     * 全局配置。
     */
    private GlobalConfig globalConfig;

    /**
     * MyBatis-Flex-Admin 配置。
     */
    private AdminConfig adminConfig;

    /**
     * Location of MyBatis xml config file.
     */
    private String configLocation;

    /**
     * Locations of MyBatis mapper files.
     */
    private String[] mapperLocations = new String[]{"mapper/**/*.xml"};

    /**
     * Packages to search type aliases. (Package delimiters are ",; \t\n")
     */
    private String typeAliasesPackage;

    /**
     * The super class for filtering type alias. If this not specifies, the MyBatis deal as type alias all classes that
     * searched from typeAliasesPackage.
     */
    private Class<?> typeAliasesSuperType;

    /**
     * Packages to search for type handlers. (Package delimiters are ",; \t\n")
     */
    private String typeHandlersPackage;

    /**
     * Indicates whether perform presence check of the MyBatis xml config file.
     */
    private boolean checkConfigLocation = false;

    /**
     * Execution mode for {@link com.kfyty.loveqq.framework.boot.data.orm.mybatis.autoconfig.support.ConcurrentSqlSession}.
     */
    private ExecutorType executorType;

    /**
     * The default scripting language driver class. (Available when use together with mybatis-spring 2.0.2+)
     */
    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

    /**
     * Externalized properties for MyBatis configuration.
     */
    private Properties configurationProperties;

    /**
     * A Configuration object for customize default settings. If {@link #configLocation} is specified, this property is
     * not used.
     */
    private CoreConfiguration configuration;

    /**
     * 合并到 {@link MybatisProperties} 配置
     *
     * @param properties 配置
     */
    void applyTo(MybatisProperties properties) {
        Mapping.from(getConfigLocation()).whenNotNull(properties::setConfigLocation);
        Mapping.from(getExecutorType()).whenNotNull(properties::setExecutorType);
        Mapping.from(getTypeAliasesPackage()).whenNotNull(properties::setTypeAliasesPackage);
        Mapping.from(getTypeHandlersPackage()).whenNotNull(properties::setTypeHandlersPackage);
        Mapping.from(getMapperLocations()).whenNotNull(properties::setMapperLocations);
        Mapping.from(getDefaultScriptingLanguageDriver()).whenNotNull(properties::setDefaultScriptingLanguageDriver);
        Mapping.from(getConfigurationProperties()).whenNotNull(properties::setConfigurationProperties);
        Mapping.from(getConfiguration()).notNullMap(CoreConfiguration::getVfsImpl).whenNotNull(properties::setVfs);
    }

    /**
     * The configuration properties for mybatis core module.
     *
     * @since 3.0.0
     */
    @Data
    @NestedConfigurationProperty
    public static class CoreConfiguration {
        /**
         * Allows using RowBounds on nested statements. If allow, set the false. Default is false.
         */
        private Boolean safeRowBoundsEnabled;

        /**
         * Allows using ResultHandler on nested statements. If allow, set the false. Default is true.
         */
        private Boolean safeResultHandlerEnabled;

        /**
         * Enables automatic mapping from classic database column names A_COLUMN to camel case classic Java property names
         * aColumn. Default is true.
         */
        private Boolean mapUnderscoreToCamelCase = true;

        /**
         * When enabled, any method call will load all the lazy properties of the object. Otherwise, each property is loaded
         * on demand (see also lazyLoadTriggerMethods). Default is false.
         */
        private Boolean aggressiveLazyLoading;

        /**
         * Allows or disallows multiple ResultSets to be returned from a single statement (compatible driver required).
         * Default is true.
         */
        private Boolean multipleResultSetsEnabled;

        /**
         * Allows JDBC support for generated keys. A compatible driver is required. This setting forces generated keys to be
         * used if set to true, as some drivers deny compatibility but still work (e.g. Derby). Default is false.
         */
        private Boolean useGeneratedKeys;

        /**
         * Uses the column label instead of the column name. Different drivers behave differently in this respect. Refer to
         * the driver documentation, or test out both modes to determine how your driver behaves. Default is true.
         */
        private Boolean useColumnLabel;

        /**
         * Globally enables or disables any caches configured in any mapper under this configuration. Default is true.
         */
        private Boolean cacheEnabled;

        /**
         * Specifies if setters or map's put method will be called when a retrieved value is null. It is useful when you
         * rely on Map.keySet() or null value initialization. Note primitives such as (int,boolean,etc.) will not be set to
         * null. Default is false.
         */
        private Boolean callSettersOnNulls;

        /**
         * Allow referencing statement parameters by their actual names declared in the method signature. To use this
         * feature, your project must be compiled in Java 8 with -parameters option. Default is true.
         */
        private Boolean useActualParamName;

        /**
         * MyBatis, by default, returns null when all the columns of a returned row are NULL. When this setting is enabled,
         * MyBatis returns an empty instance instead. Note that it is also applied to nested results (i.e. collectioin and
         * association). Default is false.
         */
        private Boolean returnInstanceForEmptyRow;

        /**
         * Removes extra whitespace characters from the SQL. Note that this also affects literal strings in SQL. Default is
         * false.
         */
        private Boolean shrinkWhitespacesInSql;

        /**
         * Specifies the default value of 'nullable' attribute on 'foreach' tag. Default is false.
         */
        private Boolean nullableOnForEach;

        /**
         * When applying constructor auto-mapping, argument name is used to search the column to map instead of relying on
         * the column order. Default is false.
         */
        private Boolean argNameBasedConstructorAutoMapping;

        /**
         * Globally enables or disables lazy loading. When enabled, all relations will be lazily loaded. This value can be
         * superseded for a specific relation by using the fetchType attribute on it. Default is False.
         */
        private Boolean lazyLoadingEnabled;

        /**
         * Sets the number of seconds the driver will wait for a response from the database.
         */
        private Integer defaultStatementTimeout;

        /**
         * Sets the driver a hint as to control fetching size for return results. This parameter value can be override by a
         * query setting.
         */
        private Integer defaultFetchSize;

        /**
         * MyBatis uses local cache to prevent circular references and speed up repeated nested queries. By default
         * (SESSION) all queries executed during a session are cached. If localCacheScope=STATEMENT local session will be
         * used just for statement execution, no data will be shared between two different calls to the same SqlSession.
         * Default is SESSION.
         */
        private LocalCacheScope localCacheScope;

        /**
         * Specifies the JDBC type for null values when no specific JDBC type was provided for the parameter. Some drivers
         * require specifying the column JDBC type but others work with generic values like NULL, VARCHAR or OTHER. Default
         * is OTHER.
         */
        private JdbcType jdbcTypeForNull;

        /**
         * Specifies a scroll strategy when omit it per statement settings.
         */
        private ResultSetType defaultResultSetType;

        /**
         * Configures the default executor. SIMPLE executor does nothing special. REUSE executor reuses prepared statements.
         * BATCH executor reuses statements and batches updates. Default is SIMPLE.
         */
        private ExecutorType defaultExecutorType;

        /**
         * Specifies if and how MyBatis should automatically map columns to fields/properties. NONE disables auto-mapping.
         * PARTIAL will only auto-map results with no nested result mappings defined inside. FULL will auto-map result
         * mappings of any complexity (containing nested or otherwise). Default is PARTIAL.
         */
        private AutoMappingBehavior autoMappingBehavior;

        /**
         * Specify the behavior when detects an unknown column (or unknown property type) of automatic mapping target.
         * Default is NONE.
         */
        private AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior;

        /**
         * Specifies the prefix string that MyBatis will add to the logger names.
         */
        private String logPrefix;

        /**
         * Specifies which Object's methods trigger a lazy load. Default is [equals,clone,hashCode,toString].
         */
        private Set<String> lazyLoadTriggerMethods;

        /**
         * Specifies which logging implementation MyBatis should use. If this setting is not present logging implementation
         * will be autodiscovered.
         */
        private Class<? extends Log> logImpl;

        /**
         * Specifies VFS implementations.
         */
        private Class<? extends VFS> vfsImpl;

        /**
         * Specifies an sql provider class that holds provider method. This class apply to the type(or value) attribute on
         * sql provider annotation(e.g. @SelectProvider), when these attribute was omitted.
         */
        private Class<?> defaultSqlProviderType;

        /**
         * Specifies the TypeHandler used by default for Enum.
         */
        Class<? extends TypeHandler> defaultEnumTypeHandler;

        /**
         * Specifies the class that provides an instance of Configuration. The returned Configuration instance is used to
         * load lazy properties of deserialized objects. This class must have a method with a signature static Configuration
         * getConfiguration().
         */
        private Class<?> configurationFactory;

        /**
         * Specify any configuration variables.
         */
        private Properties variables;

        void applyTo(Configuration target) {
            Mapping.from(getSafeRowBoundsEnabled()).whenNotNull(target::setSafeRowBoundsEnabled);
            Mapping.from(getSafeResultHandlerEnabled()).whenNotNull(target::setSafeResultHandlerEnabled);
            Mapping.from(getMapUnderscoreToCamelCase()).whenNotNull(target::setMapUnderscoreToCamelCase);
            Mapping.from(getAggressiveLazyLoading()).whenNotNull(target::setAggressiveLazyLoading);
            Mapping.from(getMultipleResultSetsEnabled()).whenNotNull(target::setMultipleResultSetsEnabled);
            Mapping.from(getUseGeneratedKeys()).whenNotNull(target::setUseGeneratedKeys);
            Mapping.from(getUseColumnLabel()).whenNotNull(target::setUseColumnLabel);
            Mapping.from(getCacheEnabled()).whenNotNull(target::setCacheEnabled);
            Mapping.from(getCallSettersOnNulls()).whenNotNull(target::setCallSettersOnNulls);
            Mapping.from(getUseActualParamName()).whenNotNull(target::setUseActualParamName);
            Mapping.from(getReturnInstanceForEmptyRow()).whenNotNull(target::setReturnInstanceForEmptyRow);
            Mapping.from(getShrinkWhitespacesInSql()).whenNotNull(target::setShrinkWhitespacesInSql);
            Mapping.from(getNullableOnForEach()).whenNotNull(target::setNullableOnForEach);
            Mapping.from(getArgNameBasedConstructorAutoMapping()).whenNotNull(target::setArgNameBasedConstructorAutoMapping);
            Mapping.from(getLazyLoadingEnabled()).whenNotNull(target::setLazyLoadingEnabled);
            Mapping.from(getLogPrefix()).whenNotNull(target::setLogPrefix);
            Mapping.from(getLazyLoadTriggerMethods()).whenNotNull(target::setLazyLoadTriggerMethods);
            Mapping.from(getDefaultStatementTimeout()).whenNotNull(target::setDefaultStatementTimeout);
            Mapping.from(getDefaultFetchSize()).whenNotNull(target::setDefaultFetchSize);
            Mapping.from(getLocalCacheScope()).whenNotNull(target::setLocalCacheScope);
            Mapping.from(getJdbcTypeForNull()).whenNotNull(target::setJdbcTypeForNull);
            Mapping.from(getDefaultResultSetType()).whenNotNull(target::setDefaultResultSetType);
            Mapping.from(getDefaultExecutorType()).whenNotNull(target::setDefaultExecutorType);
            Mapping.from(getAutoMappingBehavior()).whenNotNull(target::setAutoMappingBehavior);
            Mapping.from(getAutoMappingUnknownColumnBehavior()).whenNotNull(target::setAutoMappingUnknownColumnBehavior);
            Mapping.from(getVariables()).whenNotNull(target::setVariables);
            Mapping.from(getLogImpl()).whenNotNull(target::setLogImpl);
            Mapping.from(getVfsImpl()).whenNotNull(target::setVfsImpl);
            Mapping.from(getDefaultSqlProviderType()).whenNotNull(target::setDefaultSqlProviderType);
            Mapping.from(getConfigurationFactory()).whenNotNull(target::setConfigurationFactory);
            Mapping.from(getDefaultEnumTypeHandler()).whenNotNull(target::setDefaultEnumTypeHandler);
        }
    }

    /**
     * {@link FlexGlobalConfig} 配置。
     *
     * @author 王帅
     * @author kfyty725
     * @since 2023-06-21
     */
    @Data
    @NestedConfigurationProperty
    public static class GlobalConfig {
        /**
         * 启动是否打印 banner 和 版本号。
         */
        private boolean printBanner = true;

        /**
         * 全局的 ID 生成策略配置，当 @Id 未配置 或者 配置 KeyType 为 None 时
         * 使用当前全局配置。
         */
        @NestedConfigurationProperty
        private FlexGlobalConfig.KeyConfig keyConfig;

        /**
         * 逻辑删除数据存在标记值。
         */
        private Object normalValueOfLogicDelete = FlexConsts.LOGIC_DELETE_NORMAL;

        /**
         * 逻辑删除数据删除标记值。
         */
        private Object deletedValueOfLogicDelete = FlexConsts.LOGIC_DELETE_DELETED;


        /**
         * 默认的分页查询时的每页数据量。
         */
        private int defaultPageSize = 10;


        /**
         * 默认的 Relation 注解查询深度。
         */
        private int defaultRelationQueryDepth = 2;

        /**
         * 默认的逻辑删除字段。
         */
        private String logicDeleteColumn;

        /**
         * 默认的多租户字段。
         */
        private String tenantColumn;

        /**
         * 默认的乐观锁字段。
         */
        private String versionColumn;

        /**
         * 全局忽略 @Table 中配置的 schema
         */
        private boolean ignoreSchema = false;

        void applyTo(FlexGlobalConfig target) {
            Mapping.from(isPrintBanner()).whenNotNull(target::setPrintBanner);
            Mapping.from(getKeyConfig()).whenNotNull(target::setKeyConfig);
            Mapping.from(getNormalValueOfLogicDelete()).whenNotNull(target::setNormalValueOfLogicDelete);
            Mapping.from(getDeletedValueOfLogicDelete()).whenNotNull(target::setDeletedValueOfLogicDelete);
            Mapping.from(getDefaultPageSize()).whenNotNull(target::setDefaultPageSize);
            Mapping.from(getDefaultRelationQueryDepth()).whenNotNull(target::setDefaultRelationQueryDepth);
            Mapping.from(getLogicDeleteColumn()).whenNotNull(target::setLogicDeleteColumn);
            Mapping.from(getVersionColumn()).whenNotNull(target::setVersionColumn);
            Mapping.from(getTenantColumn()).whenNotNull(target::setTenantColumn);
            Mapping.from(isIgnoreSchema()).whenNotNull(target::setIgnoreSchema);
        }
    }

    /**
     * MyBatis Flex Admin 配置。
     *
     * @author 王帅
     * @author kfyty725
     * @since 2023-07-02
     */
    @Data
    @NestedConfigurationProperty
    public static class AdminConfig {
        /**
         * 启用服务。
         */
        private boolean enable;

        /**
         * 连接端点。
         */
        private String endpoint;

        /**
         * 秘密密钥。
         */
        private String secretKey;
    }
}
