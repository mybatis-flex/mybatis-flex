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
package com.mybatisflex.core;

import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.mybatis.Mappers;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.LambdaUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * mybatis-flex 内部缓存的统一操作入口（Facade）。
 *
 * <p>flex 在运行期维护了两组解析缓存：
 * <ul>
 *     <li>{@link TableInfoFactory} —— 实体类 / 表名 / Mapper 与 {@link com.mybatisflex.core.table.TableInfo} 的映射；</li>
 *     <li>{@link LambdaUtil} —— Lambda getter 的字段名 / 归属类 / QueryColumn 解析结果。</li>
 * </ul>
 *
 * <p>正常运行时缓存应保持稳定，不应清理。以下场景例外：
 * <ul>
 *     <li>开发期与 JRebel / Spring Boot DevTools 等热加载工具配合，实体类被重新加载后，
 *         需要强制 flex 用最新的 Class 重新解析；</li>
 *     <li>单元测试之间希望隔离，避免上一个用例遗留的 TableInfo 影响下一个用例。</li>
 * </ul>
 *
 * <p>本类只处理 flex 自身的缓存，<b>不会</b>触碰 MyBatis {@code Configuration} 内的 MappedStatement，
 * 也不会重建业务侧的 Spring bean。完整的热加载还需要业务侧在合适时机自行重建这些资源。
 *
 * @author mybatis-flex
 * @since 1.11.9
 */
public class FlexCaches {

    private FlexCaches() {
    }

    /**
     * 清除指定实体类在 flex 内部的所有缓存：
     * 委托给 {@link TableInfoFactory#evict(Class)} 与 {@link LambdaUtil#clear()}。
     *
     * <p>注意：{@link LambdaUtil} 的键是 Lambda 合成类（例如 {@code Foo$$Lambda$123}），
     * 与实体类不是一一对应，因此这里采用"整表清空"策略而非按实体粒度清理。
     * 这是刻意的取舍 —— 精细化清理需要遍历每个 QueryColumn 反查其归属实体，得不偿失。
     *
     * <p><b>不清理 MyBatis 层缓存</b>。若在热加载场景下改了实体字段类型，老的
     * {@code Configuration.resultMaps} 和 {@code FlexConfiguration.dynamicMappedStatementCache}
     * 会导致查询仍复用旧的 ResultMap（旧 javaType），此时应使用
     * {@link #evictEntity(SqlSessionFactory, Class)} 重载一并清理。
     *
     * @param entityClass 实体类，允许为 {@code null}（此时只清 Lambda 缓存）
     * @return 一份简短的清理报告，形如 {@code "TableInfo=3, Lambda=17"}
     */
    public static String evictEntity(@Nullable Class<?> entityClass) {
        int tableInfoRemoved = TableInfoFactory.evict(entityClass);
        int lambdaRemoved = LambdaUtil.clear();
        return "TableInfo=" + tableInfoRemoved + ", Lambda=" + lambdaRemoved;
    }

    /**
     * 清除指定实体类的缓存，<b>同时清理 MyBatis 层的 ResultMap / 动态 MappedStatement 缓存</b>。
     *
     * <p>相比 {@link #evictEntity(Class)}，本重载额外清理：
     * <ul>
     *     <li>{@link FlexConfiguration#dynamicMappedStatementCache} 中 key 以
     *         {@code ":<entityFQN>"} 结尾的条目（泛型/共享 mapper 场景）；</li>
     *     <li>{@code Configuration.resultMaps} 中 key 等于实体全限定名、或
     *         以 {@code "<entityFQN>-"} 开头的条目（具体 mapper 场景）。</li>
     * </ul>
     *
     * <p>适用于 JRebel / DevTools 等热加载场景：改 {@code @Table} 实体字段类型后，
     * 若只清 flex 自身缓存，{@link FlexConfiguration#replaceResultMap} 会复用老的
     * ResultMap（老 javaType），导致类型转换异常（如 Oracle 17059）。
     *
     * <p>若传入的 {@code sqlSessionFactory} 为 {@code null}，或其 Configuration
     * 不是 {@link FlexConfiguration}，则退化为只清 flex 自身缓存，与
     * {@link #evictEntity(Class)} 行为一致。
     *
     * @param sqlSessionFactory 当前应用的 SqlSessionFactory，可为 {@code null}
     * @param entityClass       实体类，允许为 {@code null}（此时只清 Lambda 缓存）
     * @return 一份简短的清理报告，形如
     *         {@code "TableInfo=3, Lambda=17, MappedStatement=2, ResultMap=1"}；
     *         当未提供可用的 FlexConfiguration 时，后两项不出现
     * @since 1.11.9
     */
    public static String evictEntity(@Nullable SqlSessionFactory sqlSessionFactory,
                                     @Nullable Class<?> entityClass) {
        String flexReport = evictEntity(entityClass);
        if (sqlSessionFactory == null) {
            return flexReport;
        }
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        if (!(configuration instanceof FlexConfiguration)) {
            return flexReport;
        }
        int msBefore = 0;
        int rmBefore = 0;
        // 仅在 entityClass 非空时统计 MyBatis 层清理数（与 FlexConfiguration.evictEntity 行为一致）
        if (entityClass != null) {
            String fqn = entityClass.getName();
            String suffix = ":" + fqn;
            // dynamicMappedStatementCache 是 static 字段，直接通过类访问
            // 这里通过 FlexConfiguration 实例方法统一清理，并由该方法维护计数语义
            msBefore = countMatchingMsCache(fqn);
            rmBefore = countMatchingResultMaps(configuration, fqn);
            ((FlexConfiguration) configuration).evictEntity(entityClass);
            int msRemoved = msBefore - countMatchingMsCache(fqn);
            int rmRemoved = rmBefore - countMatchingResultMaps(configuration, fqn);
            return flexReport + ", MappedStatement=" + msRemoved + ", ResultMap=" + rmRemoved;
        }
        return flexReport;
    }

