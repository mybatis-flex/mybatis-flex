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
import com.mybatisflex.core.constant.SqlConnector;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.*;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class QueryWrapper extends BaseQueryWrapper<QueryWrapper> {

    public static QueryWrapper create() {
        return new QueryWrapper();
    }

    /**
     * 根据实体类对象，构建查询条件
     *
     * @param entity 实体类对象
     * @return 查询对象 QueryWrapper
     */
    public static QueryWrapper create(Object entity) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(ClassUtil.getUsefulClass(entity.getClass()));
        return tableInfo.buildQueryWrapper(entity, null);
    }

    /**
     * 根据实体类构建查询条件
     *
     * @param entity    实体类对象
     * @param operators 每个属性对应的操作符
     * @return 查询对象 QueryWrapper
     */
    public static QueryWrapper create(Object entity, SqlOperators operators) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(ClassUtil.getUsefulClass(entity.getClass()));
        return tableInfo.buildQueryWrapper(entity, operators);
    }

    /**
     * 根据 Map 对象，构建查询条件
     *
     * @param map Map 对象
     * @return 查询对象 QueryWrapper
     */
    public static QueryWrapper create(Map map) {
        return create().where(map);
    }

    /**
     * 根据 Map 构建查询条件
     *
     * @param map       Map 对象
     * @param operators 每个属性对应的操作符
     * @return 查询对象 QueryWrapper
     */
    public static QueryWrapper create(Map map, SqlOperators operators) {
        return create().where(map, operators);
    }

    /**
     * <p>判断当前 {@link QueryWrapper} 是否包含 {@code WHERE} 查询条件。
     *
     * <p>需要判断的查询条件，只包括主动构建的查询条件，不包括追加的条件，例如：逻辑删除功能自动添加的
     * {@code is_delete = 0} 不会包含在检查条件内。
     *
     * @return {@code true} 包含条件，{@code false} 不包含条件。
     */
    public boolean hasCondition() {
        QueryCondition c;
        return (c = whereQueryCondition) != null && (c.checkEffective() || c.getNextEffectiveCondition() != null);
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
            addSelectColumn(new RawQueryColumn(column));
        }
        return this;
    }

    public <T> QueryWrapper select(LambdaGetter<T>... lambdaGetters) {
        for (LambdaGetter<?> lambdaGetter : lambdaGetters) {
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

    public QueryWrapper select(Iterable<QueryColumn> queryColumns) {
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
        int index = queryTables.size() - 1;
        queryTables.set(index, queryTables.get(index).as(alias));
        return this;
    }

    public QueryWrapper where(QueryCondition queryCondition) {
        this.addWhereQueryCondition(queryCondition);
        return this;
    }

    public QueryWrapper where(String sql) {
        this.addWhereQueryCondition(new RawQueryCondition(sql));
        return this;
    }

    public QueryWrapper where(String sql, Object... params) {
        this.addWhereQueryCondition(new RawQueryCondition(sql, params));
        return this;
    }

    public QueryWrapper where(Map<String, Object> whereConditions) {
        return and(whereConditions);
    }

    public QueryWrapper where(Map<String, Object> whereConditions, SqlOperators operators) {
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
        this.addWhereQueryCondition(new RawQueryCondition(sql), SqlConnector.AND);
        return this;
    }

    public QueryWrapper and(String sql, Object... params) {
        this.addWhereQueryCondition(new RawQueryCondition(sql, params), SqlConnector.AND);
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
        return and(whereConditions, SqlOperators.empty());
    }

    public QueryWrapper and(Map<String, Object> whereConditions, SqlOperators operators) {
        return connectMap(whereConditions, operators, SqlConnector.AND, SqlConnector.AND);
    }

    public QueryWrapper and(Map<String, Object> whereConditions, SqlOperators operators, SqlConnector innerConnector) {
        return connectMap(whereConditions, operators, SqlConnector.AND, innerConnector);
    }


    public QueryWrapper or(QueryCondition queryCondition) {
        return addWhereQueryCondition(queryCondition, SqlConnector.OR);
    }

    public QueryWrapper or(String sql) {
        this.addWhereQueryCondition(new RawQueryCondition(sql), SqlConnector.OR);
        return this;
    }

    public QueryWrapper or(String sql, Object... params) {
        this.addWhereQueryCondition(new RawQueryCondition(sql, params), SqlConnector.OR);
        return this;
    }

    public <T> QueryConditionBuilder<? extends QueryWrapper> or(LambdaGetter<T> fn) {
        return new QueryConditionBuilder<>(this, LambdaUtil.getQueryColumn(fn), SqlConnector.OR);
    }

    public QueryWrapper or(Consumer<QueryWrapper> consumer) {
        return or(consumer, true);
    }

    public QueryWrapper or(Consumer<QueryWrapper> consumer, boolean condition) {
        if (!condition) {
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
        return or(whereConditions, SqlOperators.empty());
    }

    public QueryWrapper or(Map<String, Object> whereConditions, SqlOperators operators) {
        return connectMap(whereConditions, operators, SqlConnector.OR, SqlConnector.AND);
    }

    public QueryWrapper or(Map<String, Object> whereConditions, SqlOperators operators, SqlConnector innerConnector) {
        return connectMap(whereConditions, operators, SqlConnector.OR, SqlConnector.AND);
    }

    protected QueryWrapper connectMap(Map<String, Object> mapConditions, SqlOperators operators, SqlConnector outerConnector, SqlConnector innerConnector) {
        if (operators == null) {
            operators = SqlOperators.empty();
        }
        if (mapConditions != null) {
            QueryCondition condition = null;
            for (Map.Entry<String, Object> entry : mapConditions.entrySet()) {
                SqlOperator operator = operators.get(entry.getKey());
                if (operator == null) {
                    operator = SqlOperator.EQUALS;
                } else if (operator == SqlOperator.IGNORE) {
                    continue;
                }
                Object value = entry.getValue();
                if (operator == SqlOperator.LIKE || operator == SqlOperator.NOT_LIKE) {
                    value = "%" + value + "%";
                } else if (operator == SqlOperator.LIKE_LEFT || operator == SqlOperator.NOT_LIKE_LEFT) {
                    value = value + "%";
                } else if (operator == SqlOperator.LIKE_RIGHT || operator == SqlOperator.NOT_LIKE_RIGHT) {
                    value = "%" + value;
                }
                QueryCondition cond = QueryCondition.create(new QueryColumn(entry.getKey()), operator, value);
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

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(QueryTable table) {
        return joining(SqlConsts.LEFT_JOIN, table, true);
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

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(QueryWrapper table) {
        return joining(SqlConsts.LEFT_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> leftJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.LEFT_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(QueryTable table) {
        return joining(SqlConsts.RIGHT_JOIN, table, true);
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

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(QueryWrapper table) {
        return joining(SqlConsts.RIGHT_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> rightJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.RIGHT_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(QueryTable table) {
        return joining(SqlConsts.INNER_JOIN, table, true);
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

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(QueryWrapper table) {
        return joining(SqlConsts.INNER_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> innerJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.INNER_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(QueryTable table) {
        return joining(SqlConsts.FULL_JOIN, table, true);
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

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(QueryWrapper table) {
        return joining(SqlConsts.FULL_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> fullJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.FULL_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(QueryTable table) {
        return joining(SqlConsts.CROSS_JOIN, table, true);
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

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(QueryWrapper table) {
        return joining(SqlConsts.CROSS_JOIN, table, true);
    }

    public <Q extends QueryWrapper> Joiner<Q> crossJoin(QueryWrapper table, boolean when) {
        return joining(SqlConsts.CROSS_JOIN, table, when);
    }

    public <Q extends QueryWrapper> Joiner<Q> join(QueryTable table) {
        return joining(SqlConsts.JOIN, table, true);
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
        addGroupByColumns(new RawQueryColumn(name));
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

    /**
     * <p>动态排序。
     *
     * <p>排序规则：
     * <ul>
     *     <li>{@code null} 不排序
     *     <li>{@code true} 升序
     *     <li>{@code false} 降序
     * </ul>
     *
     * @param column 列
     * @param asc    是否升序
     * @return {@link QueryWrapper}
     */
    public QueryWrapper orderBy(QueryColumn column, Boolean asc) {
        if (asc != null) {
            if (asc) {
                addOrderBy(column.asc());
            } else {
                addOrderBy(column.desc());
            }
        }
        return this;
    }

    public QueryWrapper orderBy(QueryOrderBy... orderBys) {
        for (QueryOrderBy queryOrderBy : orderBys) {
            addOrderBy(queryOrderBy);
        }
        return this;
    }

    /**
     * <p>动态排序。
     *
     * <p>排序规则：
     * <ul>
     *     <li>{@code null} 不排序
     *     <li>{@code true} 升序
     *     <li>{@code false} 降序
     * </ul>
     *
     * @param column 列
     * @param asc    是否升序
     * @return {@link QueryWrapper}
     */
    public <T> QueryWrapper orderBy(LambdaGetter<T> column, Boolean asc) {
        if (asc != null) {
            if (asc) {
                addOrderBy(LambdaUtil.getQueryColumn(column).asc());
            } else {
                addOrderBy(LambdaUtil.getQueryColumn(column).desc());
            }
        }
        return this;
    }


    public <T> QueryOrderByBuilder<? extends QueryWrapper> orderBy(LambdaGetter<T> getter) {
        return new QueryOrderByBuilder<>(this, getter);
    }

    /**
     * <p>动态排序。
     *
     * <p>排序规则：
     * <ul>
     *     <li>{@code null} 不排序
     *     <li>{@code true} 升序
     *     <li>{@code false} 降序
     * </ul>
     *
     * @param column 列
     * @param asc    是否升序
     * @return {@link QueryWrapper}
     */
    public QueryWrapper orderBy(String column, Boolean asc) {
        if (asc != null) {
            if (asc) {
                addOrderBy(new RawQueryColumn(column).asc());
            } else {
                addOrderBy(new RawQueryColumn(column).desc());
            }
        }
        return this;
    }

    public QueryWrapper orderBy(String... orderBys) {
        if (orderBys == null || orderBys.length == 0) {
            // ignore
            return this;
        }
        for (String queryOrderBy : orderBys) {
            if (StringUtil.isNotBlank(queryOrderBy)) {
                addOrderBy(new RawQueryOrderBy(queryOrderBy));
            }
        }
        return this;
    }

    public QueryWrapper limit(Number rows) {
        if (rows != null) {
            setLimitRows(rows.longValue());
        } else {
            setLimitRows(null);
        }
        return this;
    }

    public QueryWrapper offset(Number offset) {
        if (offset != null) {
            setLimitOffset(offset.longValue());
        } else {
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


    /////////MyBatis-Plus 兼容方法///////////////

    /**
     * 等于 {@code =}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper eq(String column, Object value) {
        and(QueryMethods.column(column).eq(value));
        return this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper eq(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).eq(value));
        return this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper eq(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).eq(value).when(isEffective));
        return this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper eq(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).eq(value).when(isEffective));
        return this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper eq(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).eq(value, isEffective));
        return this;
    }

    /**
     * 等于 {@code =}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper eq(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).eq(value, isEffective));
        return this;
    }


    /**
     * 不等于 {@code !=}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper ne(String column, Object value) {
        and(QueryMethods.column(column).ne(value));
        return this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper ne(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).ne(value));
        return this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper ne(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).ne(value).when(isEffective));
        return this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper ne(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).ne(value).when(isEffective));
        return this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper ne(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).ne(value, isEffective));
        return this;
    }

    /**
     * 不等于 {@code !=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper ne(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).ne(value, isEffective));
        return this;
    }


    /**
     * 大于 {@code >}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper gt(String column, Object value) {
        and(QueryMethods.column(column).gt(value));
        return this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper gt(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).gt(value));
        return this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper gt(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).gt(value).when(isEffective));
        return this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper gt(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).gt(value).when(isEffective));
        return this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper gt(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).gt(value, isEffective));
        return this;
    }

    /**
     * 大于 {@code >}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper gt(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).gt(value, isEffective));
        return this;
    }


    /**
     * 大于等于 {@code >=}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper ge(String column, Object value) {
        and(QueryMethods.column(column).ge(value));
        return this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper ge(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).ge(value));
        return this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper ge(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).ge(value).when(isEffective));
        return this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper ge(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).ge(value).when(isEffective));
        return this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper ge(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).ge(value, isEffective));
        return this;
    }

    /**
     * 大于等于 {@code >=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper ge(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).ge(value, isEffective));
        return this;
    }


    /**
     * 小于 {@code <}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper lt(String column, Object value) {
        and(QueryMethods.column(column).lt(value));
        return this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper lt(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).lt(value));
        return this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper lt(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).lt(value).when(isEffective));
        return this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper lt(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).lt(value).when(isEffective));
        return this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper lt(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).lt(value, isEffective));
        return this;
    }

    /**
     * 小于 {@code <}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper lt(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).lt(value, isEffective));
        return this;
    }


    /**
     * 小于等于 {@code <=}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper le(String column, Object value) {
        and(QueryMethods.column(column).le(value));
        return this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper le(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).le(value));
        return this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper le(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).le(value).when(isEffective));
        return this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper le(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).le(value).when(isEffective));
        return this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper le(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).le(value, isEffective));
        return this;
    }

    /**
     * 小于等于 {@code <=}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper le(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).le(value, isEffective));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    public QueryWrapper in(String column, Object... values) {
        and(QueryMethods.column(column).in(values));
        return this;
    }

    /**
     * {@code IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    public <T> QueryWrapper in(LambdaGetter<T> column, Object... values) {
        and(QueryMethods.column(column).in(values));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    public QueryWrapper in(String column, QueryWrapper queryWrapper) {
        and(QueryMethods.column(column).in(queryWrapper));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    public <T> QueryWrapper in(LambdaGetter<T> column, QueryWrapper queryWrapper) {
        and(QueryMethods.column(column).in(queryWrapper));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    public QueryWrapper in(String column, Collection<?> values) {
        and(QueryMethods.column(column).in(values));
        return this;
    }

    /**
     * {@code IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    public <T> QueryWrapper in(LambdaGetter<T> column, Collection<?> values) {
        and(QueryMethods.column(column).in(values));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    public QueryWrapper in(String column, Object[] values, boolean isEffective) {
        and(QueryMethods.column(column).in(values, isEffective));
        return this;
    }

    /**
     * {@code IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    public <T> QueryWrapper in(LambdaGetter<T> column, Object[] values, boolean isEffective) {
        and(QueryMethods.column(column).in(values, isEffective));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    public QueryWrapper in(String column, Collection<?> values, boolean isEffective) {
        and(QueryMethods.column(column).in(values, isEffective));
        return this;
    }

    /**
     * {@code IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    public <T> QueryWrapper in(LambdaGetter<T> column, Collection<?> values, boolean isEffective) {
        and(QueryMethods.column(column).in(values, isEffective));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    public QueryWrapper in(String column, QueryWrapper queryWrapper, boolean isEffective) {
        and(QueryMethods.column(column).in(queryWrapper, isEffective));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    public <T> QueryWrapper in(LambdaGetter<T> column, QueryWrapper queryWrapper, boolean isEffective) {
        and(QueryMethods.column(column).in(queryWrapper, isEffective));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    public QueryWrapper in(String column, QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).in(queryWrapper, isEffective));
        return this;
    }


    /**
     * {@code IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    public <T> QueryWrapper in(LambdaGetter<T> column, QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).in(queryWrapper, isEffective));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    public QueryWrapper notIn(String column, Object... values) {
        and(QueryMethods.column(column).notIn(values));
        return this;
    }

    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    public <T> QueryWrapper notIn(LambdaGetter<T> column, Object... values) {
        and(QueryMethods.column(column).notIn(values));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    public QueryWrapper notIn(String column, QueryWrapper queryWrapper) {
        and(QueryMethods.column(column).notIn(queryWrapper));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    public <T> QueryWrapper notIn(LambdaGetter<T> column, QueryWrapper queryWrapper) {
        and(QueryMethods.column(column).notIn(queryWrapper));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    public QueryWrapper notIn(String column, Collection<?> values) {
        and(QueryMethods.column(column).notIn(values));
        return this;
    }

    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    public <T> QueryWrapper notIn(LambdaGetter<T> column, Collection<?> values) {
        and(QueryMethods.column(column).notIn(values));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    public QueryWrapper notIn(String column, Object[] values, boolean isEffective) {
        and(QueryMethods.column(column).notIn(values, isEffective));
        return this;
    }

    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    public <T> QueryWrapper notIn(LambdaGetter<T> column, Object[] values, boolean isEffective) {
        and(QueryMethods.column(column).notIn(values, isEffective));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名
     * @param values 条件的值
     */
    public QueryWrapper notIn(String column, Collection<?> values, boolean isEffective) {
        and(QueryMethods.column(column).notIn(values, isEffective));
        return this;
    }

    /**
     * {@code NOT IN(value)}
     *
     * @param column 列名, lambda 展示
     * @param values 值
     */
    public <T> QueryWrapper notIn(LambdaGetter<T> column, Collection<?> values, boolean isEffective) {
        and(QueryMethods.column(column).notIn(values, isEffective));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    public QueryWrapper notIn(String column, QueryWrapper queryWrapper, boolean isEffective) {
        and(QueryMethods.column(column).notIn(queryWrapper, isEffective));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    public <T> QueryWrapper notIn(LambdaGetter<T> column, QueryWrapper queryWrapper, boolean isEffective) {
        and(QueryMethods.column(column).notIn(queryWrapper, isEffective));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名
     * @param queryWrapper 条件的值
     */
    public QueryWrapper notIn(String column, QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).notIn(queryWrapper, isEffective));
        return this;
    }


    /**
     * {@code NOT IN(value)}
     *
     * @param column       列名, lambda 展示
     * @param queryWrapper 值
     */
    public <T> QueryWrapper notIn(LambdaGetter<T> column, QueryWrapper queryWrapper, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).notIn(queryWrapper, isEffective));
        return this;
    }


    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public QueryWrapper between(String column, Object start, Object end) {
        and(QueryMethods.column(column).between(start, end));
        return this;
    }

    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public <T> QueryWrapper between(LambdaGetter<T> column, Object start, Object end) {
        and(QueryMethods.column(column).between(start, end));
        return this;
    }

    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public QueryWrapper between(String column, Object start, Object end, boolean isEffective) {
        and(QueryMethods.column(column).between(start, end, isEffective));
        return this;
    }

    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public <T> QueryWrapper between(LambdaGetter<T> column, Object start, Object end, boolean isEffective) {
        and(QueryMethods.column(column).between(start, end, isEffective));
        return this;
    }


    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public QueryWrapper between(String column, Object start, Object end, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).between(start, end, isEffective));
        return this;
    }

    /**
     * {@code BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public <T> QueryWrapper between(LambdaGetter<T> column, Object start, Object end, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).between(start, end, isEffective));
        return this;
    }


    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public QueryWrapper notBetween(String column, Object start, Object end) {
        and(QueryMethods.column(column).notBetween(start, end));
        return this;
    }

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public <T> QueryWrapper notBetween(LambdaGetter<T> column, Object start, Object end) {
        and(QueryMethods.column(column).notBetween(start, end));
        return this;
    }

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public QueryWrapper notBetween(String column, Object start, Object end, boolean isEffective) {
        and(QueryMethods.column(column).notBetween(start, end, isEffective));
        return this;
    }

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public <T> QueryWrapper notBetween(LambdaGetter<T> column, Object start, Object end, boolean isEffective) {
        and(QueryMethods.column(column).notBetween(start, end, isEffective));
        return this;
    }


    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public QueryWrapper notBetween(String column, Object start, Object end, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).notBetween(start, end, isEffective));
        return this;
    }

    /**
     * {@code NOT BETWEEN start AND end}
     *
     * @param column 列名
     * @param start  开始的值
     * @param end    结束的值
     */
    public <T> QueryWrapper notBetween(LambdaGetter<T> column, Object start, Object end, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).notBetween(start, end, isEffective));
        return this;
    }


    /**
     * {@code LIKE %value%}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper like(String column, Object value) {
        and(QueryMethods.column(column).like(value));
        return this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper like(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).like(value));
        return this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper like(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).like(value).when(isEffective));
        return this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper like(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).like(value).when(isEffective));
        return this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper like(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).like(value, isEffective));
        return this;
    }

    /**
     * {@code LIKE %value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper like(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).like(value, isEffective));
        return this;
    }


    /**
     * {@code LIKE value%}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper likeLeft(String column, Object value) {
        and(QueryMethods.column(column).likeLeft(value));
        return this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper likeLeft(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).likeLeft(value));
        return this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper likeLeft(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).likeLeft(value).when(isEffective));
        return this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper likeLeft(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).likeLeft(value).when(isEffective));
        return this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper likeLeft(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).likeLeft(value, isEffective));
        return this;
    }

    /**
     * {@code LIKE value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper likeLeft(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).likeLeft(value, isEffective));
        return this;
    }


    /**
     * {@code LIKE %value}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper likeRight(String column, Object value) {
        and(QueryMethods.column(column).likeRight(value));
        return this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper likeRight(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).likeRight(value));
        return this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper likeRight(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).likeRight(value).when(isEffective));
        return this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper likeRight(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).likeRight(value).when(isEffective));
        return this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper likeRight(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).likeRight(value, isEffective));
        return this;
    }

    /**
     * {@code LIKE %value}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper likeRight(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).likeRight(value, isEffective));
        return this;
    }


    /**
     * {@code NOT LIKE %value%}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper notLike(String column, Object value) {
        and(QueryMethods.column(column).notLike(value));
        return this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper notLike(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).notLike(value));
        return this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper notLike(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLike(value).when(isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper notLike(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLike(value).when(isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper notLike(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLike(value, isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE %value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper notLike(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLike(value, isEffective));
        return this;
    }


    /**
     * {@code NOT LIKE value%}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper notLikeLeft(String column, Object value) {
        and(QueryMethods.column(column).notLikeLeft(value));
        return this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper notLikeLeft(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).notLikeLeft(value));
        return this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper notLikeLeft(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLikeLeft(value).when(isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper notLikeLeft(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLikeLeft(value).when(isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper notLikeLeft(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLikeLeft(value, isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE value%}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper notLikeLeft(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLikeLeft(value, isEffective));
        return this;
    }


    /**
     * {@code NOT LIKE %value}
     *
     * @param column 列名
     * @param value  条件的值
     */
    public QueryWrapper notLikeRight(String column, Object value) {
        and(QueryMethods.column(column).notLikeRight(value));
        return this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column 列名, lambda 展示
     * @param value  值
     */
    public <T> QueryWrapper notLikeRight(LambdaGetter<T> column, Object value) {
        and(QueryMethods.column(column).notLikeRight(value));
        return this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public QueryWrapper notLikeRight(String column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLikeRight(value).when(isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper notLikeRight(LambdaGetter<T> column, Object value, boolean isEffective) {
        and(QueryMethods.column(column).notLikeRight(value).when(isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column      列名
     * @param value       条件的值
     * @param isEffective 是否有效
     */
    public <V> QueryWrapper notLikeRight(String column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLikeRight(value, isEffective));
        return this;
    }

    /**
     * {@code NOT LIKE %value}
     *
     * @param column      列名, lambda 展示
     * @param value       值
     * @param isEffective 是否有效
     */
    public <T, V> QueryWrapper notLikeRight(LambdaGetter<T> column, V value, Predicate<V> isEffective) {
        and(QueryMethods.column(column).notLikeRight(value, isEffective));
        return this;
    }


    /**
     * {@code IS NULL}
     *
     * @param column 列名
     */
    public QueryWrapper isNull(String column) {
        and(QueryMethods.column(column).isNull());
        return this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column 列名, lambda 展示
     */
    public <T> QueryWrapper isNull(LambdaGetter<T> column) {
        and(QueryMethods.column(column).isNull());
        return this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column      列名
     * @param isEffective 是否有效
     */
    public QueryWrapper isNull(String column, boolean isEffective) {
        and(QueryMethods.column(column).isNull(isEffective));
        return this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column      列名, lambda 展示
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper isNull(LambdaGetter<T> column, boolean isEffective) {
        and(QueryMethods.column(column).isNull(isEffective));
        return this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column      列名
     * @param isEffective 是否有效
     */
    public QueryWrapper isNull(String column, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).isNull(isEffective));
        return this;
    }

    /**
     * {@code IS NULL}
     *
     * @param column      列名, lambda 展示
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper isNull(LambdaGetter<T> column, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).isNull(isEffective));
        return this;
    }


    /**
     * {@code IS NOT NULL}
     *
     * @param column 列名
     */
    public QueryWrapper isNotNull(String column) {
        and(QueryMethods.column(column).isNotNull());
        return this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column 列名, lambda 展示
     */
    public <T> QueryWrapper isNotNull(LambdaGetter<T> column) {
        and(QueryMethods.column(column).isNotNull());
        return this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column      列名
     * @param isEffective 是否有效
     */
    public QueryWrapper isNotNull(String column, boolean isEffective) {
        and(QueryMethods.column(column).isNotNull(isEffective));
        return this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column      列名, lambda 展示
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper isNotNull(LambdaGetter<T> column, boolean isEffective) {
        and(QueryMethods.column(column).isNotNull(isEffective));
        return this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column      列名
     * @param isEffective 是否有效
     */
    public QueryWrapper isNotNull(String column, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).isNotNull(isEffective));
        return this;
    }

    /**
     * {@code IS NOT NULL}
     *
     * @param column      列名, lambda 展示
     * @param isEffective 是否有效
     */
    public <T> QueryWrapper isNotNull(LambdaGetter<T> column, BooleanSupplier isEffective) {
        and(QueryMethods.column(column).isNotNull(isEffective));
        return this;
    }


    ////////内部方法////////

    /**
     * 获取 queryWrapper 的参数
     * 在构建 sql 的时候，需要保证 where 在 having 的前面
     */
    Object[] getAllValueArray() {

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

        // select 子查询的参数：select * from (select ....)
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

        // join 子查询的参数：left join (select ...)
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

        // where 参数
        Object[] whereValues = WrapperUtil.getValues(whereQueryCondition);

        // having 参数
        Object[] havingValues = WrapperUtil.getValues(havingQueryCondition);

        Object[] paramValues = ArrayUtil.concat(whereValues, havingValues);

        // unions 参数
        if (CollectionUtil.isNotEmpty(unions)) {
            for (UnionWrapper union : unions) {
                QueryWrapper queryWrapper = union.getQueryWrapper();
                paramValues = ArrayUtil.concat(paramValues, queryWrapper.getAllValueArray());
            }
        }

        Object[] returnValues = withValues == null ? FlexConsts.EMPTY_ARRAY : withValues.toArray();
        returnValues = columnValues != null ? ArrayUtil.concat(returnValues, columnValues.toArray()) : returnValues;
        returnValues = tableValues != null ? ArrayUtil.concat(returnValues, tableValues.toArray()) : returnValues;
        returnValues = joinValues != null ? ArrayUtil.concat(returnValues, joinValues.toArray()) : returnValues;
        returnValues = ArrayUtil.concat(returnValues, paramValues);

        return returnValues;
    }


    /**
     * 获取 queryWrapper 的参数
     * 在构建 sql 的时候，需要保证 where 在 having 的前面
     */
    Object[] getJoinValueArray() {
        // join 子查询的参数：left join (select ...)
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

        return joinValues == null ? FlexConsts.EMPTY_ARRAY : joinValues.toArray();
    }


    /**
     * 获取 queryWrapper 的参数
     * 在构建 sql 的时候，需要保证 where 在 having 的前面
     */
    Object[] getConditionValueArray() {
        // where 参数
        Object[] whereValues = WrapperUtil.getValues(whereQueryCondition);

        // having 参数
        Object[] havingValues = WrapperUtil.getValues(havingQueryCondition);

        Object[] paramValues = ArrayUtil.concat(whereValues, havingValues);

        // unions 参数
        if (CollectionUtil.isNotEmpty(unions)) {
            for (UnionWrapper union : unions) {
                QueryWrapper queryWrapper = union.getQueryWrapper();
                paramValues = ArrayUtil.concat(paramValues, queryWrapper.getAllValueArray());
            }
        }

        return paramValues;
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
        return SqlUtil.replaceSqlParams(sql, getAllValueArray());
    }

    @Override
    public QueryWrapper clone() {
        return super.clone();
    }

}
