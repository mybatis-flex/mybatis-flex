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
package com.mybatisflex.core.dialect.impl;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowCPI;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import static com.mybatisflex.core.constant.SqlConsts.*;

/**
 * 通用的方言设计，其他方言可以继承于当前 CommonsDialectImpl
 * 创建或获取方言请参考 {@link com.mybatisflex.core.dialect.DialectFactory}
 */
public class CommonsDialectImpl implements IDialect {

    protected KeywordWrap keywordWrap = KeywordWrap.BACK_QUOTE;
    private LimitOffsetProcessor limitOffsetProcessor = LimitOffsetProcessor.MYSQL;

    public CommonsDialectImpl() {
    }

    public CommonsDialectImpl(LimitOffsetProcessor limitOffsetProcessor) {
        this.limitOffsetProcessor = limitOffsetProcessor;
    }

    public CommonsDialectImpl(KeywordWrap keywordWrap, LimitOffsetProcessor limitOffsetProcessor) {
        this.keywordWrap = keywordWrap;
        this.limitOffsetProcessor = limitOffsetProcessor;
    }

    @Override
    public String wrap(String keyword) {
        return ASTERISK.equals(keyword) ? keyword : keywordWrap.wrap(keyword);
    }


    @Override
    public String forHint(String hintString) {
        return StringUtil.isNotBlank(hintString) ? HINT_START + hintString + HINT_END : EMPTY;
    }

