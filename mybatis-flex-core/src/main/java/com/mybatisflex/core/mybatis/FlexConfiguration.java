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
        String mapperClassName = ms.getId().substring(0, ms.getId().lastIndexOf("."));
        try {
            Class<?> mapperClass = Class.forName(mapperClassName);
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
        }
    }


    @Override
    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
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
