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


import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.table.TableDef;
import com.mybatisflex.core.util.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * 查询列，描述的是一张表的字段
 */
public class QueryColumn implements CloneSupport<QueryColumn> {

    protected QueryTable table;
    protected String name;
    protected String alias;

    private boolean returnCopyByAsMethod = false;


    public QueryColumn() {
    }

    public QueryColumn(String name) {
        SqlUtil.keepColumnSafely(name);
        this.name = StringUtil.tryTrim(name);
    }

    public QueryColumn(String tableName, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = new QueryTable(tableName);
        this.name = StringUtil.tryTrim(name);
    }

    public QueryColumn(String schema, String tableName, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = new QueryTable(schema, tableName);
        this.name = StringUtil.tryTrim(name);
    }

    public QueryColumn(String schema, String tableName, String name, String alias) {
        SqlUtil.keepColumnSafely(name);
        this.returnCopyByAsMethod = true;
        this.table = new QueryTable(schema, tableName);
        this.name = StringUtil.tryTrim(name);
        this.alias = StringUtil.tryTrim(alias);
    }

    public QueryColumn(QueryTable queryTable, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = queryTable;
        this.name = StringUtil.tryTrim(name);
    }

    public QueryColumn(TableDef tableDef, String name) {
        this(tableDef, name, null);
    }

    public QueryColumn(TableDef tableDef, String name, String alias) {
        SqlUtil.keepColumnSafely(name);
        this.returnCopyByAsMethod = true;
        this.table = new QueryTable(tableDef);
        this.name = name;
        this.alias = alias;
    }


    public QueryTable getTable() {
        return table;
    }

    public void setTable(QueryTable table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public <T> QueryColumn as(LambdaGetter<T> fn) {
        return as(fn, false);
    }

    public <T> QueryColumn as(LambdaGetter<T> fn, boolean withPrefix) {
        return as(LambdaUtil.getAliasName(fn, withPrefix));
    }

    public QueryColumn as(String alias) {
        SqlUtil.keepColumnSafely(alias);
        if (returnCopyByAsMethod) {
            QueryColumn newColumn = new QueryColumn();
            newColumn.table = this.table;
            newColumn.name = this.name;
            newColumn.alias = alias;
            return newColumn;
        } else {
            this.alias = alias;
            return this;
        }
    }


    // query methods ///////

    /**
     * equals
     *
     * @param value
     */
    public QueryCondition eq(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.EQUALS, value);
    }