    /**
     * 清空 flex 内部的全部缓存：
     * 委托给 {@link TableInfoFactory#clear()} 与 {@link LambdaUtil#clear()}。
     *
     * <p><b>不清理 MyBatis 层缓存</b>。若在热加载场景下需要一并清理 ResultMap /
     * 动态 MappedStatement 缓存，使用 {@link #clearAll(SqlSessionFactory)} 重载。
     *
     * @return 一份简短的清理报告，形如 {@code "TableInfo=8, Lambda=17"}
     */
    public static String clearAll() {
        int tableInfoRemoved = TableInfoFactory.clear();
        int lambdaRemoved = LambdaUtil.clear();
        return "TableInfo=" + tableInfoRemoved + ", Lambda=" + lambdaRemoved;
    }

    /**
     * 清空 flex 内部的全部缓存，<b>同时清空 MyBatis 层的动态 MappedStatement 缓存</b>。
     *
     * <p>相比 {@link #clearAll()}，本重载额外清理：
     * <ul>
     *     <li>{@link FlexConfiguration#dynamicMappedStatementCache}（全部清空）；</li>
     * </ul>
     *
     * <p>注意：{@code Configuration.resultMaps} 不在此方法中全部清空——它同时承载
     * 业务侧 XML/注解声明的 ResultMap，误删会影响正常查询。如需按实体粒度清理，
     * 请使用 {@link #evictEntity(SqlSessionFactory, Class)}。
     *
     * @param sqlSessionFactory 当前应用的 SqlSessionFactory，可为 {@code null}
     * @return 一份简短的清理报告，形如
     *         {@code "TableInfo=8, Lambda=17, MappedStatement=42"}；
     *         当未提供可用的 FlexConfiguration 时，最后一项不出现
     * @since 1.11.9
     */
    public static String clearAll(@Nullable SqlSessionFactory sqlSessionFactory) {
        String flexReport = clearAll();
        if (sqlSessionFactory == null) {
            return flexReport;
        }
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        if (!(configuration instanceof FlexConfiguration)) {
            return flexReport;
        }
        // 清 dynamicMappedStatementCache（static 字段，全清）
        int msBefore = FlexConfiguration.getDynamicMappedStatementCacheSize();
        ((FlexConfiguration) configuration).clearDynamicMappedStatementCache();
        int msRemoved = msBefore - FlexConfiguration.getDynamicMappedStatementCacheSize();
        return flexReport + ", MappedStatement=" + msRemoved;
    }

