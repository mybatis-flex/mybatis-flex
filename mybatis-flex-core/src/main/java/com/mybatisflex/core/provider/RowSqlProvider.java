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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowCPI;
import com.mybatisflex.core.row.RowMapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.CollectionUtil;

import java.util.*;

public class RowSqlProvider {


    public static final String METHOD_RAW_SQL = "providerRawSql";

    /**
     * 不让实例化，使用静态方法的模式，效率更高，非静态方法每次都会实例化当前类
     * 参考源码: {{@link org.apache.ibatis.builder.annotation.ProviderSqlSource#getBoundSql(Object)}
     */
    private RowSqlProvider() {
    }

    /**
     * 执行原生 sql 的方法
     *
     * @param params
     * @return sql
     * @see RowMapper#insertBySql(String, Object...)
     * @see RowMapper#deleteBySql(String, Object...)
     * @see RowMapper#updateBySql(String, Object...)
     */
    public static String providerRawSql(Map params) {
        return ProviderUtil.getSqlString(params);
    }

    /**
     * insert 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#insert(String,String, Row)
     */
    public static String insert(Map params) {
        String tableName = ProviderUtil.getTableName(params);
        String schema = ProviderUtil.getSchemaName(params);
        Row row = ProviderUtil.getRow(params);
        ProviderUtil.setSqlArgs(params, RowCPI.obtainModifyValues(row));
        return DialectFactory.getDialect().forInsertRow(schema,tableName, row);
    }

    /**
     * insertBatch 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#insertBatchWithFirstRowColumns(String, String, List)
     */
    public static String insertBatchWithFirstRowColumns(Map params) {
        String tableName = ProviderUtil.getTableName(params);
        String schema = ProviderUtil.getSchemaName(params);
        List<Row> rows = ProviderUtil.getRows(params);
        if (rows == null || rows.isEmpty()) {
            throw FlexExceptions.wrap("rows can not be null or empty.");
        }

        // 让所有 row 的列顺序和值的数量与第条数据保持一致
        // 这个必须 new 一个 LinkedHashSet，因为 keepModifyAttrs 会清除 row 所有的 modifyAttrs
        Set<String> modifyAttrs = new LinkedHashSet<>(rows.get(0).obtainModifyAttrs());
        rows.forEach(row -> row.prepareAttrs(modifyAttrs));


        Object[] values = new Object[]{};
        for (Row row : rows) {
            values = ArrayUtil.concat(values, RowCPI.obtainModifyValues(row));
        }
        ProviderUtil.setSqlArgs(params, values);

        //sql: INSERT INTO `tb_table`(`name`, `sex`) VALUES (?, ?),(?, ?),(?, ?)
        return DialectFactory.getDialect().forInsertBatchWithFirstRowColumns(schema,tableName, rows);
    }

    /**
     * deleteById 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#deleteById(String,String, String, Object)
     */
    public static String deleteById(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        String[] primaryKeys = ProviderUtil.getPrimaryKeys(params);
        Object[] primaryValues = ProviderUtil.getPrimaryValues(params);

        if (primaryValues.length == 0) {
            throw FlexExceptions.wrap("primaryValue can not be null");
        } else {
            ProviderUtil.setSqlArgs(params, primaryValues);
        }

        return DialectFactory.getDialect().forDeleteById(schema, tableName, primaryKeys);
    }

    /**
     * deleteBatchByIds 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#deleteBatchByIds(String, String, String, Collection)
     */
    public static String deleteBatchByIds(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        String[] primaryKeys = ProviderUtil.getPrimaryKeys(params);
        Object[] primaryValues = ProviderUtil.getPrimaryValues(params);

        ProviderUtil.setSqlArgs(params, primaryValues);
        return DialectFactory.getDialect().forDeleteBatchByIds(schema, tableName, primaryKeys, primaryValues);
    }


    /**
     * deleteByQuery 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#deleteByQuery(String,String, QueryWrapper)
     */
    public static String deleteByQuery(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);
        CPI.setFromIfNecessary(queryWrapper, schema,tableName);

        Object[] valueArray = CPI.getValueArray(queryWrapper);
        ProviderUtil.setSqlArgs(params, valueArray);

