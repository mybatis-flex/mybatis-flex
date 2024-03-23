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

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.exception.locale.LocalizedFormats;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.Join;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryOrderBy;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.UnionWrapper;
import com.mybatisflex.core.query.With;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowCPI;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.update.RawValue;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.SqlUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mybatisflex.core.constant.SqlConsts.AND;
import static com.mybatisflex.core.constant.SqlConsts.ASTERISK;
import static com.mybatisflex.core.constant.SqlConsts.BLANK;
import static com.mybatisflex.core.constant.SqlConsts.BRACKET_LEFT;
import static com.mybatisflex.core.constant.SqlConsts.BRACKET_RIGHT;
import static com.mybatisflex.core.constant.SqlConsts.DELETE;
import static com.mybatisflex.core.constant.SqlConsts.DELETE_FROM;
import static com.mybatisflex.core.constant.SqlConsts.DELIMITER;
import static com.mybatisflex.core.constant.SqlConsts.EMPTY;
import static com.mybatisflex.core.constant.SqlConsts.EQUALS;
import static com.mybatisflex.core.constant.SqlConsts.EQUALS_PLACEHOLDER;
import static com.mybatisflex.core.constant.SqlConsts.FROM;
import static com.mybatisflex.core.constant.SqlConsts.GROUP_BY;
import static com.mybatisflex.core.constant.SqlConsts.HAVING;
import static com.mybatisflex.core.constant.SqlConsts.HINT_END;
import static com.mybatisflex.core.constant.SqlConsts.HINT_START;
import static com.mybatisflex.core.constant.SqlConsts.INSERT_INTO;
import static com.mybatisflex.core.constant.SqlConsts.OR;
import static com.mybatisflex.core.constant.SqlConsts.ORDER_BY;
import static com.mybatisflex.core.constant.SqlConsts.PLACEHOLDER;
import static com.mybatisflex.core.constant.SqlConsts.REFERENCE;
import static com.mybatisflex.core.constant.SqlConsts.SELECT;
import static com.mybatisflex.core.constant.SqlConsts.SELECT_ALL_FROM;
import static com.mybatisflex.core.constant.SqlConsts.SEMICOLON;
import static com.mybatisflex.core.constant.SqlConsts.SET;
import static com.mybatisflex.core.constant.SqlConsts.UPDATE;
import static com.mybatisflex.core.constant.SqlConsts.VALUES;
import static com.mybatisflex.core.constant.SqlConsts.WHERE;

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
    public String wrapColumnAlias(String keyword) {
//        return ASTERISK.equals(keyword) ? keyword : keywordWrap.getPrefix() + keyword + keywordWrap.getSuffix();
        return ASTERISK.equals(keyword) ? keyword : keywordWrap.wrap(keyword);
    }

    @Override
    public String forHint(String hintString) {
        return StringUtil.isNotBlank(hintString) ? HINT_START + hintString + HINT_END : EMPTY;
    }

    @Override
    public String forInsertRow(String schema, String tableName, Row row) {
        StringBuilder fields = new StringBuilder();
        StringBuilder paramsOrPlaceholder = new StringBuilder();

        // 插入数据时，可能包含主键
        Set<String> modifyAttrs = RowCPI.getInsertAttrs(row);
        int index = 0;
        for (String attr : modifyAttrs) {
            fields.append(wrap(attr));

            Object value = row.get(attr);
            if (value instanceof RawValue) {
                paramsOrPlaceholder.append(((RawValue) value).toSql(this));
            } else {
                paramsOrPlaceholder.append(PLACEHOLDER);
            }
            if (index != modifyAttrs.size() - 1) {
                fields.append(DELIMITER);
                paramsOrPlaceholder.append(DELIMITER);
            }
            index++;
        }

        String table = getRealTable(tableName, OperateType.INSERT);
        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.INSERT))).append(REFERENCE);
        }
        sql.append(wrap(table));
        sql.append(BRACKET_LEFT).append(fields).append(BRACKET_RIGHT);
        sql.append(VALUES).append(BRACKET_LEFT).append(paramsOrPlaceholder).append(BRACKET_RIGHT);
        return sql.toString();
    }


    @Override
    public String forInsertBatchWithFirstRowColumns(String schema, String tableName, List<Row> rows) {
        StringBuilder fields = new StringBuilder();
        StringBuilder questions = new StringBuilder();

        Row firstRow = rows.get(0);
        Set<String> attrs = RowCPI.getInsertAttrs(firstRow);
        int index = 0;
        for (String column : attrs) {
            fields.append(wrap(column));
            if (index != attrs.size() - 1) {
                fields.append(DELIMITER);
            }
            index++;
        }

        for (int i = 0; i < rows.size(); i++) {
            questions.append(SqlUtil.buildSqlParamPlaceholder(attrs.size()));
            if (i != rows.size() - 1) {
                questions.append(DELIMITER);
            }
        }

        String table = getRealTable(tableName, OperateType.INSERT);
        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.INSERT))).append(REFERENCE);
        }
        sql.append(wrap(table));
        sql.append(BLANK).append(BRACKET_LEFT)
            .append(fields)
            .append(BRACKET_RIGHT).append(BLANK);
        sql.append(VALUES).append(questions);
        return sql.toString();
    }


    @Override
    public String forDeleteById(String schema, String tableName, String[] primaryKeys) {
        String table = getRealTable(tableName, OperateType.DELETE);
        StringBuilder sql = new StringBuilder();
        sql.append(DELETE_FROM);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.DELETE))).append(REFERENCE);
        }
        sql.append(wrap(table));
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


    @Override
    public String forDeleteBatchByIds(String schema, String tableName, String[] primaryKeys, Object[] ids) {
        String table = getRealTable(tableName, OperateType.DELETE);
        StringBuilder sql = new StringBuilder();
        sql.append(DELETE_FROM);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.DELETE))).append(REFERENCE);
        }

        sql.append(wrap(table));
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
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sql.append(OR);
                }
                sql.append(wrap(primaryKeys[0])).append(EQUALS_PLACEHOLDER);
            }
        }
        prepareAuth(schema, table, sql, OperateType.DELETE);
        return sql.toString();
    }

    @Override
    public String forDeleteByQuery(QueryWrapper queryWrapper) {
        prepareAuth(queryWrapper, OperateType.DELETE);
        return buildDeleteSql(queryWrapper);
    }

    @Override
    public String forUpdateById(String schema, String tableName, Row row) {
        String table = getRealTable(tableName, OperateType.UPDATE);
        StringBuilder sql = new StringBuilder();
        Set<String> modifyAttrs = RowCPI.getModifyAttrs(row);
        Map<String, RawValue> rawValueMap = RowCPI.getRawValueMap(row);
        String[] primaryKeys = RowCPI.obtainsPrimaryKeyStrings(row);

        sql.append(UPDATE);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.UPDATE))).append(REFERENCE);
        }

        sql.append(wrap(table)).append(SET);
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

    @Override
    public String forUpdateByQuery(QueryWrapper queryWrapper, Row row) {
        prepareAuth(queryWrapper, OperateType.UPDATE);
        StringBuilder sqlBuilder = new StringBuilder();

        Set<String> modifyAttrs = RowCPI.getModifyAttrs(row);
        Map<String, RawValue> rawValueMap = RowCPI.getRawValueMap(row);

        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.size() != 1) {
            throw FlexExceptions.wrap(LocalizedFormats.UPDATE_ONLY_SUPPORT_1_TABLE);
        }

        // fix: support schema
        QueryTable queryTable = queryTables.get(0);
        sqlBuilder.append(UPDATE).append(queryTable.toSql(this, OperateType.UPDATE)).append(SET);
        int index = 0;
        for (String modifyAttr : modifyAttrs) {
            if (index > 0) {
                sqlBuilder.append(DELIMITER);
            }

            sqlBuilder.append(wrap(modifyAttr));

            if (rawValueMap.containsKey(modifyAttr)) {
                sqlBuilder.append(EQUALS).append(rawValueMap.get(modifyAttr).toSql(this));
            } else {
                sqlBuilder.append(EQUALS_PLACEHOLDER);
            }

            index++;
        }

        buildJoinSql(sqlBuilder, queryWrapper, queryTables, OperateType.UPDATE);
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

        return sqlBuilder.toString();
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
        String table = getRealTable(tableName, OperateType.SELECT);
        StringBuilder sql = new StringBuilder(SELECT_ALL_FROM);
        if (StringUtil.isNotBlank(schema)) {
            sql.append(wrap(getRealSchema(schema, table, OperateType.SELECT))).append(REFERENCE);
        }
        sql.append(wrap(table)).append(WHERE);
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(primaryKeys[i])).append(EQUALS_PLACEHOLDER);
        }
        prepareAuth(schema, table, sql, OperateType.SELECT);
        return sql.toString();
    }

    @Override
    public String forSelectByQuery(QueryWrapper queryWrapper) {
        prepareAuth(queryWrapper, OperateType.SELECT);
        return buildSelectSql(queryWrapper);
    }


    ////////////build query sql///////
    @Override
    public String buildSelectSql(QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);

        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

        List<QueryColumn> selectColumns = CPI.getSelectColumns(queryWrapper);

        // 多个表，非 SELECT * 时，需要处理重名字段
        if (allTables.size() > 1 && selectColumns != null && selectColumns.size() > 1) {
            IntStream.range(0, selectColumns.size())
                .boxed()
                // 生成 索引-字段值 对应关系
                .collect(Collectors.toMap(Function.identity(), selectColumns::get))
                .entrySet()
                .stream()
                // 需要处理别名的情况
                .filter(e -> StringUtil.isNotBlank(e.getValue().getName()))
                .filter(e -> StringUtil.isBlank(e.getValue().getAlias()))
                .filter(e -> !"*".equals(e.getValue().getName()))
                // 将相同字段对象放在一个集合里
                .collect(Collectors.groupingBy(e -> e.getValue().getName(),
                    Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)))
                .values()
                .stream()
                // 过滤出来重名的字段
                .filter(e -> e.size() > 1)
                // 合并所有需要加别名的字段
                .flatMap(Collection::stream)
                // 过滤出来可以添加别名的列
                .filter(e -> e.getValue().getTable() != null)
                .filter(e -> StringUtil.isNotBlank(e.getValue().getTable().getName()))
                // 添加别名并放回原集合索引位置
                .forEach(e -> selectColumns.set(e.getKey(),
                    e.getValue().as(e.getValue().getTable().getName() + "$" + e.getValue().getName())));
        }

        StringBuilder sqlBuilder = new StringBuilder();
        With with = CPI.getWith(queryWrapper);
        if (with != null) {
            sqlBuilder.append(with.toSql(this));
        }

        buildSelectColumnSql(sqlBuilder, allTables, selectColumns, CPI.getHint(queryWrapper));


        sqlBuilder.append(FROM).append(StringUtil.join(DELIMITER, queryTables, queryTable -> queryTable.toSql(this, OperateType.SELECT)));

        buildJoinSql(sqlBuilder, queryWrapper, allTables, OperateType.SELECT);
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
    public String buildNoSelectSql(QueryWrapper queryWrapper) {
        StringBuilder sqlBuilder = new StringBuilder();

        buildJoinSql(sqlBuilder, queryWrapper, Collections.EMPTY_LIST, OperateType.SELECT);
        buildWhereSql(sqlBuilder, queryWrapper, Collections.EMPTY_LIST, true);
        buildGroupBySql(sqlBuilder, queryWrapper, Collections.EMPTY_LIST);
        buildHavingSql(sqlBuilder, queryWrapper, Collections.EMPTY_LIST);
        buildOrderBySql(sqlBuilder, queryWrapper, Collections.EMPTY_LIST);

        List<UnionWrapper> unions = CPI.getUnions(queryWrapper);
        if (CollectionUtil.isNotEmpty(unions)) {
            if (sqlBuilder.length() > 0) {
                sqlBuilder.insert(0, BRACKET_LEFT).append(BRACKET_RIGHT);
            }
            for (UnionWrapper unionWrapper : unions) {
                unionWrapper.buildSql(sqlBuilder, this);
            }
        }

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

    private void buildSelectColumnSql(StringBuilder sqlBuilder, List<QueryTable> queryTables, List<QueryColumn> selectColumns, String hint) {
        sqlBuilder.append(SELECT);
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
    }


    @Override
    public String buildDeleteSql(QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

        // ignore selectColumns
        StringBuilder sqlBuilder = new StringBuilder(DELETE);
        String hint = CPI.getHint(queryWrapper);
        if (StringUtil.isNotBlank(hint)) {
            sqlBuilder.append(BLANK).append(hint).deleteCharAt(sqlBuilder.length() - 1);
        }

        // delete with join
        if (joinTables != null && !joinTables.isEmpty()) {
            if (queryTables == null || queryTables.isEmpty()) {
                throw new IllegalArgumentException("Delete with join sql must designate the from table.");
            } else if (queryTables.size() != 1) {
                throw new IllegalArgumentException("Delete with join sql must has 1 table only. but current has " + queryTables.size());
            }
            QueryTable queryTable = queryTables.get(0);
            String table = getRealTable(queryTable.getName(), OperateType.DELETE);
            if (StringUtil.isNotBlank(queryTable.getSchema())) {
                sqlBuilder.append(wrap(getRealSchema(queryTable.getSchema(), table, OperateType.DELETE))).append(REFERENCE);
            }
            sqlBuilder.append(BLANK).append(wrap(getRealTable(table, OperateType.DELETE)));
        }


        sqlBuilder.append(FROM).append(StringUtil.join(DELIMITER, queryTables, queryTable -> queryTable.toSql(this, OperateType.DELETE)));

        buildJoinSql(sqlBuilder, queryWrapper, allTables, OperateType.DELETE);
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
    public String buildWhereConditionSql(QueryWrapper queryWrapper) {
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        return whereQueryCondition != null ? whereQueryCondition.toSql(CPI.getQueryTables(queryWrapper), this) : EMPTY;
    }


    @Override
    public String forInsertEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls) {
        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO).append(tableInfo.getWrapSchemaAndTableName(this, OperateType.INSERT));

        String[] insertColumns = tableInfo.obtainInsertColumns(entity, ignoreNulls);
        Map<String, String> onInsertColumns = tableInfo.getOnInsertColumns();

        Map<String, RawValue> rawValueMap = tableInfo.obtainUpdateRawValueMap(entity);

        StringJoiner sqlFields = new StringJoiner(DELIMITER);
        StringJoiner sqlValues = new StringJoiner(DELIMITER);

        for (String insertColumn : insertColumns) {
            sqlFields.add(wrap(insertColumn));
            if (rawValueMap.containsKey(insertColumn)) {
                sqlValues.add(rawValueMap.remove(insertColumn).toSql(this));
            } else if (onInsertColumns != null && onInsertColumns.containsKey(insertColumn)) {
                sqlValues.add(onInsertColumns.get(insertColumn));
            } else {
                sqlValues.add(PLACEHOLDER);
            }
        }

        rawValueMap.forEach((k, v) -> {
            sqlFields.add(wrap(k));
            sqlValues.add(v.toSql(this));
        });

        return sql.append(BRACKET_LEFT).append(sqlFields).append(BRACKET_RIGHT)
            .append(VALUES)
            .append(BRACKET_LEFT).append(sqlValues).append(BRACKET_RIGHT)
            .toString();
    }


    @Override
    public String forInsertEntityWithPk(TableInfo tableInfo, Object entity, boolean ignoreNulls) {

        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO).append(tableInfo.getWrapSchemaAndTableName(this, OperateType.INSERT));

        String[] insertColumns = tableInfo.obtainInsertColumnsWithPk(entity, ignoreNulls);
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
        sql.append(INSERT_INTO).append(tableInfo.getWrapSchemaAndTableName(this, OperateType.INSERT));
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
                    // 直接读取 onInsert 配置的值，而不用 "?" 代替
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
        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();

        // 正常删除
        if (StringUtil.isBlank(logicDeleteColumn)) {
            String deleteByIdSql = forDeleteById(tableInfo.getSchema(), tableInfo.getTableName(), tableInfo.getPrimaryColumns());
            return tableInfo.buildTenantCondition(deleteByIdSql, tenantIdArgs, this);
        }

        // 逻辑删除
        StringBuilder sql = new StringBuilder();
        String[] primaryKeys = tableInfo.getPrimaryColumns();

        sql.append(UPDATE).append(tableInfo.getWrapSchemaAndTableName(this, OperateType.UPDATE));
        sql.append(SET).append(buildLogicDeletedSet(logicDeleteColumn, tableInfo));
        sql.append(WHERE);
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(primaryKeys[i])).append(EQUALS_PLACEHOLDER);
        }

        sql.append(AND).append(buildLogicNormalCondition(logicDeleteColumn, tableInfo));

        // 租户ID
        tableInfo.buildTenantCondition(sql, tenantIdArgs, this);
        prepareAuth(tableInfo, sql, OperateType.DELETE);
        return sql.toString();
    }


    @Override
    public String forDeleteEntityBatchByIds(TableInfo tableInfo, Object[] primaryValues) {
        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();

        // 正常删除
        if (StringUtil.isBlank(logicDeleteColumn)) {
            String deleteSQL = forDeleteBatchByIds(tableInfo.getSchema(), tableInfo.getTableName(), tableInfo.getPrimaryColumns(), primaryValues);

            // 多租户
            if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
                deleteSQL = deleteSQL.replace(WHERE, WHERE + BRACKET_LEFT) + BRACKET_RIGHT;
                deleteSQL = tableInfo.buildTenantCondition(deleteSQL, tenantIdArgs, this);
            }
            return deleteSQL;
        }

        StringBuilder sql = new StringBuilder();
        sql.append(UPDATE);
        sql.append(tableInfo.getWrapSchemaAndTableName(this, OperateType.UPDATE));
        sql.append(SET).append(buildLogicDeletedSet(logicDeleteColumn, tableInfo));
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
            for (int i = 0; i < primaryValues.length; i++) {
                if (i > 0) {
                    sql.append(OR);
                }
                sql.append(wrap(primaryKeys[0])).append(EQUALS_PLACEHOLDER);
            }
        }

        sql.append(BRACKET_RIGHT).append(AND).append(buildLogicNormalCondition(logicDeleteColumn, tableInfo));

        tableInfo.buildTenantCondition(sql, tenantIdArgs, this);
        prepareAuth(tableInfo, sql, OperateType.DELETE);
        return sql.toString();
    }

    @Override
    public String forDeleteEntityBatchByQuery(TableInfo tableInfo, QueryWrapper queryWrapper) {

        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();

        // 正常删除
        if (StringUtil.isBlank(logicDeleteColumn)) {
            return forDeleteByQuery(queryWrapper);
        }


        prepareAuth(queryWrapper, OperateType.DELETE);
        // 逻辑删除
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

        // ignore selectColumns
        StringBuilder sqlBuilder = new StringBuilder(UPDATE).append(forHint(CPI.getHint(queryWrapper)));
        sqlBuilder.append(tableInfo.getWrapSchemaAndTableName(this, OperateType.DELETE));
        sqlBuilder.append(SET).append(buildLogicDeletedSet(logicDeleteColumn, tableInfo));


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
        StringBuilder sql = new StringBuilder();

        Set<String> updateColumns = tableInfo.obtainUpdateColumns(entity, ignoreNulls, false);
        Map<String, RawValue> rawValueMap = tableInfo.obtainUpdateRawValueMap(entity);
        String[] primaryKeys = tableInfo.getPrimaryColumns();

        sql.append(UPDATE).append(tableInfo.getWrapSchemaAndTableName(this, OperateType.UPDATE)).append(SET);

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

        // 逻辑删除条件，已删除的数据不能被修改
        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();
        if (StringUtil.isNotBlank(logicDeleteColumn)) {
            sql.append(AND).append(buildLogicNormalCondition(logicDeleteColumn, tableInfo));
        }


        // 租户ID字段
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();
        tableInfo.buildTenantCondition(sql, tenantIdArgs, this);

        // 乐观锁条件
        if (StringUtil.isNotBlank(versionColumn)) {
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
        prepareAuth(queryWrapper, OperateType.UPDATE);
        StringBuilder sqlBuilder = new StringBuilder();

        Set<String> updateColumns = tableInfo.obtainUpdateColumns(entity, ignoreNulls, true);
        Map<String, RawValue> rawValueMap = tableInfo.obtainUpdateRawValueMap(entity);

        sqlBuilder.append(UPDATE).append(forHint(CPI.getHint(queryWrapper)));
        sqlBuilder.append(tableInfo.getWrapSchemaAndTableName(this, OperateType.UPDATE));

        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        buildJoinSql(sqlBuilder, queryWrapper, queryTables, OperateType.UPDATE);


        sqlBuilder.append(SET);

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
        if (StringUtil.isNotBlank(versionColumn)) {
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


    @Override
    public String forSelectOneEntityById(TableInfo tableInfo) {
        StringBuilder sql = new StringBuilder();
        buildSelectColumnSql(sql, null, null, null);
        sql.append(FROM).append(tableInfo.getWrapSchemaAndTableName(this, OperateType.SELECT));
        sql.append(WHERE);
        String[] pKeys = tableInfo.getPrimaryColumns();
        for (int i = 0; i < pKeys.length; i++) {
            if (i > 0) {
                sql.append(AND);
            }
            sql.append(wrap(pKeys[i])).append(EQUALS_PLACEHOLDER);
        }

        // 逻辑删除的情况下，需要添加逻辑删除的条件
        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();
        if (StringUtil.isNotBlank(logicDeleteColumn)) {
            sql.append(AND).append(buildLogicNormalCondition(logicDeleteColumn, tableInfo));
        }

        // 多租户
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();
        tableInfo.buildTenantCondition(sql, tenantIdArgs, this);
        prepareAuth(tableInfo, sql, OperateType.SELECT);
        return sql.toString();
    }


    @Override
    public String forSelectEntityListByIds(TableInfo tableInfo, Object[] primaryValues) {
        StringBuilder sql = new StringBuilder();
        buildSelectColumnSql(sql, null, tableInfo.getDefaultQueryColumn(), null);
        sql.append(FROM).append(tableInfo.getWrapSchemaAndTableName(this, OperateType.SELECT));
        sql.append(WHERE);
        String[] primaryKeys = tableInfo.getPrimaryColumns();

        String logicDeleteColumn = tableInfo.getLogicDeleteColumnOrSkip();
        Object[] tenantIdArgs = tableInfo.buildTenantIdArgs();
        if (StringUtil.isNotBlank(logicDeleteColumn) || ArrayUtil.isNotEmpty(tenantIdArgs)) {
            sql.append(BRACKET_LEFT);
        }

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
            sql.append(AND).append(buildLogicNormalCondition(logicDeleteColumn, tableInfo));
        }

        // 多租户
        tableInfo.buildTenantCondition(sql, tenantIdArgs, this);
        prepareAuth(tableInfo, sql, OperateType.SELECT);
        return sql.toString();
    }


    protected boolean buildJoinSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables, OperateType operateType) {
        List<Join> joins = CPI.getJoins(queryWrapper);
        boolean joinSuccess = false;
        if (joins != null && !joins.isEmpty()) {
            for (Join join : joins) {
                if (!join.checkEffective()) {
                    continue;
                }
                sqlBuilder.append(join.toSql(queryTables, this, operateType));
                joinSuccess = true;
            }
        }
        return joinSuccess;
    }


    protected void buildWhereSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables, boolean allowNoCondition) {
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        if (whereQueryCondition != null) {
            String whereSql = whereQueryCondition.toSql(queryTables, this);
            if (StringUtil.isNotBlank(whereSql)) {
                sqlBuilder.append(WHERE).append(whereSql);
            } else if (!allowNoCondition) {
                throw FlexExceptions.wrap(LocalizedFormats.UPDATE_OR_DELETE_NOT_ALLOW);
            }
        } else {
            // whereQueryCondition == null
            if (!allowNoCondition) {
                throw FlexExceptions.wrap(LocalizedFormats.UPDATE_OR_DELETE_NOT_ALLOW);
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
    protected StringBuilder buildLimitOffsetSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, Long limitRows, Long limitOffset) {
        return limitOffsetProcessor.process(this, sqlBuilder, queryWrapper, limitRows, limitOffset);
    }


    protected String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo) {
        return LogicDeleteManager.getProcessor().buildLogicNormalCondition(logicColumn, tableInfo, this);
    }


    protected String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo) {
        return LogicDeleteManager.getProcessor().buildLogicDeletedSet(logicColumn, tableInfo, this);
    }


}