    /**
     * 清理插件环境及其 mapper 类关联的 MyBatis-Flex 静态缓存。
     *
     * <p>在调用前，必须先停止插件并关闭所有使用这些 mapper 类的 SqlSessionFactory、
     * session 和后台任务。传入的 mapper 类应只属于已停止的插件；如果仍有活动环境
     * 使用相同的 Class 对象，其缓存也会被清除。此方法不会关闭 SqlSessionFactory，
     * 也不会替调用方停止线程或清理 Spring/PF4J 对象。
     *
     * <p>默认配置不会被移除。若默认配置或待清理环境引用了这些 mapper 类，方法会在执行
     * 任何清理前抛出 {@link IllegalStateException}。除指定环境外，其他已注册且包含这些
     * mapper 类的环境也会一并移除，避免插件 ClassLoader 仍被另一个全局配置持有。
     * Lambda 解析缓存无法按实体类精确归属，因此会整体清空；其他环境会在后续访问时重建。
     *
     * @param environmentId 要移除的环境 ID
     * @param mapperClasses 插件此前注册的 mapper 类集合
     * @return 清理报告
     * @since 1.11.9
     */
    public static String evictMappers(String environmentId, Set<Class<?>> mapperClasses) {
        if (environmentId == null || environmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("environmentId must not be blank");
        }
        if (mapperClasses == null || mapperClasses.isEmpty()) {
            throw new IllegalArgumentException("mapperClasses must not be empty");
        }
        Set<Class<?>> mappers = new HashSet<>();
        for (Class<?> mapperClass : mapperClasses) {
            if (mapperClass != null) {
                mappers.add(mapperClass);
            }
        }
        if (mappers.isEmpty()) {
            throw new IllegalArgumentException("mapperClasses must contain at least one mapper class");
        }

        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
        if (containsAnyMapper(defaultConfig.getConfiguration(), mappers)) {
            throw new IllegalStateException("Cannot evict mapper classes used by the default configuration");
        }

        Set<String> environmentIds = new HashSet<>();
        environmentIds.add(environmentId);
        for (java.util.Map.Entry<String, FlexGlobalConfig> entry : FlexGlobalConfig.getGlobalConfigs().entrySet()) {
            FlexGlobalConfig config = entry.getValue();
            if (containsAnyMapper(config.getConfiguration(), mappers)) {
                if (config == defaultConfig
                    || sameNonNull(config.getConfiguration(), defaultConfig.getConfiguration())
                    || sameNonNull(config.getSqlSessionFactory(), defaultConfig.getSqlSessionFactory())) {
                    throw new IllegalStateException("Cannot evict the default MyBatis-Flex configuration");
                }
                environmentIds.add(entry.getKey());
            }
        }

        for (String id : environmentIds) {
            FlexGlobalConfig config = FlexGlobalConfig.getConfig(id);
            if (config != null && (config == defaultConfig
                || sameNonNull(config.getConfiguration(), defaultConfig.getConfiguration())
                || sameNonNull(config.getSqlSessionFactory(), defaultConfig.getSqlSessionFactory()))) {
                throw new IllegalStateException("Cannot evict the default MyBatis-Flex configuration");
            }
        }

        Set<Class<?>> entityClasses = TableInfoFactory.evictMappers(mappers);
        entityClasses.addAll(Mappers.evictMappers(mappers));
        for (Class<?> entityClass : entityClasses) {
            TableInfoFactory.evict(entityClass);
        }
        int tableListenerRemoved = TableInfo.evictListeners(entityClasses);
        int globalListenerRemoved = FlexGlobalConfig.evictEntityListeners(entityClasses);
        int mappedStatementRemoved = FlexConfiguration.evictDynamicMappedStatements(entityClasses);
        int resultMapRemoved = 0;
        for (String id : environmentIds) {
            FlexGlobalConfig config = FlexGlobalConfig.getConfig(id);
            if (config != null && config.getConfiguration() instanceof FlexConfiguration) {
                resultMapRemoved += ((FlexConfiguration) config.getConfiguration()).evictResultMaps(entityClasses);
            }
        }
        int lambdaRemoved = LambdaUtil.clear();
        int configRemoved = 0;
        for (String id : environmentIds) {
            if (FlexGlobalConfig.removeConfig(id) != null) {
                configRemoved++;
            }
        }
        return "Environment=" + configRemoved
            + ", Mapper=" + mappers.size()
            + ", Entity=" + entityClasses.size()
            + ", Listener=" + (tableListenerRemoved + globalListenerRemoved)
            + ", MappedStatement=" + mappedStatementRemoved
            + ", ResultMap=" + resultMapRemoved
            + ", Lambda=" + lambdaRemoved;
    }

    private static boolean containsAnyMapper(org.apache.ibatis.session.Configuration configuration,
                                             Set<Class<?>> mapperClasses) {
        if (configuration == null) {
            return false;
        }
        for (Class<?> mapperClass : mapperClasses) {
            if (configuration.hasMapper(mapperClass)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameNonNull(Object left, Object right) {
        return left != null && left == right;
    }

    // ---- 内部辅助：统计 dynamicMappedStatementCache 中匹配指定 FQN 的条目数 ----

    private static int countMatchingMsCache(String entityFqn) {
        String suffix = ":" + entityFqn;
        int count = 0;
        for (String key : FlexConfiguration.getDynamicMappedStatementCacheKeys()) {
            if (key.endsWith(suffix)) {
                count++;
            }
        }
        return count;
    }

    // ---- 内部辅助：统计 Configuration.resultMaps 中匹配指定 FQN 的条目数 ----

    private static int countMatchingResultMaps(org.apache.ibatis.session.Configuration configuration,
                                               String entityFqn) {
        int count = 0;
        for (String key : configuration.getResultMapNames()) {
            if (key.equals(entityFqn) || key.startsWith(entityFqn + "-")) {
                count++;
            }
        }
        return count;
    }

}
