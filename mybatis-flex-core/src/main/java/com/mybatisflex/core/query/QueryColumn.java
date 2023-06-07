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
package com.mybatisflex.core.query;


import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.table.TableDef;
import com.mybatisflex.core.util.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 查询列，描述的是一张表的字段
 */
public class QueryColumn implements Serializable {

    protected QueryTable table;
    protected String name;
    protected String alias;


    public QueryColumn() {
    }

    public QueryColumn(String name) {
        SqlUtil.keepColumnSafely(name);
        this.name = name;
    }

    public QueryColumn(String schema, String tableName, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = new QueryTable(schema, tableName);
        this.name = name;
    }

    public QueryColumn(TableDef tableDef, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = new QueryTable(tableDef);
        this.name = name;
    }

    public QueryColumn(QueryTable queryTable, String name) {
        SqlUtil.keepColumnSafely(name);
        this.table = queryTable;
        this.name = name;
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
        return as(LambdaUtil.getFieldName(fn));
    }

    public QueryColumn as(String alias) {
        SqlUtil.keepColumnSafely(alias);
        QueryColumn newColumn = new QueryColumn();
        newColumn.table = this.table;
        newColumn.name = this.name;
        newColumn.alias = alias;
        return newColumn;
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
        return QueryCondition.create(this, QueryCondition.LOGIC_EQUALS, value);
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
        return QueryCondition.create(this, QueryCondition.LOGIC_NOT_EQUALS, value);
    }


    public QueryCondition like(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, QueryCondition.LOGIC_LIKE, "%" + value + "%");
    }


    public QueryCondition likeLeft(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, QueryCondition.LOGIC_LIKE, "%" + value);
    }


    public QueryCondition likeRight(Object value) {
        if (value == null) {
            return QueryCondition.createEmpty();
        }
        return QueryCondition.create(this, QueryCondition.LOGIC_LIKE, value + "%");
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
        return QueryCondition.create(this, QueryCondition.LOGIC_GT, value);
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
        return QueryCondition.create(this, QueryCondition.LOGIC_GE, value);
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
        return QueryCondition.create(this, QueryCondition.LOGIC_LT, value);
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
        return QueryCondition.create(this, QueryCondition.LOGIC_LE, value);
    }


    /**
     * IS NULL
     *
     * @return
     */
    public QueryCondition isNull() {
        return QueryCondition.create(this, QueryCondition.LOGIC_IS_NULL, null);
    }


    /**
     * IS NOT NULL
     *
     * @return
     */
    public QueryCondition isNotNull() {
        return QueryCondition.create(this, QueryCondition.LOGIC_IS_NOT_NULL, null);
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
        return QueryCondition.create(this, QueryCondition.LOGIC_IN, arrays);
    }

    /**
     * in child select
     *
     * @param queryWrapper
     * @return
     */
    public QueryCondition in(QueryWrapper queryWrapper) {
        return QueryCondition.create(this, QueryCondition.LOGIC_IN, queryWrapper);
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
        return QueryCondition.create(this, QueryCondition.LOGIC_NOT_IN, arrays);
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

    /**
     * not in child select
     *
     * @param queryWrapper
     */
    public QueryCondition notIn(QueryWrapper queryWrapper) {
        return QueryCondition.create(this, QueryCondition.LOGIC_NOT_IN, queryWrapper);
    }


    /**
     * between
     *
     * @param start
     * @param end
     */
    public QueryCondition between(Object start, Object end) {
        return QueryCondition.create(this, QueryCondition.LOGIC_BETWEEN, new Object[]{start, end});
    }

    /**
     * not between
     *
     * @param start
     * @param end
     */
    public QueryCondition notBetween(Object start, Object end) {
        return QueryCondition.create(this, QueryCondition.LOGIC_NOT_BETWEEN, new Object[]{start, end});
    }


    ////order by ////
    public QueryOrderBy asc() {
        return new QueryOrderBy(this);
    }


    public QueryOrderBy desc() {
        return new QueryOrderBy(this, "DESC");
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
                return dialect.wrap(selectTable.alias) + "." + dialect.wrap(name);
            } else if (StringUtil.isNotBlank(selectTable.getSchema()) && StringUtil.isNotBlank(selectTable.getName())) {
                return dialect.wrap(dialect.getRealSchema(selectTable.schema)) + "." + dialect.wrap(dialect.getRealTable(selectTable.getName())) + "." + dialect.wrap(name);
            } else if (StringUtil.isNotBlank(selectTable.getName())) {
                return dialect.wrap(dialect.getRealTable(selectTable.getName())) + "." + dialect.wrap(name);
            } else {
                return dialect.wrap(name);
            }
        }
    }


    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return toConditionSql(queryTables, dialect) + WrapperUtil.buildAsAlias(alias, dialect);
    }


    QueryTable getSelectTable(List<QueryTable> queryTables, QueryTable columnTable) {
        if (queryTables == null || queryTables.isEmpty()) {
            return null;
        }

        if (queryTables.size() == 1 && queryTables.get(0).isSameTable(columnTable)) {
            //ignore table
            return null;
        }

        if (CollectionUtil.isEmpty(queryTables)) {
            return columnTable;
        }

        if (columnTable == null && queryTables.size() == 1) {
            return queryTables.get(0);
        }

        for (QueryTable table : queryTables) {
            if (table.isSameTable(columnTable)) {
                return table;
            }
        }
        return columnTable;
    }


    @Override
    public String toString() {
        return "QueryColumn{" +
                "table=" + table +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }


}
