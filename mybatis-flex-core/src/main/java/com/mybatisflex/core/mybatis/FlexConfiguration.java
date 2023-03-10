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
package com.mybatisflex.core.mybatis;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.keygen.MultiEntityKeyGenerator;
import com.mybatisflex.core.keygen.MultiRowKeyGenerator;
import com.mybatisflex.core.keygen.MybatisKeyGeneratorUtil;
import com.mybatisflex.core.keygen.RowKeyGenerator;
import com.mybatisflex.core.row.RowMapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfos;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Map;

public class FlexConfiguration extends Configuration {


    public FlexConfiguration(Environment environment) {
        super(environment);
        setMapUnderscoreToCamelCase(true);
        initDefaultMappers();
    }

    public FlexConfiguration() {
        initDefaultMappers();
    }

    /**
     * ?????? mybatis-flex ????????? Mapper
     * ???????????? RowMapper {@link RowMapper}
     */
    private void initDefaultMappers() {
        addMapper(RowMapper.class);
    }


    /**
     * ????????? sql ????????????
     */
    @Override
    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        String mappedStatementId = mappedStatement.getId();
        /**
         *  ??? "!selectKey" ????????? mappedStatementId???????????? Sequence ??????????????????????????????????????????
         *  {@link SelectKeyGenerator#SELECT_KEY_SUFFIX}
         */
        if (!mappedStatementId.endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)
                && parameterObject instanceof Map
                && ((Map<?, ?>) parameterObject).containsKey(FlexConsts.SQL_ARGS)) {
            return new SqlArgsParameterHandler(mappedStatement, (Map) parameterObject, boundSql);
        } else {
            return super.newParameterHandler(mappedStatement, parameterObject, boundSql);
        }
    }

    /**
     * ????????? FlexRoutingStatementHandler????????????????????????????????????????????????
     * FlexRoutingStatementHandler ??? ????????? RoutingStatementHandler ?????????????????????????????????
     */
    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = new FlexRoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }


    @Override
    public void addMappedStatement(MappedStatement ms) {
        //?????? RowMapper.insertRow ??????????????????
        //?????? RowMapper.insertBatchWithFirstRowColumns ??????????????????
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
                , "selectListByQuery")) {
            ms = replaceResultMap(ms);
        }

        super.addMappedStatement(ms);
    }


    /**
     * ?????? entity ????????? ResultMap
     */
    private MappedStatement replaceResultMap(MappedStatement ms) {

        TableInfo tableInfo = getTableInfo(ms);
        if (tableInfo == null) {
            return ms;
        }

        String resultMapId = tableInfo.getEntityClass().getName();

        ResultMap resultMap;
        if (hasResultMap(resultMapId)) {
            resultMap = getResultMap(resultMapId);
        } else {
            resultMap = tableInfo.buildResultMap(this);
            this.addResultMap(resultMap);
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
                .resultMaps(CollectionUtil.newArrayList(resultMap)) // ??????resultMap
                .resultSetType(ms.getResultSetType())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .cache(ms.getCache())
                .build();
    }

    /**
     * ?????????????????????????????????????????? MappedStatement
     *
     * @param ms MappedStatement
     * @return replaced MappedStatement
     */
    private MappedStatement replaceRowKeyGenerator(MappedStatement ms) {

        //???????????? SQL???????????????????????????????????????
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
                .keyGenerator(keyGenerator) // ?????????????????????
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
     * ?????????????????????????????????????????? MappedStatement
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

        //????????????
        if (ms.getId().endsWith(FlexConsts.METHOD_INSERT_BATCH)) {
            keyGenerator = new MultiEntityKeyGenerator(keyGenerator);
        }

        return new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(keyGenerator) // ?????????????????????
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
            return TableInfos.ofMapperClass(mapperClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


}
