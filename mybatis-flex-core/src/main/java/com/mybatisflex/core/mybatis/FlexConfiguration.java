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

import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.handler.CompositeEnumTypeHandler;
import com.mybatisflex.core.keygen.MultiEntityKeyGenerator;
import com.mybatisflex.core.keygen.MultiRowKeyGenerator;
import com.mybatisflex.core.keygen.MybatisKeyGeneratorUtil;
import com.mybatisflex.core.keygen.RowKeyGenerator;
import com.mybatisflex.core.mybatis.binding.FlexMapperRegistry;
import com.mybatisflex.core.mybatis.executor.FlexBatchExecutor;
import com.mybatisflex.core.mybatis.executor.FlexReuseExecutor;
import com.mybatisflex.core.mybatis.executor.FlexSimpleExecutor;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.MapUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael
 * @author life
 */
public class FlexConfiguration extends Configuration {

    private static final Map<String, MappedStatement> dynamicMappedStatementCache = new ConcurrentHashMap<>();
    private final MapperRegistry mapperRegistry = new FlexMapperRegistry(this);

    public FlexConfiguration() {
        setObjectWrapperFactory(new FlexWrapperFactory());
        setDefaultEnumTypeHandler(CompositeEnumTypeHandler.class);
    }


    public FlexConfiguration(Environment environment) {
        super(environment);
        setObjectWrapperFactory(new FlexWrapperFactory());
        setDefaultEnumTypeHandler(CompositeEnumTypeHandler.class);
    }

