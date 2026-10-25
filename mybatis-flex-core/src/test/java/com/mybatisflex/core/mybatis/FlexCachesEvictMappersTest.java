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
package com.mybatisflex.core.mybatis;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.FlexCaches;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.coretest.Article;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/** Integration coverage for plugin-scoped cache eviction. */
public class FlexCachesEvictMappersTest {

    private static final String ENVIRONMENT_ID = "plugin-cache-eviction-test";
    private static final String SECOND_ENVIRONMENT_ID = "plugin-cache-eviction-test-2";

    private FlexConfiguration configuration;
    private FlexConfiguration secondConfiguration;
    private FlexGlobalConfig environmentConfig;
    private FlexGlobalConfig defaultConfig;
    private Class<?> isolatedEntityClass;
    private TableInfo pluginTableInfo;
    private TableInfo articleTableInfo;

    @Before
    public void setUp() throws Exception {
        TableInfoFactory.clear();
        FlexConfiguration.clearDynamicMappedStatementCache();
        configuration = new FlexConfiguration(new Environment(ENVIRONMENT_ID,
            new JdbcTransactionFactory(), mockDataSource()));
        defaultConfig = FlexGlobalConfig.getDefaultConfig();
        environmentConfig = new FlexGlobalConfig();
        environmentConfig.setConfiguration(configuration);
        environmentConfig.setSqlSessionFactory(mockSqlSessionFactory(configuration));
        environmentConfig.registerInsertListener((InsertListener) entity -> { }, PluginEntity.class);
        FlexGlobalConfig.getGlobalConfigs().put(ENVIRONMENT_ID, environmentConfig);

        secondConfiguration = new FlexConfiguration(new Environment(SECOND_ENVIRONMENT_ID,
            new JdbcTransactionFactory(), mockDataSource()));
        secondConfiguration.getMapperRegistry().addMapper(PluginMapper.class);
        FlexGlobalConfig secondEnvironmentConfig = new FlexGlobalConfig();
        secondEnvironmentConfig.setConfiguration(secondConfiguration);
        secondEnvironmentConfig.setSqlSessionFactory(mockSqlSessionFactory(secondConfiguration));
        FlexGlobalConfig.getGlobalConfigs().put(SECOND_ENVIRONMENT_ID, secondEnvironmentConfig);

        Mappers.addMapping(PluginEntity.class, PluginMapper.class);
        Mappers.addMapping(Article.class, ArticleMapper.class);
        pluginTableInfo = TableInfoFactory.ofMapperClass(PluginMapper.class);
        articleTableInfo = TableInfoFactory.ofMapperClass(ArticleMapper.class);
        Mappers.ofMapperClass(ENVIRONMENT_ID, PluginMapper.class);
        Mappers.ofMapperClass(ENVIRONMENT_ID, ArticleMapper.class);

        pluginTableInfo.invokeOnInsertListener(new PluginEntity());
        isolatedEntityClass = duplicateClass(PluginEntity.class);
        addDynamicStatement("plugin.select:" + PluginEntity.class.getName(), PluginEntity.class);
        addDynamicStatement("isolated.select:" + isolatedEntityClass.getName(), isolatedEntityClass);
        addDynamicStatement("article.select:" + Article.class.getName(), Article.class);
        addResultMap(configuration, PluginEntity.class);
        addResultMap(configuration, Article.class);
    }

    @After
    public void tearDown() {
        FlexGlobalConfig.removeConfig(ENVIRONMENT_ID);
        FlexGlobalConfig.removeConfig(SECOND_ENVIRONMENT_ID);
        Set<Class<?>> mapperClasses = new HashSet<>();
        mapperClasses.add(PluginMapper.class);
        mapperClasses.add(ArticleMapper.class);
        Mappers.evictMappers(mapperClasses);
        TableInfo.evictListeners(Collections.<Class<?>>singleton(PluginEntity.class));
        TableInfo.evictListeners(Collections.<Class<?>>singleton(Article.class));
        FlexConfiguration.clearDynamicMappedStatementCache();
        TableInfoFactory.clear();
    }

    @Test
    public void evictsOnlyTheSpecifiedPluginClassesAndEnvironment() throws Exception {
        Set<Class<?>> mapperClasses = Collections.<Class<?>>singleton(PluginMapper.class);

        String report = FlexCaches.evictMappers(ENVIRONMENT_ID, mapperClasses);

        Assert.assertTrue(report, report.contains("Environment=2"));
        Assert.assertTrue(report, report.contains("Entity=1"));
        Assert.assertNull(FlexGlobalConfig.getConfig(ENVIRONMENT_ID));
        Assert.assertNull(FlexGlobalConfig.getConfig(SECOND_ENVIRONMENT_ID));
        Assert.assertSame("The shared default configuration must not change",
            defaultConfig, FlexGlobalConfig.getDefaultConfig());

        Assert.assertNotSame("The plugin entity TableInfo should be rebuilt after eviction",
            pluginTableInfo, TableInfoFactory.ofEntityClass(PluginEntity.class));
        Assert.assertSame("Unrelated entity metadata should remain cached",
            articleTableInfo, TableInfoFactory.ofEntityClass(Article.class));

        Assert.assertFalse("The plugin mapper-to-entity mapping should be removed",
            mapperMappings().containsKey(PluginEntity.class));
        Assert.assertTrue("Unrelated mapper-to-entity mapping should remain",
            mapperMappings().containsKey(Article.class));
        Assert.assertTrue("The unrelated dynamic statement should remain",
            FlexConfiguration.getDynamicMappedStatementCacheKeys().contains(
                "article.select:" + Article.class.getName()));
        Assert.assertTrue("A same-name entity from another ClassLoader must remain",
            FlexConfiguration.getDynamicMappedStatementCacheKeys().contains(
                "isolated.select:" + isolatedEntityClass.getName()));
        Assert.assertFalse("The plugin mapper proxy should be removed",
            hasCachedMapper(ENVIRONMENT_ID, PluginMapper.class));
        Assert.assertFalse(configuration.hasResultMap(PluginEntity.class.getName()));
        Assert.assertFalse(secondConfiguration.hasResultMap(PluginEntity.class.getName()));
        Assert.assertTrue(configuration.hasResultMap(Article.class.getName()));
        Assert.assertTrue("Plugin entity listener registration should be removed",
            environmentConfig.getEntityInsertListeners().get(PluginEntity.class) == null);
    }

