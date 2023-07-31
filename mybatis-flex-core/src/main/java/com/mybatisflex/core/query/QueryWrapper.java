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
package com.mybatisflex.core.query;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.table.TableDef;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.*;

import java.util.*;
import java.util.function.Consumer;

public class QueryWrapper extends BaseQueryWrapper<QueryWrapper> {

    public static QueryWrapper create() {
        return new QueryWrapper();
    }

    /**
     * 根据实体类对象，构建查询条件
     *
     * @param entity 实体类对象
     * @return 查询对象
     */
    public static QueryWrapper create(Object entity) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(ClassUtil.getUsefulClass(entity.getClass()));
        return tableInfo.buildQueryWrapper(entity);
    }

    @SuppressWarnings("unchecked")
    public <Q extends QueryWrapper> WithBuilder<Q> with(String name) {
        if (with == null) {
            with = new With();
        }
        return new WithBuilder<>((Q) this, with, name);
    }

    @SuppressWarnings("unchecked")
    public <Q extends QueryWrapper> WithBuilder<Q> with(String name, String... params) {
        if (with == null) {
            with = new With();
        }
        return new WithBuilder<>((Q) this, with, name, Arrays.asList(params));
    }

    @SuppressWarnings("unchecked")
    public <Q extends QueryWrapper> WithBuilder<Q> withRecursive(String name) {
        if (with == null) {
            with = new With(true);
        }
        return new WithBuilder<>((Q) this, with, name);
    }

    @SuppressWarnings("unchecked")
    public <Q extends QueryWrapper> WithBuilder<Q> withRecursive(String name, String... params) {
        if (with == null) {
            with = new With(true);
        }
        return new WithBuilder<>((Q) this, with, name, Arrays.asList(params));
    }

    public QueryWrapper select() {
        return this;
    }

    public QueryWrapper select(String... columns) {
        for (String column : columns) {
            addSelectColumn(new StringQueryColumn(column));
        }
        return this;
    }

    public <T> QueryWrapper select(LambdaGetter<T>... lambdaGetters) {
        for (LambdaGetter<T> lambdaGetter : lambdaGetters) {
            QueryColumn queryColumn = LambdaUtil.getQueryColumn(lambdaGetter);
            addSelectColumn(queryColumn);
        }
        return this;
    }

    public QueryWrapper select(QueryColumn... queryColumns) {
        for (QueryColumn column : queryColumns) {
            if (column != null) {
                addSelectColumn(column);
            }
        }
        return this;
    }

    public QueryWrapper select(QueryColumn[]... queryColumns) {
        for (QueryColumn[] columnArray : queryColumns) {
            if (columnArray != null) {
                for (QueryColumn column : columnArray) {
                    if (column != null) {
                        addSelectColumn(column);
                    }
                }
            }
        }
        return this;
    }

    public QueryWrapper select(QueryColumn[] queryColumns, QueryColumn... queryColumns2) {
        for (QueryColumn column : queryColumns) {
            if (column != null) {
                addSelectColumn(column);
            }
        }
        for (QueryColumn column : queryColumns2) {
            if (column != null) {
                addSelectColumn(column);
            }
        }
        return this;
    }

    public QueryWrapper from(TableDef... tableDefs) {
        for (TableDef tableDef : tableDefs) {
            from(new QueryTable(tableDef));
        }
        return this;
    }

    public QueryWrapper from(Class<?>... entityClasses) {
        for (Class<?> entityClass : entityClasses) {
            TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
            from(new QueryTable(tableInfo.getSchema(), tableInfo.getTableName()));
        }
        return this;
    }

    public QueryWrapper from(String... tables) {
        for (String table : tables) {
            if (StringUtil.isBlank(table)) {
                throw new IllegalArgumentException("table must not be null or blank.");
            }
            from(new QueryTable(table));
        }
        return this;
    }

    public QueryWrapper from(QueryTable... tables) {
        if (CollectionUtil.isEmpty(queryTables)) {
            queryTables = new ArrayList<>();
            queryTables.addAll(Arrays.asList(tables));
        } else {
            for (QueryTable table : tables) {
                boolean contains = false;
                for (QueryTable queryTable : queryTables) {
                    if (queryTable.isSameTable(table)) {
                        contains = true;
                    }
                }
                if (!contains) {
                    queryTables.add(table);
                }
            }
        }
        return this;
    }

    public QueryWrapper from(QueryWrapper queryWrapper) {
        return from(new SelectQueryTable(queryWrapper));
    }

    public QueryWrapper as(String alias) {
        if (CollectionUtil.isEmpty(queryTables)) {
            throw new IllegalArgumentException("query table must not be empty.");
        }
        queryTables.get(queryTables.size() - 1).alias = alias;
        return this;
    }

    public QueryWrapper where(QueryCondition queryCondition) {
        this.setWhereQueryCondition(queryCondition);
        return this;
    }

    public QueryWrapper where(String sql) {
        this.setWhereQueryCondition(new RawFragment(sql));
        return this;
    }

    public QueryWrapper where(String sql, Object... params) {
        this.setWhereQueryCondition(new RawFragment(sql, params));
        return this;
    }

    public QueryWrapper where(Map<String, Object> whereConditions) {
        return and(whereConditions);
    }

    public QueryWrapper where(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        return and(whereConditions, operators);
    }

    public <T> QueryConditionBuilder<? extends QueryWrapper> where(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>(this, LambdaUtil.getQueryColumn(fn), SqlConnector.AND);
    }

    public QueryWrapper where(Consumer<QueryWrapper> consumer) {
        return and(consumer);
    }

    public QueryWrapper and(QueryCondition queryCondition) {
        return addWhereQueryCondition(queryCondition, SqlConnector.AND);
    }

    public QueryWrapper and(String sql) {
        this.addWhereQueryCondition(new RawFragment(sql), SqlConnector.AND);
        return this;
    }

    public QueryWrapper and(String sql, Object... params) {
        this.addWhereQueryCondition(new RawFragment(sql, params), SqlConnector.AND);
        return this;
    }

    public <T> QueryConditionBuilder<? extends QueryWrapper> and(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>(this, LambdaUtil.getQueryColumn(fn), SqlConnector.AND);
    }

    public QueryWrapper and(Consumer<QueryWrapper> consumer) {
        return and(consumer, true);
    }

    public QueryWrapper and(Consumer<QueryWrapper> consumer, boolean condition) {
        if (!condition) {
            return this;
        }
        QueryWrapper newWrapper = new QueryWrapper();
        consumer.accept(newWrapper);
        QueryCondition whereQueryCondition = newWrapper.whereQueryCondition;
        if (whereQueryCondition != null) {
            and(new Brackets(whereQueryCondition));
        }
        return this;
    }


    public QueryWrapper and(Map<String, Object> whereConditions) {
        return and(whereConditions, Collections.emptyMap());
    }

    public QueryWrapper and(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        return connectMap(whereConditions, operators, SqlConnector.AND, SqlConnector.AND);
    }

    public QueryWrapper and(Map<String, Object> whereConditions, Map<String, SqlOperator> operators, SqlConnector innerConnector) {
        return connectMap(whereConditions, operators, SqlConnector.AND, innerConnector);
    }


    public QueryWrapper or(QueryCondition queryCondition) {
        return addWhereQueryCondition(queryCondition, SqlConnector.OR);
    }

    public QueryWrapper or(String sql) {
        this.addWhereQueryCondition(new RawFragment(sql), SqlConnector.OR);
        return this;
    }

    public QueryWrapper or(String sql, Object... params) {
        this.addWhereQueryCondition(new RawFragment(sql, params), SqlConnector.OR);
        return this;
    }

    public <T> QueryConditionBuilder<? extends QueryWrapper> or(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>(this, LambdaUtil.getQueryColumn(fn), SqlConnector.OR);
    }

    public QueryWrapper or(Consumer<QueryWrapper> consumer) {
        return or(consumer, true);
    }

    public QueryWrapper or(Consumer<QueryWrapper> consumer, boolean condition) {
        if (condition) {
            return this;
        }
        QueryWrapper newWrapper = new QueryWrapper();
        consumer.accept(newWrapper);
        QueryCondition whereQueryCondition = newWrapper.whereQueryCondition;
        if (whereQueryCondition != null) {
            or(new Brackets(whereQueryCondition));
        }
        return this;
    }


    public QueryWrapper or(Map<String, Object> whereConditions) {
        return or(whereConditions, Collections.emptyMap());
    }

    public QueryWrapper or(Map<String, Object> whereConditions, Map<String, SqlOperator> operators) {
        return connectMap(whereConditions, operators, SqlConnector.OR, SqlConnector.AND);
    }

    public QueryWrapper or(Map<String, Object> whereConditions, Map<String, SqlOperator> operators, SqlConnector innerConnector) {
        return connectMap(whereConditions, operators, SqlConnector.OR, SqlConnector.AND);
    }

    protected QueryWrapper connectMap(Map<String, Object> mapConditions, Map<String, SqlOperator> operators, SqlConnector outerConnector, SqlConnector innerConnector) {
        if (operators == null) {
            operators = Collections.emptyMap();
        }
        if (mapConditions != null) {
            QueryCondition condition = null;
            for (Map.Entry<String, Object> entry : mapConditions.entrySet()) {
                SqlOperator operator = operators.get(entry.getKey());
                if (operator == null) {
                    operator = SqlOperator.EQUALS;
                }
                QueryCondition cond = QueryCondition.create(new QueryColumn(entry.getKey()), operator.getValue(), entry.getValue());
                if (condition == null) {
                    condition = cond;
                } else {
                    if (innerConnector == SqlConnector.AND) {
                        condition.and(cond);
                    } else {
                        condition.or(cond);
                    }
                }
            }
            addWhereQueryCondition(condition, outerConnector);
        }
        return this;
    }


    public <Q extends QueryWrapper> Joiner<Q> leftJoin(String table) {
        return joining(SqlConsts.LEFT_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(String table, boolean when) {
        return joining(SqlConsts.LEFT_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(Class<?> entityClass) {
        return joining(SqlConsts.LEFT_JOIN, entityClass, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(Class<?> entityClass, boolean when) {
        return joining(SqlConsts.LEFT_JOIN, entityClass, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(TableDef table) {
        return joining(SqlConsts.LEFT_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(TableDef table, boolean when) {
        return joining(SqlConsts.LEFT_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(QueryWrapper table) {
        return joining(SqlConsts.LEFT_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.LEFT_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(String table) {
        return joining(SqlConsts.RIGHT_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(String table, boolean when) {
        return joining(SqlConsts.RIGHT_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(Class<?> entityClass) {
        return joining(SqlConsts.RIGHT_JOIN, entityClass, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(Class<?> entityClass, boolean when) {
        return joining(SqlConsts.RIGHT_JOIN, entityClass, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(TableDef table) {
        return joining(SqlConsts.RIGHT_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(TableDef table, boolean when) {
        return joining(SqlConsts.RIGHT_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(QueryWrapper table) {
        return joining(SqlConsts.RIGHT_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.RIGHT_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(String table) {
        return joining(SqlConsts.INNER_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(String table, boolean when) {
        return joining(SqlConsts.INNER_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(Class<?> entityClass) {
        return joining(SqlConsts.INNER_JOIN, entityClass, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(Class<?> entityClass, boolean when) {
        return joining(SqlConsts.INNER_JOIN, entityClass, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(TableDef table) {
        return innerJoin(table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(TableDef table, boolean when) {
        return joining(SqlConsts.INNER_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(QueryWrapper table) {
        return joining(SqlConsts.INNER_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.INNER_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(String table) {
        return joining(SqlConsts.FULL_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(String table, boolean when) {
        return joining(SqlConsts.FULL_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(Class<?> entityClass) {
        return joining(SqlConsts.FULL_JOIN, entityClass, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(Class<?> entityClass, boolean when) {
        return joining(SqlConsts.FULL_JOIN, entityClass, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(TableDef table) {
        return joining(SqlConsts.FULL_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(TableDef table, boolean when) {
        return joining(SqlConsts.FULL_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(QueryWrapper table) {
        return joining(SqlConsts.FULL_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.FULL_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(String table) {
        return joining(SqlConsts.CROSS_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(String table, boolean when) {
        return joining(SqlConsts.CROSS_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(Class<?> entityClass) {
        return joining(SqlConsts.CROSS_JOIN, entityClass, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(Class<?> entityClass, boolean when) {
        return joining(SqlConsts.CROSS_JOIN, entityClass, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(TableDef table) {
        return joining(SqlConsts.CROSS_JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(TableDef table, boolean when) {
        return joining(SqlConsts.CROSS_JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(QueryWrapper table) {
        return joining(SqlConsts.CROSS_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.CROSS_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(String table) {
        return joining(SqlConsts.JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(String table, boolean when) {
        return joining(SqlConsts.JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(Class<?> entityClass) {
        return joining(SqlConsts.JOIN, entityClass, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(Class<?> entityClass, boolean when) {
        return joining(SqlConsts.JOIN, entityClass, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(TableDef table) {
        return joining(SqlConsts.JOIN, new QueryTable(table), true);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(TableDef table, boolean when) {
        return joining(SqlConsts.JOIN, new QueryTable(table), when);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(QueryWrapper table) {
        return joining(SqlConsts.JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(QueryWrapper table, boolean when) {
        return joining(SqlConsts.JOIN, table, when);
    }

    public QueryWrapper union(QueryWrapper unionQuery) {
        if (unions == null) {
            unions = new ArrayList<>();
        }
        unions.add(UnionWrapper.union(unionQuery));
        return this;
    }

    public QueryWrapper unionAll(QueryWrapper unionQuery) {
        if (unions == null) {
            unions = new ArrayList<>();
        }
        unions.add(UnionWrapper.unionAll(unionQuery));
        return this;
    }

    public QueryWrapper forUpdate() {
        addEndFragment("FOR UPDATE");
        return this;
    }

    public QueryWrapper forUpdateNoWait() {
        addEndFragment("FOR UPDATE NOWAIT");
        return this;
    }

    //    public QueryWrapper end(String sqlPart){
    //        addEndFragment(sqlPart);
    //        return this;
    //    }

    @SuppressWarnings("unchecked")
    protected <T extends QueryWrapper> Joiner<T> joining(String type, QueryTable table, boolean when) {
        Join join = new Join(type, table, when);
        addJoinTable(join.getQueryTable());
        return new Joiner<>((T) addJoin(join), join);
    }

    protected <T extends QueryWrapper> Joiner<T> joining(String type, Class<?> entityClass, boolean when) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName());
        return joining(type, queryTable, when);
    }

    @SuppressWarnings("unchecked")
    protected <T extends QueryWrapper> Joiner<T> joining(String type, QueryWrapper queryWrapper, boolean when) {
        Join join = new Join(type, queryWrapper, when);
        addJoinTable(join.getQueryTable());
        return new Joiner<>((T) addJoin(join), join);
    }

    public QueryWrapper groupBy(String name) {
        addGroupByColumns(new QueryColumn(name));
        return this;
    }

    public QueryWrapper groupBy(String... names) {
        for (String name : names) {
            groupBy(name);
        }
        return this;
    }

    public QueryWrapper groupBy(QueryColumn column) {
        addGroupByColumns(column);
        return this;
    }

    public QueryWrapper groupBy(QueryColumn... columns) {
        for (QueryColumn column : columns) {
            groupBy(column);
        }
        return this;
    }

    public <T> QueryWrapper groupBy(LambdaGetter<T> column) {
        addGroupByColumns(LambdaUtil.getQueryColumn(column));
        return this;
    }

    public <T> QueryWrapper groupBy(LambdaGetter<T>... columns) {
        for (LambdaGetter<T> column : columns) {
            groupBy(LambdaUtil.getQueryColumn(column));
        }
        return this;
    }

    public QueryWrapper having(QueryCondition queryCondition) {
        addHavingQueryCondition(queryCondition, SqlConnector.AND);
        return this;
    }

    public QueryWrapper orderBy(QueryOrderBy... orderBys) {
        for (QueryOrderBy queryOrderBy : orderBys) {
            addOrderBy(queryOrderBy);
        }
        return this;
    }


    public <T> QueryOrderByBuilder<? extends QueryWrapper> orderBy(LambdaGetter<T> getter) {
        return new QueryOrderByBuilder<>(this, getter);
    }


    public QueryWrapper orderBy(String... orderBys) {
        if (orderBys == null || orderBys.length == 0) {
            //ignore
            return this;
        }
        for (String queryOrderBy : orderBys) {
            if (StringUtil.isNotBlank(queryOrderBy)) {
                addOrderBy(new StringQueryOrderBy(queryOrderBy));
            }
        }
        return this;
    }

    public QueryWrapper limit(Number rows) {
        if (rows != null) {
            setLimitRows(rows.longValue());
        }else {
            setLimitRows(null);
        }
        return this;
    }

    public QueryWrapper offset(Number offset) {
        if (offset!= null) {
            setLimitOffset(offset.longValue());
        }else {
            setLimitOffset(null);
        }
        return this;
    }

    public QueryWrapper limit(Number offset, Number rows) {
        offset(offset);
        limit(rows);
        return this;
    }

    public QueryWrapper datasource(String datasource) {
        setDataSource(datasource);
        return this;
    }

    public QueryWrapper hint(String hint) {
        setHint(hint);
        return this;
    }

    /**
     * 获取 queryWrapper 的参数
     * 在构建 sql 的时候，需要保证 where 在 having 的前面
     */
    Object[] getValueArray() {

        List<Object> withValues = null;
        if (with != null) {
            Object[] paramValues = with.getParamValues();
            if (ArrayUtil.isNotEmpty(paramValues)) {
                withValues = new ArrayList<>(Arrays.asList(paramValues));

            }
        }

        List<Object> columnValues = null;
        List<QueryColumn> selectColumns = getSelectColumns();
        if (CollectionUtil.isNotEmpty(selectColumns)) {
            for (QueryColumn selectColumn : selectColumns) {
                if (selectColumn instanceof HasParamsColumn) {
                    Object[] paramValues = ((HasParamsColumn) selectColumn).getParamValues();
                    if (ArrayUtil.isNotEmpty(paramValues)) {
                        if (columnValues == null) {
                            columnValues = new ArrayList<>(paramValues.length);
                        }
                        columnValues.addAll(Arrays.asList(paramValues));
                    }
                }
            }
        }

        //select 子查询的参数：select * from (select ....)
        List<Object> tableValues = null;
        List<QueryTable> queryTables = getQueryTables();
        if (CollectionUtil.isNotEmpty(queryTables)) {
            for (QueryTable queryTable : queryTables) {
                Object[] tableValueArray = queryTable.getValueArray();
                if (tableValueArray.length > 0) {
                    if (tableValues == null) {
                        tableValues = new ArrayList<>(tableValueArray.length);
                    }
                    tableValues.addAll(Arrays.asList(tableValueArray));
                }
            }
        }

        //join 子查询的参数：left join (select ...)
        List<Object> joinValues = null;
        List<Join> joins = getJoins();
        if (CollectionUtil.isNotEmpty(joins)) {
            for (Join join : joins) {
                QueryTable joinTable = join.getQueryTable();
                Object[] valueArray = joinTable.getValueArray();
                if (valueArray.length > 0) {
                    if (joinValues == null) {
                        joinValues = new ArrayList<>(valueArray.length);
                    }
                    joinValues.addAll(Arrays.asList(valueArray));
                }
                QueryCondition onCondition = join.getOnCondition();
                Object[] values = WrapperUtil.getValues(onCondition);
                if (values.length > 0) {
                    if (joinValues == null) {
                        joinValues = new ArrayList<>(values.length);
                    }
                    joinValues.addAll(Arrays.asList(values));
                }
            }
        }

        //where 参数
        Object[] whereValues = WrapperUtil.getValues(whereQueryCondition);

        //having 参数
        Object[] havingValues = WrapperUtil.getValues(havingQueryCondition);

        Object[] paramValues = ArrayUtil.concat(whereValues, havingValues);

        //unions 参数
        if (CollectionUtil.isNotEmpty(unions)) {
            for (UnionWrapper union : unions) {
                QueryWrapper queryWrapper = union.getQueryWrapper();
                paramValues = ArrayUtil.concat(paramValues, queryWrapper.getValueArray());
            }
        }

        Object[] returnValues = withValues == null ? FlexConsts.EMPTY_ARRAY : withValues.toArray();
        returnValues = columnValues != null ? ArrayUtil.concat(returnValues, columnValues.toArray()) : returnValues;
        returnValues = tableValues != null ? ArrayUtil.concat(returnValues, tableValues.toArray()) : returnValues;
        returnValues = joinValues != null ? ArrayUtil.concat(returnValues, joinValues.toArray()) : returnValues;
        returnValues = ArrayUtil.concat(returnValues, paramValues);

        return returnValues;
    }

    List<QueryWrapper> getChildSelect() {

        List<QueryWrapper> tableChildQuery = null;
        List<QueryTable> queryTables = getQueryTables();
        if (CollectionUtil.isNotEmpty(queryTables)) {
            for (QueryTable queryTable : queryTables) {
                if (queryTable instanceof SelectQueryTable) {
                    if (tableChildQuery == null) {
                        tableChildQuery = new ArrayList<>();
                    }
                    tableChildQuery.add(((SelectQueryTable) queryTable).getQueryWrapper());
                }
            }
        }

        List<QueryWrapper> whereChildQuery = WrapperUtil.getChildQueryWrapper(whereQueryCondition);
        List<QueryWrapper> havingChildQuery = WrapperUtil.getChildQueryWrapper(havingQueryCondition);

        if (tableChildQuery == null && whereChildQuery.isEmpty() && havingChildQuery.isEmpty()) {
            return Collections.emptyList();
        }

        List<QueryWrapper> childQueryWrappers = tableChildQuery == null ? new ArrayList<>()
            : new ArrayList<>(tableChildQuery);
        childQueryWrappers.addAll(whereChildQuery);
        childQueryWrappers.addAll(havingChildQuery);

        return childQueryWrappers;
    }

    public String toSQL() {
        String sql = DialectFactory.getDialect().forSelectByQuery(this);
        return SqlUtil.replaceSqlParams(sql, getValueArray());
    }

    @Override
    public QueryWrapper clone() {
        return super.clone();
    }

}