    public <T> QueryCondition eq(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.EQUALS, value).when(fn);
    }


    /**
     * not equals !=
     *
     * @param value
     */
    public QueryCondition ne(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_EQUALS, value);
    }

    public <T> QueryCondition ne(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_EQUALS, value).when(fn);
    }


    /**
     * like %%
     *
     * @param value
     */
    public QueryCondition like(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LIKE, "%" + value + "%");
    }

    public <T> QueryCondition like(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LIKE, "%" + value + "%").when(fn);
    }


    public QueryCondition likeLeft(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LIKE, value + "%");
    }

    public <T> QueryCondition likeLeft(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LIKE, value + "%").when(fn);
    }


    public QueryCondition likeRight(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LIKE, "%" + value);
    }

    public <T> QueryCondition likeRight(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LIKE, "%" + value).when(fn);
    }


    public QueryCondition likeRaw(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LIKE, value);
    }

    public <T> QueryCondition likeRaw(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LIKE, value).when(fn);
    }



    /**
     * not like %%
     *
     * @param value
     */
    public QueryCondition notLike(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_LIKE, "%" + value + "%");
    }

    public <T> QueryCondition notLike(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_LIKE, "%" + value + "%").when(fn);
    }


    public QueryCondition notLikeLeft(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_LIKE, value + "%");
    }

    public <T> QueryCondition notLikeLeft(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_LIKE, value + "%").when(fn);
    }


    public QueryCondition notLikeRight(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_LIKE, "%" + value);
    }

    public <T> QueryCondition notLikeRight(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_LIKE, "%" + value).when(fn);
    }



    /**
     * 大于 greater than
     *
     * @param value
     */
    public QueryCondition gt(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.GT, value);
    }

    public <T> QueryCondition gt(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.GT, value).when(fn);
    }

    /**
     * 大于等于 greater or equal
     *
     * @param value
     */
    public QueryCondition ge(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.GE, value);
    }

    public <T> QueryCondition ge(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.GE, value).when(fn);
    }

    /**
     * 小于 less than
     *
     * @param value
     */
    public QueryCondition lt(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LT, value);
    }

    public <T> QueryCondition lt(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LT, value).when(fn);
    }

    /**
     * 小于等于 less or equal
     *
     * @param value
     */
    public QueryCondition le(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LE, value);
    }

    public <T> QueryCondition le(Object value, Predicate<T> fn) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.LE, value).when(fn);
    }


    /**
     * IS NULL
     *
     * @return
     */
    public QueryCondition isNull() {
        return QueryCondition.create(this, SqlConsts.IS_NULL, null);
    }

    public <T> QueryCondition isNull(Predicate<T> fn) {
        return QueryCondition.create(this, SqlConsts.IS_NULL, null).when(fn);
    }


    /**
     * IS NOT NULL
     *
     * @return
     */
    public QueryCondition isNotNull() {
        return QueryCondition.create(this, SqlConsts.IS_NOT_NULL, null);
    }

    public <T> QueryCondition isNotNull(Predicate<T> fn) {
        return QueryCondition.create(this, SqlConsts.IS_NOT_NULL, null).when(fn);
    }


    /**
     * in arrays
     *
     * @param arrays
     * @return
     */
    public QueryCondition in(Object... arrays) {
        //忽略 QueryWrapper.in("name", null) 的情况
        if (arrays == null || arrays.length == 0 || (arrays.length == 1 && arrays[0] == null)) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.IN, arrays);
    }

    public <T> QueryCondition in(Object[] arrays, Predicate<T> fn) {
        //忽略 QueryWrapper.in("name", null) 的情况
        if (arrays == null || arrays.length == 0 || (arrays.length == 1 && arrays[0] == null)) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.IN, arrays).when(fn);
    }

    /**
     * in child select
     *
     * @param queryWrapper
     * @return
     */
    public QueryCondition in(QueryWrapper queryWrapper) {
        return QueryCondition.create(this, SqlConsts.IN, queryWrapper);
    }

    public <T> QueryCondition in(QueryWrapper queryWrapper, Predicate<T> fn) {
        return QueryCondition.create(this, SqlConsts.IN, queryWrapper).when(fn);
    }


    /**
     * in Collection
     *
     * @param collection
     * @return
     */
    public QueryCondition in(Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            return in(collection.toArray());
        }
        return QueryCondition.createEmpty();
    }

    public <T> QueryCondition in(Collection<?> collection, Predicate<T> fn) {
        if (collection != null && !collection.isEmpty()) {
            return in(collection.toArray(), fn);
        }
        return QueryCondition.createEmpty();
    }

    /**
     * not int arrays
     *
     * @param arrays
     * @return
     */
    public QueryCondition notIn(Object... arrays) {
        //忽略 QueryWrapper.notIn("name", null) 的情况
        if (arrays == null || arrays.length == 0 || (arrays.length == 1 && arrays[0] == null)) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_IN, arrays);
    }

    public <T> QueryCondition notIn(Object[] arrays, Predicate<T> fn) {
        //忽略 QueryWrapper.notIn("name", null) 的情况
        if (arrays == null || arrays.length == 0 || (arrays.length == 1 && arrays[0] == null)) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, SqlConsts.NOT_IN, arrays).when(fn);
    }


    /**
     * not in Collection
     *
     * @param collection
     * @return
     */
    public QueryCondition notIn(Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            return notIn(collection.toArray());
        }
        return QueryCondition.createEmpty();
    }

    public <T> QueryCondition notIn(Collection<?> collection, Predicate<T> fn) {
        if (collection != null && !collection.isEmpty()) {
            return notIn(collection.toArray(), fn);
        }
        return QueryCondition.createEmpty();
    }

    /**
     * not in child select
     *
     * @param queryWrapper
     */
    public QueryCondition notIn(QueryWrapper queryWrapper) {
        return QueryCondition.create(this, SqlConsts.NOT_IN, queryWrapper);
    }

    public <T> QueryCondition notIn(QueryWrapper queryWrapper, Predicate<T> fn) {
        return QueryCondition.create(this, SqlConsts.NOT_IN, queryWrapper).when(fn);
    }


    /**
     * between
     *
     * @param start
     * @param end
     */
    public QueryCondition between(Object start, Object end) {
        return QueryCondition.create(this, SqlConsts.BETWEEN, new Object[]{start, end});
    }

    public <T> QueryCondition between(Object start, Object end, Predicate<T> fn) {
        return QueryCondition.create(this, SqlConsts.BETWEEN, new Object[]{start, end}).when(fn);
    }


    /**
     * not between
     *
     * @param start
     * @param end
     */
    public QueryCondition notBetween(Object start, Object end) {
        return QueryCondition.create(this, SqlConsts.NOT_BETWEEN, new Object[]{start, end});
    }

    public <T> QueryCondition notBetween(Object start, Object end, Predicate<T> fn) {
        return QueryCondition.create(this, SqlConsts.NOT_BETWEEN, new Object[]{start, end}).when(fn);
    }


    ////order by ////
    public QueryOrderBy asc() {
        return new QueryOrderBy(this, SqlConsts.ASC);
    }


    public QueryOrderBy desc() {
        return new QueryOrderBy(this, SqlConsts.DESC);
    }


    // 运算 加减乘除 + - * /
    public QueryColumn add(QueryColumn queryColumn) {
        return new ArithmeticQueryColumn(this).add(queryColumn);
    }

    public QueryColumn add(Number number) {
        return new ArithmeticQueryColumn(this).add(number);
    }

    public QueryColumn subtract(QueryColumn queryColumn) {
        return new ArithmeticQueryColumn(this).subtract(queryColumn);
    }

    public QueryColumn subtract(Number number) {
        return new ArithmeticQueryColumn(this).subtract(number);
    }

    public QueryColumn multiply(QueryColumn queryColumn) {
        return new ArithmeticQueryColumn(this).multiply(queryColumn);
    }

    public QueryColumn multiply(Number number) {
        return new ArithmeticQueryColumn(this).multiply(number);
    }

    public QueryColumn divide(QueryColumn queryColumn) {
        return new ArithmeticQueryColumn(this).divide(queryColumn);
    }

    public QueryColumn divide(Number number) {
        return new ArithmeticQueryColumn(this).divide(number);
    }


    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        QueryTable selectTable = getSelectTable(queryTables, table);
        if (selectTable == null) {
            return dialect.wrap(name);
        } else {
            if (StringUtil.isNotBlank(selectTable.alias)) {
                return dialect.wrap(selectTable.alias) + SqlConsts.REFERENCE + dialect.wrap(name);
            } else if (StringUtil.isNotBlank(selectTable.getSchema()) && StringUtil.isNotBlank(selectTable.getName())) {
                return dialect.wrap(dialect.getRealSchema(selectTable.schema)) + SqlConsts.REFERENCE + dialect.wrap(dialect.getRealTable(selectTable.getName())) + SqlConsts.REFERENCE + dialect.wrap(name);
            } else if (StringUtil.isNotBlank(selectTable.getName())) {
                return dialect.wrap(dialect.getRealTable(selectTable.getName())) + SqlConsts.REFERENCE + dialect.wrap(name);
            } else {
                return dialect.wrap(name);
            }
        }
    }


    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return toConditionSql(queryTables, dialect) + WrapperUtil.buildAlias(alias, dialect);
    }


    QueryTable getSelectTable(List<QueryTable> queryTables, QueryTable selfTable) {
        //未查询任何表
        if (queryTables == null || queryTables.isEmpty()) {
            return null;
        }

        if (selfTable != null && StringUtil.isNotBlank(selfTable.alias)){
            return selfTable;
        }

        if (queryTables.size() == 1 && queryTables.get(0).isSameTable(selfTable)) {
            //ignore table
            return null;
        }

        if (CollectionUtil.isEmpty(queryTables)) {
            return selfTable;
        }

        if (selfTable == null && queryTables.size() == 1) {
            return queryTables.get(0);
        }

        for (QueryTable table : queryTables) {
            if (table.isSameTable(selfTable)) {
                return table;
            }
        }
        return selfTable;
    }


    @Override
    public String toString() {
        return "QueryColumn{" +
            "table=" + table +
            ", name='" + name + '\'' +
            ", alias='" + alias + '\'' +
            '}';
    }


    @Override
    public QueryColumn clone() {
        try {
            QueryColumn clone = (QueryColumn) super.clone();
            // deep clone ...
            clone.table = ObjectUtil.clone(this.table);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }

}
