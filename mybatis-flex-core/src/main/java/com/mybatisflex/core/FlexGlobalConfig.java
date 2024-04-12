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
package com.mybatisflex.core;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Listener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.exception.FlexAssert;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 全局配置文件
 */
public class FlexGlobalConfig {

    /**
     * 启动是否打印 banner 和 版本号
     */
    private boolean printBanner = true;

    /**
     * 默认使用 Mysql 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * Mybatis 配置
     */
    private Configuration configuration;

    /**
     * 创建好的 sqlSessionFactory
     */
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 全局的 ID 生成策略配置，当 @Id 未配置 或者 配置 KeyType 为 None 时
     * 使用当前全局配置
     */
    private KeyConfig keyConfig;

    /**
     * entity 的监听器
     */
    private Map<Class<?>, List<SetListener>> entitySetListeners = new ConcurrentHashMap<>();
    private Map<Class<?>, List<UpdateListener>> entityUpdateListeners = new ConcurrentHashMap<>();
    private Map<Class<?>, List<InsertListener>> entityInsertListeners = new ConcurrentHashMap<>();


    /**
     * 逻辑删除的相关配置
     */
    private Object normalValueOfLogicDelete = FlexConsts.LOGIC_DELETE_NORMAL;
    private Object deletedValueOfLogicDelete = FlexConsts.LOGIC_DELETE_DELETED;

    /**
     * 分页查询时，默认每页显示的数据数量。
     */
    private int defaultPageSize = 10;


    /**
     * 默认的 Relation 注解查询深度
     */
    private int defaultRelationQueryDepth = 2;

    /**
     * 默认的逻辑删除字段，允许设置 {@code null} 忽略匹配。
     */
    private String logicDeleteColumn;

    /**
     * 默认的多租户字段，允许设置 {@code null} 忽略匹配。
     */
    private String tenantColumn;

    /**
     * 默认的乐观锁字段，允许设置 {@code null} 忽略匹配。
     */
    private String versionColumn;

    public boolean isPrintBanner() {
        return printBanner;
    }