    /**
     * 为原生 sql 设置参数
     */
    @Override
    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        String mappedStatementId = mappedStatement.getId();
        /**
         *  以 "!selectKey" 结尾的 mappedStatementId，是用于 Sequence 生成主键的，无需为其设置参数
         *  {@link SelectKeyGenerator#SELECT_KEY_SUFFIX}
         */
        if (!mappedStatementId.endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)
            && parameterObject instanceof Map
            && ((Map<?, ?>) parameterObject).containsKey(FlexConsts.SQL_ARGS)) {
            SqlArgsParameterHandler sqlArgsParameterHandler = new SqlArgsParameterHandler(mappedStatement, parameterObject, boundSql);
            return (ParameterHandler) interceptorChain.pluginAll(sqlArgsParameterHandler);
        } else {
            return super.newParameterHandler(mappedStatement, parameterObject, boundSql);
        }
    }


    @Override
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement
        , RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new FlexResultSetHandler(executor, mappedStatement, parameterHandler,
            resultHandler, boundSql, rowBounds);
        return (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
    }

    /**
     * 替换为 FlexStatementHandler，主要用来为实体类的多主键做支持、和数据审计
     * FlexStatementHandler 和 原生的 RoutingStatementHandler 对比，没有任何性能影响
     */
    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = new FlexStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }


    /**
     * 替换为 Flex 的 Executor，主要用于重建 CacheKey
     * 默认情况下，Mybatis 的 CacheKey 构建是必须有 ParameterMapping，而 Flex 的 select 是不带有 ParameterMapping 的
     */
    @Override
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        executorType = executorType == null ? defaultExecutorType : executorType;
        Executor executor;
        if (ExecutorType.BATCH == executorType) {
            executor = new FlexBatchExecutor(this, transaction);
        } else if (ExecutorType.REUSE == executorType) {
            executor = new FlexReuseExecutor(this, transaction);
        } else {
            executor = new FlexSimpleExecutor(this, transaction);
        }
        if (cacheEnabled) {
            executor = new CachingExecutor(executor);
        }
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }


    @Override
    public MappedStatement getMappedStatement(String id) {
        MappedStatement ms = super.getMappedStatement(id);
        //动态 resultsMap，方法名称为：selectListByQuery
        Class<?> asType = MappedStatementTypes.getCurrentType();
        //忽略掉查询 Rows 的方法
        if (asType != null) {
            return MapUtil.computeIfAbsent(dynamicMappedStatementCache, id + ":" + asType.getName(),
                clazz -> replaceResultMap(ms, TableInfoFactory.ofEntityClass(asType))
            );
        }

        return ms;
    }


    @Override
    public void addMappedStatement(MappedStatement ms) {
        //替换 RowMapper.insert 的主键生成器
        //替换 RowMapper.insertBatchWithFirstRowColumns 的主键生成器
        if (ms.getId().startsWith("com.mybatisflex.core.row.RowMapper.insert")) {
            ms = replaceRowKeyGenerator(ms);
        }
        //entity insert methods
        else if (StringUtil.endsWithAny(ms.getId(), "insert", FlexConsts.METHOD_INSERT_BATCH)
            && ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            ms = replaceEntityKeyGenerator(ms);
        }
        //entity select
        else if (StringUtil.endsWithAny(ms.getId(), "selectOneById", "selectListByIds"
            , "selectListByQuery", "selectCursorByQuery")) {
            ms = replaceResultMap(ms, getTableInfo(ms));
        } else {
            List<ResultMap> resultMaps = ms.getResultMaps();
            //根据 resultMap 里面的 class 进行判断
            for (ResultMap resultMap : resultMaps) {
                //获取结果的类型
                Class<?> clazz = resultMap.getType();
                //判断是否为表实体类
                if (clazz.getDeclaredAnnotation(Table.class) != null && isDefaultResultMap(ms.getId(), resultMap.getId())) {
                    TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
                    ms = replaceResultMap(ms, tableInfo);
                }
            }
        }
        super.addMappedStatement(ms);
    }

    /**
     * 是否为默认的 resultMap，也就是未配置 resultMap
     *
     * @return
     */
    private boolean isDefaultResultMap(String statementId, String resultMapId) {
        // 参考 {@code  MapperBuilderAssistant.getStatementResultMaps}
        return resultMapId.equals(statementId + "-Inline");
    }


    /**
     * 替换 entity 查询的 ResultMap
     */
    private MappedStatement replaceResultMap(MappedStatement ms, TableInfo tableInfo) {

        if (tableInfo == null) {
            return ms;
        }

        String resultMapId = tableInfo.getEntityClass().getName();

        ResultMap resultMap;
        if (hasResultMap(resultMapId)) {
            resultMap = getResultMap(resultMapId);
        } else {
            resultMap = tableInfo.buildResultMap(this);
        }

        return new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
            .resource(ms.getResource())
            .fetchSize(ms.getFetchSize())
            .timeout(ms.getTimeout())
            .statementType(ms.getStatementType())
            .keyGenerator(NoKeyGenerator.INSTANCE)
            .keyProperty(ms.getKeyProperties() == null ? null : String.join(",", ms.getKeyProperties()))
            .keyColumn(ms.getKeyColumns() == null ? null : String.join(",", ms.getKeyColumns()))
            .databaseId(databaseId)
            .lang(ms.getLang())
            .resultOrdered(ms.isResultOrdered())
            .resultSets(ms.getResultSets() == null ? null : String.join(",", ms.getResultSets()))
            .resultMaps(Collections.singletonList(resultMap))
            .resultSetType(ms.getResultSetType())
            .flushCacheRequired(ms.isFlushCacheRequired())
            .useCache(ms.isUseCache())
            .cache(ms.getCache())
            .build();
    }

    /**
     * 清理指定实体在 MyBatis 层的两处 ResultMap 缓存,用于热加载场景。
     *
     * <p>改 {@code @Table} 实体字段类型后,仅清 {@link TableInfoFactory} 的 TableInfo 缓存不够:
     * {@link #replaceResultMap} 在 {@code hasResultMap(entityFQN)} 为 true 时会复用老的
     * {@link ResultMap}(老的 javaType),导致类型转换异常(如 Oracle 17059)。本方法把两处
     * 缓存一并清掉,下次查询用最新 TableInfo 重建 ResultMap。
     *
     * <p>清理范围:
     * <ul>
     *   <li>{@link #dynamicMappedStatementCache} -- 移除 key 后缀为 {@code ":<entityFQN>"} 的条目
     *       (泛型/共享 mapper 场景写入);</li>
     *   <li>{@link Configuration#resultMaps} -- 移除 key 等于 entityFQN 或以 {@code "<entityFQN>-"} 开头的条目
     *       (具体 mapper 场景写入)。{@code resultMaps} 实际类型是 MyBatis 的 {@code StrictMap},
     *       其 {@code removeIf} 继承自 {@code HashMap} 无 strict 检查,可安全移除。</li>
     * </ul>
     *
     * <p><b>不自动生效</b>:本方法只是公开 API,flex 内部不订阅 reload 事件,需调用方在实体重加载
     * 时机显式调用(JRebel listener / DevTools / 手动)。
     *
     * @param entityClass 实体类;{@code null} 时直接返回
     * @since 1.11.9
     */
    public void evictEntity(Class<?> entityClass) {
        if (entityClass == null) {
            return;
        }
        String fqn = entityClass.getName();
        dynamicMappedStatementCache.entrySet().removeIf(e -> e.getKey().endsWith(":" + fqn));
        resultMaps.entrySet().removeIf(
            e -> e.getKey().equals(fqn) || e.getKey().startsWith(fqn + "-"));
    }

    /**
     * 从全局动态 MappedStatement 缓存中移除引用指定实体类的条目。
     *
     * <p>与 {@link #evictEntity(Class)} 按类名后缀匹配不同，此方法比较 ResultMap
     * 中的实际 {@link Class} 对象，避免清理一个插件的类时误删另一个 ClassLoader
     * 中同名实体的条目。
     *
     * @param entityClasses 要清理的实体类
     * @return 实际移除的动态 MappedStatement 数量
     */
    public static int evictDynamicMappedStatements(Set<Class<?>> entityClasses) {
        if (entityClasses == null || entityClasses.isEmpty()) {
            return 0;
        }
        int[] removed = {0};
        dynamicMappedStatementCache.entrySet().removeIf(entry -> {
            boolean matches = entry.getValue().getResultMaps().stream()
                .anyMatch(resultMap -> entityClasses.contains(resultMap.getType()));
            if (matches) {
                removed[0]++;
            }
            return matches;
        });
        return removed[0];
    }

    /**
     * 从此 Configuration 的 ResultMap 集合中移除类型为指定实体类的映射。
     *
     * @param entityClasses 要清理的实体类
     * @return 实际移除的 ResultMap 数量
     */
    public int evictResultMaps(Set<Class<?>> entityClasses) {
        if (entityClasses == null || entityClasses.isEmpty()) {
            return 0;
        }
        int[] removed = {0};
        resultMaps.entrySet().removeIf(entry -> {
            boolean matches = entityClasses.contains(entry.getValue().getType());
            if (matches) {
                removed[0]++;
            }
            return matches;
        });
        return removed[0];
    }

    /**
     * 返回 {@link #dynamicMappedStatementCache} 中所有 key 的快照（副本）。
     *
     * <p>用于排查/统计，非高频路径使用。返回的是调用时的快照，后续缓存变更不影响返回值。
     *
     * @return key 集合的快照
     * @since 1.11.9
     */
    public static Set<String> getDynamicMappedStatementCacheKeys() {
        return new java.util.HashSet<>(dynamicMappedStatementCache.keySet());
    }

    /**
     * 返回 {@link #dynamicMappedStatementCache} 当前条目数。
     *
     * @return 动态 MappedStatement 缓存条目数
     * @since 1.11.9
     */
    public static int getDynamicMappedStatementCacheSize() {
        return dynamicMappedStatementCache.size();
    }

    /**
     * 清空 {@link #dynamicMappedStatementCache}（static 全局缓存）。
     *
     * <p>注意：这是全局操作，会影响所有使用同一 ClassLoader 的 SqlSessionFactory。
     * 一般仅用于"全量热加载/测试隔离"场景；按实体粒度清理请用
     * {@link #evictEntity(Class)}。
     *
     * @since 1.11.9
     */
    public static void clearDynamicMappedStatementCache() {
        dynamicMappedStatementCache.clear();
    }

    /**
     * 生成新的、已替换主键生成器的 MappedStatement
     *
     * @param ms MappedStatement
     * @return replaced MappedStatement
     */
    private MappedStatement replaceRowKeyGenerator(MappedStatement ms) {

        //执行原生 SQL，不需要为其设置主键生成器
        if (ms.getId().endsWith("BySql")) {
            return ms;
        }

        KeyGenerator keyGenerator = new RowKeyGenerator(ms);
        if (ms.getId().endsWith("insertBatchWithFirstRowColumns")) {
            keyGenerator = new MultiRowKeyGenerator(keyGenerator);
        }

        return new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
            .resource(ms.getResource())
            .fetchSize(ms.getFetchSize())
            .timeout(ms.getTimeout())
            .statementType(ms.getStatementType())
            // 替换主键生成器
            .keyGenerator(keyGenerator)
            .keyProperty(ms.getKeyProperties() == null ? null : String.join(",", ms.getKeyProperties()))
            .keyColumn(ms.getKeyColumns() == null ? null : String.join(",", ms.getKeyColumns()))
            .databaseId(databaseId)
            .lang(ms.getLang())
            .resultOrdered(ms.isResultOrdered())
            .resultSets(ms.getResultSets() == null ? null : String.join(",", ms.getResultSets()))
            .resultMaps(ms.getResultMaps())
            .resultSetType(ms.getResultSetType())
            .flushCacheRequired(ms.isFlushCacheRequired())
            .useCache(ms.isUseCache())
            .cache(ms.getCache())
            .build();
    }

    /**
     * 生成新的、已替换主键生成器的 MappedStatement
     *
     * @param ms MappedStatement
     * @return replaced MappedStatement
     */
    private MappedStatement replaceEntityKeyGenerator(MappedStatement ms) {

        TableInfo tableInfo = getTableInfo(ms);
        if (tableInfo == null) {
            return ms;
        }

        KeyGenerator keyGenerator = MybatisKeyGeneratorUtil.createTableKeyGenerator(tableInfo, ms);
        if (keyGenerator == NoKeyGenerator.INSTANCE) {
            return ms;
        }

        //批量插入
        if (ms.getId().endsWith(FlexConsts.METHOD_INSERT_BATCH)) {
            keyGenerator = new MultiEntityKeyGenerator(keyGenerator);
        }

        return new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
            .resource(ms.getResource())
            .fetchSize(ms.getFetchSize())
            .timeout(ms.getTimeout())
            .statementType(ms.getStatementType())
            // 替换主键生成器
            .keyGenerator(keyGenerator)
            .keyProperty(tableInfo.getKeyProperties())
            .keyColumn(tableInfo.getKeyColumns())
            .databaseId(databaseId)
            .lang(ms.getLang())
            .resultOrdered(ms.isResultOrdered())
            .resultSets(ms.getResultSets() == null ? null : String.join(",", ms.getResultSets()))
            .resultMaps(ms.getResultMaps())
            .resultSetType(ms.getResultSetType())
            .flushCacheRequired(ms.isFlushCacheRequired())
            .useCache(ms.isUseCache())
            .cache(ms.getCache())
            .build();
    }


    private TableInfo getTableInfo(MappedStatement ms) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String mapperClassName = ms.getId().substring(0, ms.getId().lastIndexOf("."));
        try {
            Class<?> mapperClass = Class.forName(mapperClassName, true, contextClassLoader);
            return TableInfoFactory.ofMapperClass(mapperClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


    @Override
    public <T> void addMapper(Class<T> type) {
        Type[] genericInterfaces = type.getGenericInterfaces();
        boolean isGenericInterface = false;
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                if (actualTypeArgument instanceof Class) {
                    Mappers.addMapping((Class<?>) actualTypeArgument, type);
                } else {
                    isGenericInterface = true;
                    break;
                }
            }
        }

        //不支持泛型类添加
        if (!isGenericInterface) {
            mapperRegistry.addMapper(type);
            TableInfoFactory.init(type.getPackage().getName());
        }
    }


    @Override
    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mapperRegistry.addMappers(packageName, superType);
        TableInfoFactory.init(packageName);
    }

    @Override
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
        TableInfoFactory.init(packageName);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }


}
