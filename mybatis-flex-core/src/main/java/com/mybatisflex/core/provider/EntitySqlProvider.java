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
package com.mybatisflex.core.provider;

import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.querywrapper.QueryColumn;
import com.mybatisflex.core.querywrapper.QueryCondition;
import com.mybatisflex.core.querywrapper.QueryWrapper;
import com.mybatisflex.core.querywrapper.CPI;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EntitySqlProvider {


    /**
     * 不让实例化，使用静态方法的模式，效率更高，非静态方法每次都会实例化当前类
     * 参考源码: {{@link org.apache.ibatis.builder.annotation.ProviderSqlSource#getBoundSql(Object)}
     */
    private EntitySqlProvider() {
    }

    /**
     * insert 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#insert(Object)
     */
    public static String insert(Map params, ProviderContext context) {
        Object entity = ProviderUtil.getEntity(params);
        if (entity == null) {
            throw FlexExceptions.wrap("entity can not be null.");
        }

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);

        Object[] values = tableInfo.obtainInsertValues(entity);
        ProviderUtil.setSqlArgs(params, values);

        return DialectFactory.getDialect().forInsertEntity(tableInfo, entity);
    }


    /**
     * insertBatchWithFirstEntityColumns 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#insertBatch(List)
     * @see com.mybatisflex.core.FlexConsts#METHOD_INSERT_BATCH
     */
    public static String insertBatch(Map params, ProviderContext context) {
        List<Object> entities = ProviderUtil.getEntities(params);
        if (CollectionUtil.isEmpty(entities)) {
            throw FlexExceptions.wrap("entities can not be null or empty.");
        }

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);
        Object[] values = new Object[0];
        for (Object entity : entities) {
            values = ArrayUtil.concat(values, tableInfo.obtainInsertValues(entity));
        }

        ProviderUtil.setSqlArgs(params, values);

        return DialectFactory.getDialect().forInsertEntityBatch(tableInfo, entities);
    }


    /**
     * deleteById 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#deleteById(Serializable)
     */
    public static String deleteById(Map params, ProviderContext context) {
        Object[] primaryValues = ProviderUtil.getPrimaryValues(params);
        if (ArrayUtil.isEmpty(primaryValues)) {
            throw FlexExceptions.wrap("primaryValues can not be null or empty.");
        }

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);
        ProviderUtil.setSqlArgs(params, primaryValues);

        return DialectFactory.getDialect().forDeleteEntityById(tableInfo);
    }


    /**
     * deleteBatchByIds 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#deleteBatchByIds(Collection)
     */
    public static String deleteBatchByIds(Map params, ProviderContext context) {
        Object[] primaryValues = ProviderUtil.getPrimaryValues(params);
        if (ArrayUtil.isEmpty(primaryValues)) {
            throw FlexExceptions.wrap("primaryValues can not be null or empty.");
        }

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);
        ProviderUtil.setSqlArgs(params, primaryValues);

        return DialectFactory.getDialect().forDeleteEntityBatchByIds(tableInfo, primaryValues);
    }


    /**
     * deleteByQuery 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#deleteByQuery(QueryWrapper)
     */
    public static String deleteByQuery(Map params, ProviderContext context) {
        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);
        if (queryWrapper == null) {
            throw FlexExceptions.wrap("queryWrapper can not be null or empty.");
        }

        ProviderUtil.setSqlArgs(params, CPI.getValueArray(queryWrapper));

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);
        queryWrapper.from(tableInfo.getTableName());

        return DialectFactory.getDialect().forDeleteEntityBatchByQuery(tableInfo, queryWrapper);
    }


    /**
     * update 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#update(Object, boolean)
     */
    public static String update(Map params, ProviderContext context) {
        Object entity = ProviderUtil.getEntity(params);
        if (entity == null) {
            throw FlexExceptions.wrap("entity can not be null");
        }

        boolean ignoreNulls = ProviderUtil.isIgnoreNulls(params);

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);
        Object[] updateValues = tableInfo.obtainUpdateValues(entity, ignoreNulls, false);
        Object[] primaryValues = tableInfo.obtainPrimaryValues(entity);
        ProviderUtil.setSqlArgs(params, ArrayUtil.concat(updateValues, primaryValues));

        return DialectFactory.getDialect().forUpdateEntity(tableInfo, entity, ignoreNulls);
    }


    /**
     * updateByQuery 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#updateByQuery(Object, QueryWrapper)
     */
    public static String updateByQuery(Map params, ProviderContext context) {
        Object entity = ProviderUtil.getEntity(params);
        if (entity == null) {
            throw FlexExceptions.wrap("entity can not be null");
        }
        boolean ignoreNulls = ProviderUtil.isIgnoreNulls(params);
        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);

        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
        if (StringUtil.isNotBlank(logicDeleteColumn)) {
            queryWrapper.and(QueryCondition.create(new QueryColumn(tableInfo.getTableName(), logicDeleteColumn), 0));
        }

        Object[] values = tableInfo.obtainUpdateValues(entity, ignoreNulls, true);

        ProviderUtil.setSqlArgs(params, ArrayUtil.concat(values, CPI.getValueArray(queryWrapper)));

        return DialectFactory.getDialect().forUpdateEntityByQuery(tableInfo, entity, ignoreNulls, queryWrapper);
    }


    /**
     * selectOneById 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#selectOneById(Serializable)
     */
    public static String selectOneById(Map params, ProviderContext context) {
        Object[] primaryValues = ProviderUtil.getPrimaryValues(params);
        if (ArrayUtil.isEmpty(primaryValues)) {
            throw FlexExceptions.wrap("primaryValues can not be null or empty.");
        }

        ProviderUtil.setSqlArgs(params, primaryValues);

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);
        return DialectFactory.getDialect().forSelectOneEntityById(tableInfo);
    }


    /**
     * selectListByIds 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#selectListByIds(Collection)
     */
    public static String selectListByIds(Map params, ProviderContext context) {
        Object[] primaryValues = ProviderUtil.getPrimaryValues(params);
        if (ArrayUtil.isEmpty(primaryValues)) {
            throw FlexExceptions.wrap("primaryValues can not be null or empty.");
        }

        ProviderUtil.setSqlArgs(params, primaryValues);

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);
        return DialectFactory.getDialect().forSelectEntityListByIds(tableInfo, primaryValues);
    }


    /**
     * selectListByQuery 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#selectListByQuery(QueryWrapper)
     */
    public static String selectListByQuery(Map params, ProviderContext context) {
        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);
        if (queryWrapper == null) {
            throw FlexExceptions.wrap("queryWrapper can not be null.");
        }

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);

        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
        if (StringUtil.isNotBlank(logicDeleteColumn)) {
            queryWrapper.and(QueryCondition.create(new QueryColumn(tableInfo.getTableName(), logicDeleteColumn), 0));
        }

        Object[] values = CPI.getValueArray(queryWrapper);
        ProviderUtil.setSqlArgs(params, values);

        queryWrapper.from(tableInfo.getTableName());

        return DialectFactory.getDialect().forSelectListByQuery(queryWrapper);
    }

    /**
     * selectCountByQuery 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see com.mybatisflex.core.BaseMapper#selectCountByQuery(QueryWrapper)
     */
    public static String selectCountByQuery(Map params, ProviderContext context) {
        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);
        if (queryWrapper == null) {
            throw FlexExceptions.wrap("queryWrapper can not be null.");
        }

        TableInfo tableInfo = ProviderUtil.getTableInfo(context);

        //逻辑删除
        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
        if (StringUtil.isNotBlank(logicDeleteColumn)) {
            queryWrapper.and(QueryCondition.create(new QueryColumn(tableInfo.getTableName(), logicDeleteColumn), 0));
        }

        Object[] values = CPI.getValueArray(queryWrapper);
        ProviderUtil.setSqlArgs(params, values);

        queryWrapper.from(tableInfo.getTableName());
        return DialectFactory.getDialect().forSelectCountByQuery(queryWrapper);
    }


}