    public void setPrintBanner(boolean printBanner) {
        this.printBanner = printBanner;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        if (dataSource instanceof FlexDataSource) {
            this.dbType = ((FlexDataSource) dataSource).getDefaultDbType();
        }
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public KeyConfig getKeyConfig() {
        return keyConfig;
    }

    public void setKeyConfig(KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
    }

    public Map<Class<?>, List<SetListener>> getEntitySetListeners() {
        return entitySetListeners;
    }

    public void setEntitySetListeners(Map<Class<?>, List<SetListener>> entitySetListeners) {
        this.entitySetListeners = entitySetListeners;
    }

    public Map<Class<?>, List<UpdateListener>> getEntityUpdateListeners() {
        return entityUpdateListeners;
    }

    public void setEntityUpdateListeners(Map<Class<?>, List<UpdateListener>> entityUpdateListeners) {
        this.entityUpdateListeners = entityUpdateListeners;
    }

    public Map<Class<?>, List<InsertListener>> getEntityInsertListeners() {
        return entityInsertListeners;
    }

    public void setEntityInsertListeners(Map<Class<?>, List<InsertListener>> entityInsertListeners) {
        this.entityInsertListeners = entityInsertListeners;
    }

    public void registerSetListener(SetListener listener, Class<?>... classes) {
        for (Class<?> aClass : classes) {
            entitySetListeners.computeIfAbsent(aClass, k -> new ArrayList<>()).add(listener);
        }
    }

    public void registerUpdateListener(UpdateListener listener, Class<?>... classes) {
        for (Class<?> aClass : classes) {
            entityUpdateListeners.computeIfAbsent(aClass, k -> new ArrayList<>()).add(listener);
        }
    }

    public void registerInsertListener(InsertListener listener, Class<?>... classes) {
        for (Class<?> aClass : classes) {
            entityInsertListeners.computeIfAbsent(aClass, k -> new ArrayList<>()).add(listener);
        }
    }

    public List<SetListener> getSetListener(Class<?> entityClass) {
        return entitySetListeners.get(entityClass);
    }

    /**
     * 获取支持该 {@code entityClass} 的set监听器
     * <p>当registerClass是entityClass的本身或其超类时，则视为支持</p>
     *
     * @param entityClass 实体class
     * @return UpdateListener
     */
    public List<SetListener> getSupportedSetListener(Class<?> entityClass) {
        return this.findSupportedListeners(entityClass, this.entitySetListeners);
    }

    public List<UpdateListener> getUpdateListener(Class<?> entityClass) {
        return entityUpdateListeners.get(entityClass);
    }

    /**
     * 查找支持该 {@code entityClass} 的监听器
     *
     * @param entityClass 实体class
     * @param listenerMap 监听器map
     * @param <T>         监听器类型
     * @return 符合条件的监听器
     */
    public <T extends Listener> List<T> findSupportedListeners(Class<?> entityClass, Map<Class<?>, List<T>> listenerMap) {
        return listenerMap.entrySet()
            .stream()
            .filter(entry -> entry.getKey().isAssignableFrom(entityClass))
            .flatMap(e -> e.getValue().stream())
            .collect(Collectors.toList());
    }

    /**
     * 获取支持该 {@code entityClass} 的update监听器
     * <p>当registerClass是entityClass的本身或其超类时，则视为支持</p>
     *
     * @param entityClass 实体class
     * @return UpdateListener
     */
    public List<UpdateListener> getSupportedUpdateListener(Class<?> entityClass) {
        return this.findSupportedListeners(entityClass, this.entityUpdateListeners);
    }


    public List<InsertListener> getInsertListener(Class<?> entityClass) {
        return entityInsertListeners.get(entityClass);
    }

    /**
     * 获取支持该 {@code entityClass} 的insert监听器
     * <p>当registerClass是entityClass的本身或其超类时，则视为支持</p>
     *
     * @param entityClass 实体class
     * @return InsertListener
     */
    public List<InsertListener> getSupportedInsertListener(Class<?> entityClass) {
        return this.findSupportedListeners(entityClass, this.entityInsertListeners);
    }

    public Object getNormalValueOfLogicDelete() {
        return normalValueOfLogicDelete;
    }

    public void setNormalValueOfLogicDelete(Object normalValueOfLogicDelete) {
        FlexAssert.notNull(normalValueOfLogicDelete, "normalValueOfLogicDelete");
        this.normalValueOfLogicDelete = normalValueOfLogicDelete;
    }

    public Object getDeletedValueOfLogicDelete() {
        return deletedValueOfLogicDelete;
    }

    public void setDeletedValueOfLogicDelete(Object deletedValueOfLogicDelete) {
        FlexAssert.notNull(deletedValueOfLogicDelete, "deletedValueOfLogicDelete");
        this.deletedValueOfLogicDelete = deletedValueOfLogicDelete;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public int getDefaultRelationQueryDepth() {
        return defaultRelationQueryDepth;
    }

    public void setDefaultRelationQueryDepth(int defaultRelationQueryDepth) {
        this.defaultRelationQueryDepth = defaultRelationQueryDepth;
    }

    public String getLogicDeleteColumn() {
        return logicDeleteColumn;
    }

    public void setLogicDeleteColumn(String logicDeleteColumn) {
        this.logicDeleteColumn = logicDeleteColumn;
    }

    public String getTenantColumn() {
        return tenantColumn;
    }

    public void setTenantColumn(String tenantColumn) {
        this.tenantColumn = tenantColumn;
    }

    public String getVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(String versionColumn) {
        this.versionColumn = versionColumn;
    }

    public FlexDataSource getDataSource() {
        return (FlexDataSource) getConfiguration().getEnvironment().getDataSource();
    }

    public static ConcurrentHashMap<String, FlexGlobalConfig> getGlobalConfigs() {
        return globalConfigs;
    }

    public static void setGlobalConfigs(ConcurrentHashMap<String, FlexGlobalConfig> globalConfigs) {
        FlexGlobalConfig.globalConfigs = globalConfigs;
    }


    /**
     * 对应的是 注解 {@link com.mybatisflex.annotation.Id} 的配置
     */
    public static class KeyConfig {

        private KeyType keyType;
        private String value;
        private boolean before = true;

        public KeyType getKeyType() {
            return keyType;
        }

        public void setKeyType(KeyType keyType) {
            this.keyType = keyType;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isBefore() {
            return before;
        }

        public void setBefore(boolean before) {
            this.before = before;
        }

    }


    /////static factory methods/////
    private static ConcurrentHashMap<String, FlexGlobalConfig> globalConfigs = new ConcurrentHashMap<>();
    private static FlexGlobalConfig defaultConfig = new FlexGlobalConfig();

    public static FlexGlobalConfig getDefaultConfig() {
        return defaultConfig;
    }

    public static void setDefaultConfig(FlexGlobalConfig config) {
        if (config == null) {
            throw new NullPointerException("config must not be null.");
        }
        defaultConfig = config;
    }

    public static FlexGlobalConfig getConfig(Configuration configuration) {
        return getConfig(configuration.getEnvironment().getId());
    }

    public static FlexGlobalConfig getConfig(String environmentId) {
        return globalConfigs.get(environmentId);
    }


    /**
     * 设置全局配置
     *
     * @param id        环境id
     * @param config    全局配置
     * @param isDefault 自动指定默认全局配置（在多源时，方便由注解指定默认源）
     */
    public static synchronized void setConfig(String id, FlexGlobalConfig config, boolean isDefault) {
        if (isDefault) {
            defaultConfig.setSqlSessionFactory(config.sqlSessionFactory);
            defaultConfig.setConfiguration(config.configuration);
        }

        globalConfigs.put(id, isDefault ? defaultConfig : config);
    }

}