    @Override
    public String forInsertRow(String schema, String tableName, Row row) {
        StringBuilder fields = new StringBuilder();
        StringBuilder questions = new StringBuilder();

        Set<String> attrs = row.obtainModifyAttrs();
        int index = 0;
        for (String attr : attrs) {
            fields.append(wrap(attr));
            questions.append(PLACEHOLDER);
            if (index != attrs.size() - 1) {
                fields.append(DELIMITER);
                questions.append(DELIMITER);
            }
            index++;
        }
        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema))).append(REFERENCE);
        }
        sql.append(wrap(getRealTable(tableName)));
        sql.append(BRACKET_LEFT).append(fields).append(BRACKET_RIGHT);
        sql.append(VALUES).append(BRACKET_LEFT).append(questions).append(BRACKET_RIGHT);
        return sql.toString();
    }


    @Override
    public String forInsertBatchWithFirstRowColumns(String schema, String tableName, List<Row> rows) {
        StringBuilder fields = new StringBuilder();
        StringBuilder questions = new StringBuilder();

        Row firstRow = rows.get(0);
        Set<String> attrs = firstRow.obtainModifyAttrs();
        int index = 0;
        for (String column : attrs) {
            fields.append(wrap(column));
            if (index != attrs.size() - 1) {
                fields.append(DELIMITER);
            }
            index++;
        }

        for (int i = 0; i < rows.size(); i++) {
            questions.append(buildQuestion(attrs.size()));
            if (i != rows.size() - 1) {
                questions.append(DELIMITER);
            }
        }


        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema))).append(REFERENCE);
        }
        sql.append(wrap(getRealTable(tableName)));
        sql.append(BLANK).append(BRACKET_LEFT)
                .append(fields)
                .append(BRACKET_RIGHT).append(BLANK);
        sql.append(VALUES).append(questions);
        return sql.toString();
    }


    @Override
    public String forDeleteById(String schema, String tableName, String[] primaryKeys) {
        StringBuilder sql = new StringBuilder();
        sql.append(DELETE_FROM);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema))).append(REFERENCE);
        }
        sql.append(wrap(getRealTable(tableName)));
        sql.append(WHERE);
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(primaryKeys[i])).append(EQUALS_PLACEHOLDER);
        }
        return sql.toString();
    }


    @Override
    public String forDeleteBatchByIds(String schema, String tableName, String[] primaryKeys, Object[] ids) {
        StringBuilder sql = new StringBuilder();
        sql.append(DELETE_FROM);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema))).append(REFERENCE);
        }
        sql.append(wrap(getRealTable(tableName)));
        sql.append(WHERE);

        //多主键的场景
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
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sql.append(OR);
                }
                sql.append(wrap(primaryKeys[0])).append(EQUALS_PLACEHOLDER);
            }
        }
        return sql.toString();
    }

    @Override
    public String forDeleteByQuery(QueryWrapper queryWrapper) {
        return buildDeleteSql(queryWrapper);
    }

    @Override
    public String forUpdateById(String schema, String tableName, Row row) {
        StringBuilder sql = new StringBuilder();

        Set<String> modifyAttrs = row.obtainModifyAttrs();
        String[] primaryKeys = RowCPI.obtainsPrimaryKeyStrings(row);

        sql.append(UPDATE);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema))).append(REFERENCE);
        }
        sql.append(wrap(getRealTable(tableName))).append(SET);
        int index = 0;
        for (Map.Entry<String, Object> e : row.entrySet()) {
            String colName = e.getKey();
            if (modifyAttrs.contains(colName) && !ArrayUtil.contains(primaryKeys, colName)) {
                if (index > 0) {
                    sql.append(DELIMITER);
                }
                sql.append(wrap(colName)).append(EQUALS_PLACEHOLDER);
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

        return sql.toString();
    }

    @Override
    public String forUpdateByQuery(QueryWrapper queryWrapper, Row row) {
        StringBuilder sql = new StringBuilder();

        Set<String> modifyAttrs = row.obtainModifyAttrs();

        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.size() != 1) {
            throw FlexExceptions.wrap("update sql must need 1 table.");
        }

        //fix: support schema
        QueryTable queryTable = queryTables.get(0);
//        String tableName = queryTables.get(0).getName();
        sql.append(UPDATE).append(queryTable.toSql(this)).append(SET);
        int index = 0;
        for (String modifyAttr : modifyAttrs) {
            if (index > 0) {
                sql.append(DELIMITER);
            }
            sql.append(wrap(modifyAttr)).append(EQUALS_PLACEHOLDER);
            index++;
        }

        String whereConditionSql = buildWhereConditionSql(queryWrapper);
        if (StringUtil.isNotBlank(whereConditionSql)) {
            sql.append(WHERE).append(whereConditionSql);
        }

        return sql.toString();
    }

    @Override
    public String forUpdateBatchById(String schema, String tableName, List<Row> rows) {
        if (rows.size() == 1) {
            return forUpdateById(schema, tableName, rows.get(0));
        }
        StringBuilder sql = new StringBuilder();
        for (Row row : rows) {
            sql.append(forUpdateById(schema, tableName, row)).append(SEMICOLON).append(BLANK);
        }
        return sql.toString();
    }


    @Override
    public String forSelectOneById(String schema, String tableName, String[] primaryKeys, Object[] primaryValues) {
        StringBuilder sql = new StringBuilder(SELECT_ALL_FROM);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema))).append(REFERENCE);
        }
        sql.append(wrap(getRealTable(tableName))).append(WHERE);
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(primaryKeys[i])).append(EQUALS_PLACEHOLDER);
        }
        return sql.toString();
    }

    @Override
    public String forSelectByQuery(QueryWrapper queryWrapper) {
        return buildSelectSql(queryWrapper);
    }


    ////////////build query sql///////
    @Override
    public String buildSelectSql(QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

        List<QueryColumn> selectColumns = CPI.getSelectColumns(queryWrapper);

        StringBuilder sqlBuilder = buildSelectColumnSql(allTables, selectColumns, CPI.getHint(queryWrapper));
        sqlBuilder.append(FROM).append(StringUtil.join(DELIMITER, queryTables, queryTable -> queryTable.toSql(this)));

        buildJoinSql(sqlBuilder, queryWrapper, allTables);
        buildWhereSql(sqlBuilder, queryWrapper, allTables, true);
        buildGroupBySql(sqlBuilder, queryWrapper, allTables);
        buildHavingSql(sqlBuilder, queryWrapper, allTables);
        buildOrderBySql(sqlBuilder, queryWrapper, allTables);

        List<UnionWrapper> unions = CPI.getUnions(queryWrapper);
        if (CollectionUtil.isNotEmpty(unions)) {
            sqlBuilder.insert(0, BRACKET_LEFT).append(BRACKET_RIGHT);
            for (UnionWrapper unionWrapper : unions) {
                unionWrapper.buildSql(sqlBuilder, this);
            }
        }

        Integer limitRows = CPI.getLimitRows(queryWrapper);
        Integer limitOffset = CPI.getLimitOffset(queryWrapper);
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

    private StringBuilder buildSelectColumnSql(List<QueryTable> queryTables, List<QueryColumn> selectColumns, String hint) {
        StringBuilder sqlBuilder = new StringBuilder(SELECT);
        sqlBuilder.append(forHint(hint));
        if (selectColumns == null || selectColumns.isEmpty()) {
            sqlBuilder.append(ASTERISK);
        } else {
            int index = 0;
            for (QueryColumn selectColumn : selectColumns) {
                String selectColumnSql = CPI.toSelectSql(selectColumn, queryTables, this);
                sqlBuilder.append(selectColumnSql);
                if (index != selectColumns.size() - 1) {
                    sqlBuilder.append(DELIMITER);
                }
                index++;
            }
        }
        return sqlBuilder;
    }


    @Override
    public String buildDeleteSql(QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

        //ignore selectColumns
        StringBuilder sqlBuilder = new StringBuilder(DELETE);
        String hint = CPI.getHint(queryWrapper);
        if (StringUtil.isNotBlank(hint)) {
            sqlBuilder.append(BLANK).append(hint).deleteCharAt(sqlBuilder.length() - 1);
        }
        sqlBuilder.append(FROM).append(StringUtil.join(DELIMITER, queryTables, queryTable -> queryTable.toSql(this)));

        buildJoinSql(sqlBuilder, queryWrapper, allTables);
        buildWhereSql(sqlBuilder, queryWrapper, allTables, false);
        buildGroupBySql(sqlBuilder, queryWrapper, allTables);
        buildHavingSql(sqlBuilder, queryWrapper, allTables);

        //ignore orderBy and limit
        //buildOrderBySql(sqlBuilder, queryWrapper);
        //buildLimitSql(sqlBuilder, queryWrapper);

        List<String> endFragments = CPI.getEndFragments(queryWrapper);
        if (CollectionUtil.isNotEmpty(endFragments)) {
            for (String endFragment : endFragments) {
                sqlBuilder.append(BLANK).append(endFragment);
            }
        }

        return sqlBuilder.toString();
    }

    @Override
    public String buildWhereConditionSql(QueryWrapper queryWrapper) {
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        return whereQueryCondition != null ? whereQueryCondition.toSql(CPI.getQueryTables(queryWrapper), this) : EMPTY;
    }

    @Override
    public String forInsertEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls) {
        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO).append(tableInfo.getWrapSchemaAndTableName(this));

        String[] insertColumns = tableInfo.obtainInsertColumns(entity, ignoreNulls);
        Map<String, String> onInsertColumns = tableInfo.getOnInsertColumns();

        StringJoiner sqlFields = new StringJoiner(DELIMITER);
        StringJoiner sqlValues = new StringJoiner(DELIMITER);

        for (String insertColumn : insertColumns) {
            sqlFields.add(wrap(insertColumn));
            if (onInsertColumns != null && onInsertColumns.containsKey(insertColumn)) {
                sqlValues.add(onInsertColumns.get(insertColumn));
            } else {
                sqlValues.add(PLACEHOLDER);
            }
        }

        return sql.append(BRACKET_LEFT).append(sqlFields).append(BRACKET_RIGHT)
                .append(VALUES)
                .append(BRACKET_LEFT).append(sqlValues).append(BRACKET_RIGHT)
                .toString();
    }

    @Override
    public String forInsertEntityBatch(TableInfo tableInfo, List<?> entities) {
        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO).append(tableInfo.getWrapSchemaAndTableName(this));
        String[] insertColumns = tableInfo.obtainInsertColumns(null, false);
        String[] warpedInsertColumns = new String[insertColumns.length];
        for (int i = 0; i < insertColumns.length; i++) {
            warpedInsertColumns[i] = wrap(insertColumns[i]);
        }
        sql.append(BRACKET_LEFT)
                .append(StringUtil.join(DELIMITER, warpedInsertColumns))
                .append(BRACKET_RIGHT);
        sql.append(VALUES);

        Map<String, String> onInsertColumns = tableInfo.getOnInsertColumns();
        for (int i = 0; i < entities.size(); i++) {
            StringJoiner stringJoiner = new StringJoiner(DELIMITER, BRACKET_LEFT, BRACKET_RIGHT);
            for (String insertColumn : insertColumns) {
                if (onInsertColumns != null && onInsertColumns.containsKey(insertColumn)) {
                    //直接读取 onInsert 配置的值，而不用 "?" 代替
                    stringJoiner.add(onInsertColumns.get(insertColumn));
                } else {
                    stringJoiner.add(PLACEHOLDER);
                }
            }
            sql.append(stringJoiner);
            if (i != entities.size() - 1) {
                sql.append(DELIMITER);
            }
        }

        return sql.toString();
    }

    @Override
    public String forDeleteEntityById(TableInfo tableInfo) {
        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();
        //正常删除
        if (StringUtil.isBlank(logicDeleteColumn)) {
            String deleteByIdSql = forDeleteById(tableInfo.getSchema(), tableInfo.getTableName(), tableInfo.getPrimaryKeys());

            if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
                deleteByIdSql += AND + wrap(tableInfo.getTenantIdColumn()) + IN + buildQuestion(tenantIdArgs.length);
            }
            return deleteByIdSql;
        }

        //逻辑删除
        StringBuilder sql = new StringBuilder();
        String[] primaryKeys = tableInfo.getPrimaryKeys();

        sql.append(UPDATE).append(tableInfo.getWrapSchemaAndTableName(this));
        sql.append(SET).append(wrap(logicDeleteColumn)).append(EQUALS).append(getLogicDeletedValue());
        sql.append(WHERE);
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(primaryKeys[i])).append(EQUALS_PLACEHOLDER);
        }

        sql.append(AND).append(wrap(logicDeleteColumn)).append(EQUALS).append(getLogicNormalValue());

        //租户ID
        if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
            sql.append(AND).append(wrap(tableInfo.getTenantIdColumn())).append(IN).append(buildQuestion(tenantIdArgs.length));
        }

        return sql.toString();
    }


    @Override
    public String forDeleteEntityBatchByIds(TableInfo tableInfo, Object[] primaryValues) {
        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();

        //正常删除
        if (StringUtil.isBlank(logicDeleteColumn)) {
            String deleteSQL = forDeleteBatchByIds(tableInfo.getSchema(), tableInfo.getTableName(), tableInfo.getPrimaryKeys(), primaryValues);

            //多租户
            if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
                deleteSQL = deleteSQL.replace(WHERE, WHERE + BRACKET_LEFT) + BRACKET_RIGHT;
                deleteSQL += AND + wrap(tableInfo.getTenantIdColumn()) + IN + buildQuestion(tenantIdArgs.length);
            }
            return deleteSQL;
        }

        StringBuilder sql = new StringBuilder();
        sql.append(UPDATE);
        sql.append(tableInfo.getWrapSchemaAndTableName(this));
        sql.append(SET).append(wrap(logicDeleteColumn)).append(EQUALS).append(getLogicDeletedValue());
        sql.append(WHERE);
        sql.append(BRACKET_LEFT);

        String[] primaryKeys = tableInfo.getPrimaryKeys();

        //多主键的场景
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
            for (int i = 0; i < primaryValues.length; i++) {
                if (i > 0) {
                    sql.append(OR);
                }
                sql.append(wrap(primaryKeys[0])).append(EQUALS_PLACEHOLDER);
            }
        }

        sql.append(BRACKET_RIGHT).append(AND).append(wrap(logicDeleteColumn)).append(EQUALS).append(getLogicNormalValue());

        if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
            sql.append(AND).append(wrap(tableInfo.getTenantIdColumn())).append(IN).append(buildQuestion(tenantIdArgs.length));
        }

        return sql.toString();
    }

    @Override
    public String forDeleteEntityBatchByQuery(TableInfo tableInfo, QueryWrapper queryWrapper) {

        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();

        //正常删除
        if (StringUtil.isBlank(logicDeleteColumn)) {
            return forDeleteByQuery(queryWrapper);
        }


        //逻辑删除
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

        //ignore selectColumns
        StringBuilder sqlBuilder = new StringBuilder(UPDATE).append(forHint(CPI.getHint(queryWrapper)));
        sqlBuilder.append(tableInfo.getWrapSchemaAndTableName(this));
        sqlBuilder.append(SET).append(wrap(logicDeleteColumn)).append(EQUALS).append(getLogicDeletedValue());


        buildJoinSql(sqlBuilder, queryWrapper, allTables);
        buildWhereSql(sqlBuilder, queryWrapper, allTables, false);
        buildGroupBySql(sqlBuilder, queryWrapper, allTables);
        buildHavingSql(sqlBuilder, queryWrapper, allTables);

        //ignore orderBy and limit
        //buildOrderBySql(sqlBuilder, queryWrapper);
        //buildLimitSql(sqlBuilder, queryWrapper);

        return sqlBuilder.toString();
    }


    @Override
    public String forUpdateEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls) {
        StringBuilder sql = new StringBuilder();

        Set<String> modifyAttrs = tableInfo.obtainUpdateColumns(entity, ignoreNulls, false);
        String[] primaryKeys = tableInfo.getPrimaryKeys();

        sql.append(UPDATE).append(tableInfo.getWrapSchemaAndTableName(this)).append(SET);

        StringJoiner stringJoiner = new StringJoiner(DELIMITER);

        for (String modifyAttr : modifyAttrs) {
            stringJoiner.add(wrap(modifyAttr) + EQUALS_PLACEHOLDER);
        }

        Map<String, String> onUpdateColumns = tableInfo.getOnUpdateColumns();
        if (onUpdateColumns != null && !onUpdateColumns.isEmpty()) {
            onUpdateColumns.forEach((column, value) -> stringJoiner.add(wrap(column) + EQUALS + value));
        }

        //乐观锁字段
        String versionColumn = tableInfo.getVersionColumn();
        if (StringUtil.isNotBlank(versionColumn)) {
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

        //逻辑删除条件，已删除的数据不能被修改
        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
        if (StringUtil.isNotBlank(logicDeleteColumn)) {
            sql.append(AND).append(wrap(logicDeleteColumn)).append(EQUALS).append(getLogicNormalValue());
        }


        //租户ID字段
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();
        if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
            if (tenantIdArgs.length == 1) {
                sql.append(AND).append(wrap(tableInfo.getTenantIdColumn())).append(EQUALS_PLACEHOLDER);
            } else {
                sql.append(AND).append(wrap(tableInfo.getTenantIdColumn())).append(IN).append(buildQuestion(tenantIdArgs.length));
            }
        }

        //乐观锁条件
        if (StringUtil.isNotBlank(versionColumn)) {
            Object versionValue = tableInfo.buildColumnSqlArg(entity, versionColumn);
            if (versionValue == null) {
                throw FlexExceptions.wrap("The version value of entity[%s] must not be null.", entity);
            }
            sql.append(AND).append(wrap(versionColumn)).append(EQUALS).append(versionValue);
        }


        return sql.toString();
    }

    @Override
    public String forUpdateEntityByQuery(TableInfo tableInfo, Object entity, boolean ignoreNulls, QueryWrapper queryWrapper) {
        StringBuilder sql = new StringBuilder();

        Set<String> modifyAttrs = tableInfo.obtainUpdateColumns(entity, ignoreNulls, true);

        sql.append(UPDATE).append(forHint(CPI.getHint(queryWrapper)))
                .append(tableInfo.getWrapSchemaAndTableName(this)).append(SET);

        StringJoiner stringJoiner = new StringJoiner(DELIMITER);

        for (String modifyAttr : modifyAttrs) {
            stringJoiner.add(wrap(modifyAttr) + EQUALS_PLACEHOLDER);
        }

        Map<String, String> onUpdateColumns = tableInfo.getOnUpdateColumns();
        if (onUpdateColumns != null && !onUpdateColumns.isEmpty()) {
            onUpdateColumns.forEach((column, value) -> stringJoiner.add(wrap(column) + EQUALS + value));
        }

        //乐观锁字段
        String versionColumn = tableInfo.getVersionColumn();
        if (StringUtil.isNotBlank(versionColumn)) {
            stringJoiner.add(wrap(versionColumn) + EQUALS + wrap(versionColumn) + " + 1 ");
        }

        sql.append(stringJoiner);


        String whereConditionSql = buildWhereConditionSql(queryWrapper);

        //不允许全量更新
        if (StringUtil.isBlank(whereConditionSql)) {
            throw new IllegalArgumentException("Not allowed UPDATE a table without where condition.");
        }

        sql.append(WHERE).append(whereConditionSql);

        List<String> endFragments = CPI.getEndFragments(queryWrapper);
        if (CollectionUtil.isNotEmpty(endFragments)) {
            for (String endFragment : endFragments) {
                sql.append(BLANK).append(endFragment);
            }
        }

        return sql.toString();
    }


    @Override
    public String forUpdateNumberAddByQuery(String schema, String tableName, String fieldName, Number value, QueryWrapper queryWrapper) {
        StringBuilder sql = new StringBuilder();
        sql.append(UPDATE).append(forHint(CPI.getHint(queryWrapper)));
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema))).append(REFERENCE);
        }
        sql.append(wrap(getRealTable(tableName))).append(SET);
        sql.append(wrap(fieldName)).append(EQUALS).append(wrap(fieldName)).append(geZero(value) ? PLUS_SIGN : MINUS_SIGN).append(abs(value));

        String whereConditionSql = buildWhereConditionSql(queryWrapper);

        //不允许全量更新
        if (StringUtil.isBlank(whereConditionSql)) {
            throw new IllegalArgumentException("Not allowed UPDATE a table without where condition.");
        }

        sql.append(WHERE).append(whereConditionSql);

        List<String> endFragments = CPI.getEndFragments(queryWrapper);
        if (CollectionUtil.isNotEmpty(endFragments)) {
            for (String endFragment : endFragments) {
                sql.append(BLANK).append(endFragment);
            }
        }
        return sql.toString();
    }


    protected boolean geZero(Number number) {
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).signum() >= 0;
        } else if (number instanceof BigInteger) {
            return ((BigInteger) number).signum() >= 0;
        } else if (number instanceof Float) {
            return (Float) number >= 0;
        } else if (number instanceof Double) {
            return (Double) number >= 0;
        } else {
            return number.longValue() >= 0;
        }
    }


    protected Number abs(Number number) {
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).abs();
        } else if (number instanceof BigInteger) {
            return ((BigInteger) number).abs();
        } else if (number instanceof Float) {
            return Math.abs((Float) number);
        } else if (number instanceof Double) {
            return Math.abs((Double) number);
        } else {
            return Math.abs(number.longValue());
        }
    }


    @Override
    public String forSelectOneEntityById(TableInfo tableInfo) {
        StringBuilder sql = buildSelectColumnSql(null, null, null);
        sql.append(FROM).append(tableInfo.getWrapSchemaAndTableName(this));
        sql.append(WHERE);
        String[] pKeys = tableInfo.getPrimaryKeys();
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(pKeys[i])).append(EQUALS_PLACEHOLDER);
        }

        //逻辑删除的情况下，需要添加逻辑删除的条件
        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
        if (StringUtil.isNotBlank(logicDeleteColumn)) {
            sql.append(AND).append(wrap(logicDeleteColumn)).append(EQUALS).append(getLogicNormalValue());
        }

        //多租户
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();
        if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
            sql.append(AND).append(wrap(tableInfo.getTenantIdColumn())).append(IN).append(buildQuestion(tenantIdArgs.length));
        }

        return sql.toString();
    }


    @Override
    public String forSelectEntityListByIds(TableInfo tableInfo, Object[] primaryValues) {
        StringBuilder sql = buildSelectColumnSql(null, tableInfo.getDefaultQueryColumn(), null);
        sql.append(FROM).append(tableInfo.getWrapSchemaAndTableName(this));
        sql.append(WHERE);
        String[] primaryKeys = tableInfo.getPrimaryKeys();

        String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();
        if (StringUtil.isNotBlank(logicDeleteColumn) || ArrayUtil.isNotEmpty(tenantIdArgs)) {
            sql.append(BRACKET_LEFT);
        }

        //多主键的场景
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
            for (int i = 0; i < primaryValues.length; i++) {
                if (i > 0) {
                    sql.append(OR);
                }
                sql.append(wrap(primaryKeys[0])).append(EQUALS_PLACEHOLDER);
            }
        }

        if (StringUtil.isNotBlank(logicDeleteColumn) || ArrayUtil.isNotEmpty(tenantIdArgs)) {
            sql.append(BRACKET_RIGHT);
        }


        if (StringUtil.isNotBlank(logicDeleteColumn)) {
            sql.append(AND).append(wrap(logicDeleteColumn)).append(EQUALS).append(getLogicNormalValue());
        }

        if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
            sql.append(AND).append(wrap(tableInfo.getTenantIdColumn())).append(IN).append(buildQuestion(tenantIdArgs.length));
        }

        return sql.toString();
    }


    protected void buildJoinSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (joins != null && !joins.isEmpty()) {
            for (Join join : joins) {
                if (!join.checkEffective()) {
                    continue;
                }
                sqlBuilder.append(join.toSql(queryTables, this));
            }
        }
    }


    protected void buildWhereSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables, boolean allowNoCondition) {
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        if (whereQueryCondition != null) {
            String whereSql = whereQueryCondition.toSql(queryTables, this);
            if (StringUtil.isNotBlank(whereSql)) {
                sqlBuilder.append(WHERE).append(whereSql);
            } else if (!allowNoCondition) {
                throw new IllegalArgumentException("Not allowed DELETE a table without where condition.");
            }
        }
    }


    protected void buildGroupBySql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        List<QueryColumn> groupByColumns = CPI.getGroupByColumns(queryWrapper);
        if (groupByColumns != null && !groupByColumns.isEmpty()) {
            sqlBuilder.append(GROUP_BY);
            int index = 0;
            for (QueryColumn groupByColumn : groupByColumns) {
                String groupBy = CPI.toConditionSql(groupByColumn, queryTables, this);
                sqlBuilder.append(groupBy);
                if (index != groupByColumns.size() - 1) {
                    sqlBuilder.append(DELIMITER);
                }
                index++;
            }
        }
    }


    protected void buildHavingSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        QueryCondition havingQueryCondition = CPI.getHavingQueryCondition(queryWrapper);
        if (havingQueryCondition != null) {
            String havingSql = havingQueryCondition.toSql(queryTables, this);
            if (StringUtil.isNotBlank(havingSql)) {
                sqlBuilder.append(HAVING).append(havingSql);
            }
        }
    }


    protected void buildOrderBySql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        List<QueryOrderBy> orderBys = CPI.getOrderBys(queryWrapper);
        if (orderBys != null && !orderBys.isEmpty()) {
            sqlBuilder.append(ORDER_BY);
            int index = 0;
            for (QueryOrderBy orderBy : orderBys) {
                sqlBuilder.append(orderBy.toSql(queryTables, this));
                if (index != orderBys.size() - 1) {
                    sqlBuilder.append(DELIMITER);
                }
                index++;
            }
        }
    }


    /**
     * 构建 limit 和 offset 的参数
     */
    protected StringBuilder buildLimitOffsetSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, Integer limitRows, Integer limitOffset) {
        return limitOffsetProcessor.process(sqlBuilder, queryWrapper, limitRows, limitOffset);
    }


    protected String buildQuestion(int count) {
        StringBuilder sb = new StringBuilder(BRACKET_LEFT);
        for (int i = 0; i < count; i++) {
            sb.append(PLACEHOLDER);
            if (i != count - 1) {
                sb.append(DELIMITER);
            }
        }
        sb.append(BRACKET_RIGHT);
        return sb.toString();
    }


    protected Object getLogicNormalValue() {
        Object normalValueOfLogicDelete = FlexGlobalConfig.getDefaultConfig().getNormalValueOfLogicDelete();
        if (normalValueOfLogicDelete instanceof Number
                || normalValueOfLogicDelete instanceof Boolean) {
            return normalValueOfLogicDelete;
        }
        return SINGLE_QUOTE + normalValueOfLogicDelete + SINGLE_QUOTE;
    }


    protected Object getLogicDeletedValue() {
        Object deletedValueOfLogicDelete = FlexGlobalConfig.getDefaultConfig().getDeletedValueOfLogicDelete();
        if (deletedValueOfLogicDelete instanceof Number
                || deletedValueOfLogicDelete instanceof Boolean) {
            return deletedValueOfLogicDelete;
        }
        return SINGLE_QUOTE + deletedValueOfLogicDelete + SINGLE_QUOTE;
    }

}