        return DialectFactory.getDialect().forDeleteByQuery(queryWrapper);
    }

    /**
     * updateById 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#updateById(String, String, Row)
     */
    public static String updateById(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        Row row = ProviderUtil.getRow(params);
        ProviderUtil.setSqlArgs(params, RowCPI.obtainAllModifyValues(row));
        return DialectFactory.getDialect().forUpdateById(schema, tableName, row);
    }


    /**
     * updateByQuery 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#updateByQuery(String, String, Row, QueryWrapper)
     */
    public static String updateByQuery(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        Row data = ProviderUtil.getRow(params);

        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);
        CPI.setFromIfNecessary(queryWrapper,schema, tableName);

        Object[] modifyValues = RowCPI.obtainModifyValues(data);
        Object[] valueArray = CPI.getValueArray(queryWrapper);

        ProviderUtil.setSqlArgs(params, ArrayUtil.concat(modifyValues, valueArray));

        return DialectFactory.getDialect().forUpdateByQuery(queryWrapper, data);
    }


    /**
     * updateBatchById 的 sql 构建
     * mysql 等链接配置需要开启 allowMultiQueries=true
     *
     * @param params
     * @return sql
     * @see RowMapper#updateBatchById(String, String, List)
     */
    public static String updateBatchById(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        List<Row> rows = ProviderUtil.getRows(params);
        if (CollectionUtil.isEmpty(rows)) {
            throw FlexExceptions.wrap("rows can not be null or empty.");
        }

        Object[] values = FlexConsts.EMPTY_ARRAY;
        for (Row row : rows) {
            values = ArrayUtil.concat(values, RowCPI.obtainAllModifyValues(row));
        }
        ProviderUtil.setSqlArgs(params, values);
        return DialectFactory.getDialect().forUpdateBatchById(schema,tableName, rows);
    }

    /**
     * updateEntity 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#updateEntity(Object entities)
     */
    public static String updateEntity(Map params) {
        Object entity = ProviderUtil.getEntity(params);
        if (entity == null) {
            throw FlexExceptions.wrap("entity can not be null");
        }

        // 该 Mapper 是通用 Mapper  无法通过 ProviderContext 获取，直接使用 TableInfoFactory
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());

        // 执行 onUpdate 监听器
        tableInfo.invokeOnUpdateListener(entity);

        Object[] updateValues = tableInfo.buildUpdateSqlArgs(entity, false, false);
        Object[] primaryValues = tableInfo.buildPkSqlArgs(entity);
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();

        FlexExceptions.assertAreNotNull(primaryValues, "The value of primary key must not be null, entity[%s]", entity);

        ProviderUtil.setSqlArgs(params, ArrayUtil.concat(updateValues, primaryValues, tenantIdArgs));

        return DialectFactory.getDialect().forUpdateEntity(tableInfo, entity, false);
    }

    /**
     * 执行类似 update table set field=field+1 where ... 的场景
     *
     * @param params
     * @return sql
     * @see RowMapper#updateNumberAddByQuery(String, String, String, Number, QueryWrapper)
     */
    public static String updateNumberAddByQuery(Map params) {

        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        String fieldName = ProviderUtil.getFieldName(params);
        Number value = (Number) ProviderUtil.getValue(params);


        Object[] queryParams = CPI.getValueArray(queryWrapper);

        ProviderUtil.setSqlArgs(params, queryParams);

        return DialectFactory.getDialect().forUpdateNumberAddByQuery(schema, tableName, fieldName, value, queryWrapper);
    }


    /**
     * selectOneById 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#selectOneById(String, String, String, Object)
     */
    public static String selectOneById(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        String[] primaryKeys = ProviderUtil.getPrimaryKeys(params);
        Object[] primaryValues = ProviderUtil.getPrimaryValues(params);

        ProviderUtil.setSqlArgs(params, primaryValues);

        return DialectFactory.getDialect().forSelectOneById(schema,tableName, primaryKeys, primaryValues);
    }


    /**
     * selectListByQuery 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#selectListByQuery(String, String, QueryWrapper)
     */
    public static String selectListByQuery(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);
        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);
        CPI.setFromIfNecessary(queryWrapper,schema, tableName);

        Object[] valueArray = CPI.getValueArray(queryWrapper);
        ProviderUtil.setSqlArgs(params, valueArray);


        return DialectFactory.getDialect().forSelectByQuery(queryWrapper);
    }

    /**
     * selectCountByQuery 的 sql 构建
     *
     * @param params
     * @return sql
     * @see RowMapper#selectCountByQuery(String, String, QueryWrapper)
     */
    public static String selectObjectByQuery(Map params) {
        String schema = ProviderUtil.getSchemaName(params);
        String tableName = ProviderUtil.getTableName(params);

        QueryWrapper queryWrapper = ProviderUtil.getQueryWrapper(params);
        CPI.setFromIfNecessary(queryWrapper,schema,tableName);

        Object[] valueArray = CPI.getValueArray(queryWrapper);
        ProviderUtil.setSqlArgs(params, valueArray);

        return DialectFactory.getDialect().forSelectByQuery(queryWrapper);
    }


}
