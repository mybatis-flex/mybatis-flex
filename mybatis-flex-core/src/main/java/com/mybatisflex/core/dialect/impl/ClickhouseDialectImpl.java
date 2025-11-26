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
package com.mybatisflex.core.dialect.impl;

import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.exception.locale.LocalizedFormats;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowCPI;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.update.RawValue;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import static com.mybatisflex.core.constant.SqlConsts.*;

/**
 * @author: 老唐
 * @date: 2024-07-20 11:36
 * @version: 1.0
 */
public class ClickhouseDialectImpl extends CommonsDialectImpl {
    public static final String ALTER_TABLE = " ALTER TABLE ";
    public static final String CK_DELETE = " DELETE ";
    public static final String CK_UPDATE = " UPDATE ";

    public ClickhouseDialectImpl(KeywordWrap keywordWrap, LimitOffsetProcessor limitOffsetProcessor) {
        super(keywordWrap, limitOffsetProcessor);
    }

    /**
     * 根据主键更新
     *
     * @param schema
     * @param tableName
     * @param row
     * @return
     */
    @Override
    public String forUpdateById(String schema, String tableName, Row row) {
        //eg: ALTER TABLE test  UPDATE USERNAME = ? , AGE = ?  WHERE CUSERID = ?
        String table = getRealTable(tableName, OperateType.UPDATE);
        StringBuilder sql = new StringBuilder();
        Set<String> modifyAttrs = RowCPI.getModifyAttrs(row);
        Map<String, RawValue> rawValueMap = RowCPI.getRawValueMap(row);
        String[] primaryKeys = RowCPI.obtainsPrimaryKeyStrings(row);

        sql.append(ALTER_TABLE);
        if (StringUtil.hasText(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.UPDATE))).append(REFERENCE);
        }
        sql.append(wrap(table)).append(CK_UPDATE);
        int index = 0;
        for (Map.Entry<String, Object> e : row.entrySet()) {
            String colName = e.getKey();
            if (modifyAttrs.contains(colName) && !ArrayUtil.contains(primaryKeys, colName)) {
                if (index > 0) {
                    sql.append(DELIMITER);
                }
                sql.append(wrap(colName));

                if (rawValueMap.containsKey(colName)) {
                    sql.append(EQUALS).append(rawValueMap.get(colName).toSql(this));
                } else {
                    sql.append(EQUALS_PLACEHOLDER);
                }

                index++;
            }
        }
        sql.append(WHERE);
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(primaryKeys[i])).append(EQUALS_PLACEHOLDER);
        }
        prepareAuth(schema, table, sql, OperateType.UPDATE);
        return sql.toString();
    }

    /**
     * 根据主键删除
     *
     * @param schema
     * @param tableName
     * @param primaryKeys
     * @return
     */
    @Override
    public String forDeleteById(String schema, String tableName, String[] primaryKeys) {
        //eg: ALTER TABLE test  DELETE WHERE CUSERID = ?
        String table = getRealTable(tableName, OperateType.DELETE);
        StringBuilder sql = new StringBuilder();

        sql.append(ALTER_TABLE);
        if (StringUtil.hasText(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.DELETE))).append(REFERENCE);
        }
        sql.append(wrap(table));
        sql.append(CK_DELETE);
        sql.append(WHERE);
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(primaryKeys[i])).append(EQUALS_PLACEHOLDER);
        }
        prepareAuth(schema, table, sql, OperateType.DELETE);
        return sql.toString();
    }

    /**
     * 根据查询更新
     *
     * @param queryWrapper
     * @param row
     * @return
     */
    @Override
    public String forUpdateByQuery(QueryWrapper queryWrapper, Row row) {
        //eg: ALTER TABLE test  UPDATE USERNAME = ? , AGE = ?  WHERE CUSERID = ?
        prepareAuth(queryWrapper, OperateType.UPDATE);
        StringBuilder sql = new StringBuilder();

        Set<String> modifyAttrs = RowCPI.getModifyAttrs(row);
        Map<String, RawValue> rawValueMap = RowCPI.getRawValueMap(row);

        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.size() != 1) {
            throw FlexExceptions.wrap(LocalizedFormats.UPDATE_ONLY_SUPPORT_1_TABLE);
        }
        sql.append(ALTER_TABLE);
        // fix: support schema
        QueryTable queryTable = queryTables.get(0);
        sql.append(queryTable.toSql(this, OperateType.UPDATE)).append(CK_UPDATE);
        int index = 0;
        for (String modifyAttr : modifyAttrs) {
            if (index > 0) {
                sql.append(DELIMITER);
            }

            sql.append(wrap(modifyAttr));

            if (rawValueMap.containsKey(modifyAttr)) {
                sql.append(EQUALS).append(rawValueMap.get(modifyAttr).toSql(this));
            } else {
                sql.append(EQUALS_PLACEHOLDER);
            }

            index++;
        }

        buildJoinSql(sql, queryWrapper, queryTables, OperateType.UPDATE);
        buildWhereSql(sql, queryWrapper, queryTables, false);
        buildGroupBySql(sql, queryWrapper, queryTables);
        buildHavingSql(sql, queryWrapper, queryTables);

        // ignore orderBy and limit
        buildOrderBySql(sql, queryWrapper, queryTables);

        Long limitRows = CPI.getLimitRows(queryWrapper);
        Long limitOffset = CPI.getLimitOffset(queryWrapper);
        if (limitRows != null || limitOffset != null) {
            sql = buildLimitOffsetSql(sql, queryWrapper, limitRows, limitOffset);
        }
        return sql.toString();
    }

    /**
     * 根据主键批量删除
     *
     * @param schema
     * @param tableName
     * @param primaryKeys
     * @param ids
     * @return
     */
    @Override
    public String forDeleteBatchByIds(String schema, String tableName, String[] primaryKeys, Object[] ids) {
        //eg: ALTER TABLE test  DELETE WHERE CUSERID = ?
        String table = getRealTable(tableName, OperateType.DELETE);
        StringBuilder sql = new StringBuilder();
        sql.append(ALTER_TABLE);
        if (StringUtil.hasText(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.DELETE))).append(REFERENCE);
        }
        sql.append(wrap(table));
        sql.append(CK_DELETE);
        sql.append(WHERE);

        // 多主键的场景
        if (primaryKeys.length > 1) {
            for (int i = 0; i < ids.length / primaryKeys.length; i++) {
                if (i > 0) {
                    sql.append(OR);
                }
                sql.append(BRACKET_LEFT);
                for (int j = 0; j < primaryKeys.length; j++) {
                    if (j > 0) {
                        sql.append(AND);
                    }
                    sql.append(wrap(primaryKeys[j])).append(EQUALS_PLACEHOLDER);
                }
                sql.append(BRACKET_RIGHT);
            }
        }
        // 单主键
        else {
            sql.append(wrap(primaryKeys[0])).append(IN).append(BRACKET_LEFT);
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sql.append(DELIMITER);
                }
                sql.append(PLACEHOLDER);
            }
            sql.append(BRACKET_RIGHT);
        }
        prepareAuth(schema, table, sql, OperateType.DELETE);
        return sql.toString();
    }

    /**
     * 实体 根据主键批量删除及逻辑删除
     *
     * @param tableInfo
     * @param primaryValues
     * @return
     */
    @Override
    public String forDeleteEntityBatchByIds(TableInfo tableInfo, Object[] primaryValues) {
        //eg: ALTER TABLE test  UPDATE DR = ?  WHERE CUSERID = ?
        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();

        // 正常删除
        if (StringUtil.noText(logicDeleteColumn)) {
            String deleteSQL = forDeleteBatchByIds(tableInfo.getSchema(), tableInfo.getTableName(), tableInfo.getPrimaryColumns(), primaryValues);

            // 多租户
            if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
                deleteSQL = deleteSQL.replace(WHERE, WHERE + BRACKET_LEFT) + BRACKET_RIGHT;
                deleteSQL = tableInfo.buildTenantCondition(deleteSQL, tenantIdArgs, this);
            }
            return deleteSQL;
        }

        StringBuilder sql = new StringBuilder();
        sql.append(ALTER_TABLE);
        sql.append(tableInfo.getWrapSchemaAndTableName(this, OperateType.UPDATE));
        sql.append(CK_UPDATE).append(buildLogicDeletedSet(logicDeleteColumn, tableInfo));
        sql.append(WHERE);
        sql.append(BRACKET_LEFT);

        String[] primaryKeys = tableInfo.getPrimaryColumns();

        // 多主键的场景
        if (primaryKeys.length > 1) {
            for (int i = 0; i < primaryValues.length / primaryKeys.length; i++) {
                if (i > 0) {
                    sql.append(OR);
                }
                sql.append(BRACKET_LEFT);
                for (int j = 0; j < primaryKeys.length; j++) {
                    if (j > 0) {
                        sql.append(AND);
                    }
                    sql.append(wrap(primaryKeys[j])).append(EQUALS_PLACEHOLDER);
                }
                sql.append(BRACKET_RIGHT);
            }
        }
        // 单主键
        else {
            sql.append(wrap(primaryKeys[0])).append(IN).append(BRACKET_LEFT);
            for (int i = 0; i < primaryValues.length; i++) {
                if (i > 0) {
                    sql.append(DELIMITER);
                }
                sql.append(PLACEHOLDER);
            }
            sql.append(BRACKET_RIGHT);
        }

        sql.append(BRACKET_RIGHT).append(AND).append(buildLogicNormalCondition(logicDeleteColumn, tableInfo));

        tableInfo.buildTenantCondition(sql, tenantIdArgs, this);
        prepareAuth(tableInfo, sql, OperateType.DELETE);
        return sql.toString();
    }

    @Override
    public String buildDeleteSql(QueryWrapper queryWrapper) {
        //eg: ALTER TABLE test  DELETE WHERE CUSERID = ?
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);
        // delete with join
        if (joinTables != null && !joinTables.isEmpty()) {
            throw new IllegalArgumentException("Delete query not support join sql ");
        }
        // ignore selectColumns
        StringBuilder sqlBuilder = new StringBuilder(ALTER_TABLE);
        String hint = CPI.getHint(queryWrapper);
        if (StringUtil.hasText(hint)) {
            sqlBuilder.append(BLANK).append(hint).deleteCharAt(sqlBuilder.length() - 1);
        }

        sqlBuilder.append(StringUtil.join(DELIMITER, queryTables, queryTable -> queryTable.toSql(this, OperateType.DELETE)));
        sqlBuilder.append(CK_DELETE);

        buildWhereSql(sqlBuilder, queryWrapper, allTables, false);
        buildGroupBySql(sqlBuilder, queryWrapper, allTables);
        buildHavingSql(sqlBuilder, queryWrapper, allTables);

        // ignore orderBy and limit
        buildOrderBySql(sqlBuilder, queryWrapper, allTables);

        Long limitRows = CPI.getLimitRows(queryWrapper);
        Long limitOffset = CPI.getLimitOffset(queryWrapper);
        if (limitRows != null || limitOffset != null) {
            sqlBuilder = buildLimitOffsetSql(sqlBuilder, queryWrapper, limitRows, limitOffset);
        }

        List<String> endFragments = CPI.getEndFragments(queryWrapper);
        if (CollectionUtil.isNotEmpty(endFragments)) {
            for (String endFragment : endFragments) {
                sqlBuilder.append(BLANK).append(endFragment);
            }
        }

        return sqlBuilder.toString();
    }

    @Override
    public String forDeleteEntityBatchByQuery(TableInfo tableInfo, QueryWrapper queryWrapper) {
        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();

        // 正常删除
        if (StringUtil.noText(logicDeleteColumn)) {
            return forDeleteByQuery(queryWrapper);
        }


        prepareAuth(queryWrapper, OperateType.DELETE);
        // 逻辑删除
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

        // ignore selectColumns
        //eg: ALTER TABLE test  UPDATE DR = ?  WHERE CUSERID = ?
        StringBuilder sqlBuilder = new StringBuilder(ALTER_TABLE).append(forHint(CPI.getHint(queryWrapper)));
        sqlBuilder.append(tableInfo.getWrapSchemaAndTableName(this, OperateType.DELETE));
        sqlBuilder.append(CK_UPDATE).append(buildLogicDeletedSet(logicDeleteColumn, tableInfo));


        buildJoinSql(sqlBuilder, queryWrapper, allTables, OperateType.DELETE);
        buildWhereSql(sqlBuilder, queryWrapper, allTables, false);
        buildGroupBySql(sqlBuilder, queryWrapper, allTables);
        buildHavingSql(sqlBuilder, queryWrapper, allTables);

        // ignore orderBy and limit
        // buildOrderBySql(sqlBuilder, queryWrapper)
        // buildLimitSql(sqlBuilder, queryWrapper)

        return sqlBuilder.toString();
    }


    @Override
    public String forUpdateEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls) {
        //eg: ALTER TABLE test  UPDATE AGE = ?  WHERE CUSERID = ?
        StringBuilder sql = new StringBuilder();

        Set<String> updateColumns = tableInfo.obtainUpdateColumns(entity, ignoreNulls, false);
        Map<String, RawValue> rawValueMap = tableInfo.obtainUpdateRawValueMap(entity);
        String[] primaryKeys = tableInfo.getPrimaryColumns();

        sql.append(ALTER_TABLE)
                .append(tableInfo.getWrapSchemaAndTableName(this, OperateType.UPDATE))
                .append(CK_UPDATE);

        StringJoiner stringJoiner = new StringJoiner(DELIMITER);

        for (String updateColumn : updateColumns) {
            if (rawValueMap.containsKey(updateColumn)) {
                stringJoiner.add(wrap(updateColumn) + EQUALS + rawValueMap.get(updateColumn).toSql(this));
            } else {
                stringJoiner.add(wrap(updateColumn) + EQUALS_PLACEHOLDER);
            }
        }

        Map<String, String> onUpdateColumns = tableInfo.getOnUpdateColumns();
        if (onUpdateColumns != null && !onUpdateColumns.isEmpty()) {
            onUpdateColumns.forEach((column, value) -> stringJoiner.add(wrap(column) + EQUALS + value));
        }

        // 乐观锁字段
        String versionColumn = tableInfo.getVersionColumn();
        if (StringUtil.hasText(tableInfo.getOptimisticLockColumnOrSkip())) {
            stringJoiner.add(wrap(versionColumn) + EQUALS + wrap(versionColumn) + " + 1 ");
        }

        sql.append(stringJoiner);

        sql.append(WHERE);
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(primaryKeys[i])).append(EQUALS_PLACEHOLDER);
        }

        // 逻辑删除条件，已删除的数据不能被修改
        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();
        if (StringUtil.hasText(logicDeleteColumn)) {
            sql.append(AND).append(buildLogicNormalCondition(logicDeleteColumn, tableInfo));
        }


        // 租户ID字段
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();
        tableInfo.buildTenantCondition(sql, tenantIdArgs, this);

        // 乐观锁条件
        if (StringUtil.hasText(versionColumn)) {
            Object versionValue = tableInfo.buildColumnSqlArg(entity, versionColumn);
            if (versionValue == null) {
                throw FlexExceptions.wrap(LocalizedFormats.ENTITY_VERSION_NULL, entity);
            }
            sql.append(AND).append(wrap(versionColumn)).append(EQUALS).append(versionValue);
        }

        prepareAuth(tableInfo, sql, OperateType.UPDATE);
        return sql.toString();
    }

    @Override
    public String forUpdateEntityByQuery(TableInfo tableInfo, Object entity, boolean ignoreNulls, QueryWrapper queryWrapper) {
        //eg: ALTER TABLE test  UPDATE DR = ?  WHERE CUSERID = ?
        prepareAuth(queryWrapper, OperateType.UPDATE);
        StringBuilder sqlBuilder = new StringBuilder();

        Set<String> updateColumns = tableInfo.obtainUpdateColumns(entity, ignoreNulls, true);
        Map<String, RawValue> rawValueMap = tableInfo.obtainUpdateRawValueMap(entity);

        sqlBuilder.append(ALTER_TABLE).append(forHint(CPI.getHint(queryWrapper)));
        sqlBuilder.append(tableInfo.getWrapSchemaAndTableName(this, OperateType.UPDATE));

        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        buildJoinSql(sqlBuilder, queryWrapper, queryTables, OperateType.UPDATE);

        sqlBuilder.append(CK_UPDATE);

        StringJoiner stringJoiner = new StringJoiner(DELIMITER);

        for (String modifyAttr : updateColumns) {
            if (rawValueMap.containsKey(modifyAttr)) {
                stringJoiner.add(wrap(modifyAttr) + EQUALS + rawValueMap.get(modifyAttr).toSql(this));
            } else {
                stringJoiner.add(wrap(modifyAttr) + EQUALS_PLACEHOLDER);
            }
        }


        Map<String, String> onUpdateColumns = tableInfo.getOnUpdateColumns();
        if (onUpdateColumns != null && !onUpdateColumns.isEmpty()) {
            onUpdateColumns.forEach((column, value) -> stringJoiner.add(wrap(column) + EQUALS + value));
        }

        // 乐观锁字段
        String versionColumn = tableInfo.getVersionColumn();
        if (StringUtil.hasText(tableInfo.getOptimisticLockColumnOrSkip())) {
            stringJoiner.add(wrap(versionColumn) + EQUALS + wrap(versionColumn) + " + 1 ");
        }

        sqlBuilder.append(stringJoiner);


        buildWhereSql(sqlBuilder, queryWrapper, queryTables, false);
        buildGroupBySql(sqlBuilder, queryWrapper, queryTables);
        buildHavingSql(sqlBuilder, queryWrapper, queryTables);

        // ignore orderBy and limit
        buildOrderBySql(sqlBuilder, queryWrapper, queryTables);

        Long limitRows = CPI.getLimitRows(queryWrapper);
        Long limitOffset = CPI.getLimitOffset(queryWrapper);
        if (limitRows != null || limitOffset != null) {
            sqlBuilder = buildLimitOffsetSql(sqlBuilder, queryWrapper, limitRows, limitOffset);
        }


        List<String> endFragments = CPI.getEndFragments(queryWrapper);
        if (CollectionUtil.isNotEmpty(endFragments)) {
            for (String endFragment : endFragments) {
                sqlBuilder.append(BLANK).append(endFragment);
            }
        }

        return sqlBuilder.toString();
    }
}