    @Test
    public void doesNotEvictTheDefaultConfiguration() {
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
        FlexGlobalConfig.getGlobalConfigs().put(ENVIRONMENT_ID, defaultConfig);
        try {
            FlexCaches.evictMappers(ENVIRONMENT_ID,
                Collections.<Class<?>>singleton(PluginMapper.class));
            Assert.fail("The default configuration must be protected");
        } catch (IllegalStateException expected) {
            Assert.assertSame(defaultConfig, FlexGlobalConfig.getDefaultConfig());
            Assert.assertSame(defaultConfig, FlexGlobalConfig.getConfig(ENVIRONMENT_ID));
            Assert.assertSame(pluginTableInfo, TableInfoFactory.ofMapperClass(PluginMapper.class));
        }
    }

    private SqlSessionFactory mockSqlSessionFactory(FlexConfiguration configuration) {
        return (SqlSessionFactory) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class<?>[]{SqlSessionFactory.class},
            (proxy, method, args) -> "getConfiguration".equals(method.getName()) ? configuration : null);
    }

    private DataSource mockDataSource() {
        return (DataSource) Proxy.newProxyInstance(
            getClass().getClassLoader(), new Class<?>[]{DataSource.class}, (proxy, method, args) -> null);
    }

    private Class<?> duplicateClass(Class<?> source) throws Exception {
        String className = source.getName();
        String resourceName = className.replace('.', '/') + ".class";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (InputStream input = source.getClassLoader().getResourceAsStream(resourceName)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                bytes.write(buffer, 0, read);
            }
        }
        byte[] classBytes = bytes.toByteArray();
        ClassLoader isolatedLoader = new ClassLoader(source.getClassLoader()) {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (!className.equals(name)) {
                    return super.loadClass(name, resolve);
                }
                synchronized (getClassLoadingLock(name)) {
                    Class<?> loaded = findLoadedClass(name);
                    if (loaded == null) {
                        loaded = defineClass(name, classBytes, 0, classBytes.length);
                    }
                    if (resolve) {
                        resolveClass(loaded);
                    }
                    return loaded;
                }
            }
        };
        Class<?> duplicate = isolatedLoader.loadClass(className);
        Assert.assertEquals(source.getName(), duplicate.getName());
        Assert.assertNotSame(source, duplicate);
        return duplicate;
    }

    private void addDynamicStatement(String key, Class<?> entityClass) {
        ResultMap resultMap = new ResultMap.Builder(configuration, key + ".result", entityClass,
            Collections.emptyList()).build();
        MappedStatement statement = new MappedStatement.Builder(configuration, key,
            new StaticSqlSource(configuration, "select 1"), SqlCommandType.SELECT)
            .resultMaps(Collections.singletonList(resultMap))
            .build();
        try {
            Field field = FlexConfiguration.class.getDeclaredField("dynamicMappedStatementCache");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, MappedStatement> cache = (Map<String, MappedStatement>) field.get(null);
            cache.put(key, statement);
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed dynamic MappedStatement cache", e);
        }
    }

    private void addResultMap(FlexConfiguration configuration, Class<?> entityClass) {
        configuration.addResultMap(new ResultMap.Builder(configuration,
            entityClass.getName(), entityClass, Collections.emptyList()).build());
    }

    @SuppressWarnings("unchecked")
    private Map<Class<?>, Class<?>> mapperMappings() throws Exception {
        Field field = Mappers.class.getDeclaredField("ENTITY_MAPPER_MAP");
        field.setAccessible(true);
        return (Map<Class<?>, Class<?>>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private boolean hasCachedMapper(String environmentId, Class<?> mapperClass) throws Exception {
        Field field = Mappers.class.getDeclaredField("MAPPER_OBJECTS_OF_ENV");
        field.setAccessible(true);
        Map<String, Map<Class<?>, Object>> mapperObjects =
            (Map<String, Map<Class<?>, Object>>) field.get(null);
        Map<Class<?>, Object> environmentMappers = mapperObjects.get(environmentId);
        return environmentMappers != null && environmentMappers.containsKey(mapperClass);
    }

    public interface PluginMapper extends BaseMapper<PluginEntity> {
    }

    public interface ArticleMapper extends BaseMapper<Article> {
    }

    public static class PluginEntity {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
