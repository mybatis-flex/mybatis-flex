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


import com.mybatisflex.core.constant.SqlConnector;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;

public class QueryCondition implements CloneSupport<QueryCondition> {


    protected QueryColumn column;
    protected String logic;
    protected Object value;
    protected boolean effective = true;

    //当前条件的上一个条件
    protected QueryCondition prev;

    //当前条件的下一个条件
    protected QueryCondition next;

    //两个条件直接的连接符
    protected SqlConnector connector;

    /**
     * 是否为空条件，默认false
     */
    private boolean empty = false;

    protected boolean notEmpty() {
        return !empty;
    }

    protected QueryCondition setEmpty(boolean empty) {
        this.empty = empty;
        return this;
    }

    public static QueryCondition createEmpty() {
        return new QueryCondition().when(false).setEmpty(true);
    }


    public static QueryCondition create(String schema, String table, String column, String logic, Object value) {
        return create(new QueryColumn(schema, table, column), logic, value);
    }

    public static QueryCondition create(QueryColumn queryColumn, Object value) {
        return create(queryColumn, SqlConsts.EQUALS, value);
    }

    public static QueryCondition create(QueryColumn queryColumn, SqlOperator logic, Collection<?> values) {
        return create(queryColumn, logic, values == null ? null : values.toArray());
    }

    public static QueryCondition create(QueryColumn queryColumn, SqlOperator logic, Object value) {
        return create(queryColumn, logic.getValue(), value);
    }

    public static QueryCondition create(QueryColumn queryColumn, String logic, Collection<?> values) {
        return create(queryColumn, logic, values == null ? null : values.toArray());
    }

    public static QueryCondition create(QueryColumn queryColumn, String logic, Object value) {
        QueryCondition condition = new QueryCondition();
        condition.setColumn(queryColumn);
        condition.setLogic(logic);
        condition.setValue(value);
        return condition;
    }

    public QueryColumn getColumn() {
        return column;
    }

    public void setColumn(QueryColumn column) {
        this.column = column;
    }

    public Object getValue() {
        return checkEffective() ? value : null;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    /**
     * 动态条件构造。
     *
     * @param effective 是否启用该条件
     * @return {@link QueryCondition}
     */
    public QueryCondition when(boolean effective) {
        if (notEmpty()) {
            this.effective = effective;
        }
        return this;
    }

    /**
     * 动态条件构造。
     *
     * @param fn 是否启用该条件
     * @return {@link QueryCondition}
     */
    public QueryCondition when(BooleanSupplier fn) {
        if (notEmpty()) {
            this.effective = fn.getAsBoolean();
        }
        return this;
    }

    public boolean checkEffective() {
        return effective;
    }


    public QueryCondition and(String sql) {
        return and(new RawQueryCondition(sql));
    }

    public QueryCondition and(String sql, Object... params) {
        return and(new RawQueryCondition(sql, params));
    }

    public QueryCondition and(QueryCondition nextCondition) {
        return new Brackets(this).and(nextCondition);
    }

    public QueryCondition or(String sql) {
        return or(new RawQueryCondition(sql));
    }

    public QueryCondition or(String sql, Object... params) {
        return or(new RawQueryCondition(sql, params));
    }

    public QueryCondition or(QueryCondition nextCondition) {
        return new Brackets(this).or(nextCondition);
    }

    protected void connect(QueryCondition nextCondition, SqlConnector connector) {

        if (this.next != null) {
            this.next.connect(nextCondition, connector);
        } else {
            nextCondition.connector = connector;
            this.next = nextCondition;
            nextCondition.prev = this;
        }
    }

    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();
        //检测是否生效
        if (checkEffective()) {
            QueryCondition prevEffectiveCondition = getPrevEffectiveCondition();
            if (prevEffectiveCondition != null && this.connector != null) {
                sql.append(this.connector);
            }
            //列
            sql.append(getColumn().toConditionSql(queryTables, dialect));

            //逻辑符号
            sql.append(logic);

            //值（或者问号）
            if (value instanceof QueryColumn) {
                sql.append(((QueryColumn) value).toConditionSql(queryTables, dialect));
            }
            //子查询
            else if (value instanceof QueryWrapper) {
                sql.append(SqlConsts.BRACKET_LEFT)
                    .append(dialect.buildSelectSql((QueryWrapper) value))
                    .append(SqlConsts.BRACKET_RIGHT);
            }
            //原生sql
            else if (value instanceof RawQueryCondition) {
                sql.append(((RawQueryCondition) value).getContent());
            }
            //正常查询，构建问号
            else {
                appendQuestionMark(sql);
            }
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }


    /**
     * 获取上一个 “有效” 的条件
     *
     * @return QueryCondition
     */
    protected QueryCondition getPrevEffectiveCondition() {
        if (prev == null) {
            return null;
        }
        return prev.checkEffective() ? prev : prev.getPrevEffectiveCondition();
    }

    protected QueryCondition getNextEffectiveCondition() {
        if (next == null) {
            return null;
        }
        return next.checkEffective() ? next : next.getNextEffectiveCondition();
    }


    protected void appendQuestionMark(StringBuilder sqlBuilder) {
        //noinspection StatementWithEmptyBody
        if (SqlConsts.IS_NULL.equals(logic)
            || SqlConsts.IS_NOT_NULL.equals(logic)
            || value instanceof QueryColumn
            || value instanceof QueryWrapper
            || value instanceof RawQueryCondition) {
            //do nothing
        }

        //between, not between
        else if (SqlConsts.BETWEEN.equals(logic) || SqlConsts.NOT_BETWEEN.equals(logic)) {
            sqlBuilder.append(SqlConsts.AND_PLACEHOLDER);
        }
        //in, not in
        else if (SqlConsts.IN.equals(logic) || SqlConsts.NOT_IN.equals(logic)) {
            int paramsCount = calculateValueArrayCount();
            sqlBuilder.append(SqlConsts.BRACKET_LEFT);
            for (int i = 0; i < paramsCount; i++) {
                sqlBuilder.append(SqlConsts.PLACEHOLDER);
                if (i != paramsCount - 1) {
                    sqlBuilder.append(SqlConsts.DELIMITER);
                }
            }
            sqlBuilder.append(SqlConsts.BRACKET_RIGHT);
        } else {
            sqlBuilder.append(SqlConsts.PLACEHOLDER);
        }
    }


    private int calculateValueArrayCount() {
        Object[] values = (Object[]) value;
        int paramsCount = 0;
        for (Object object : values) {
            if (object != null && ClassUtil.isArray(object.getClass())) {
                paramsCount += Array.getLength(object);
            } else {
                paramsCount++;
            }
        }
        return paramsCount;
    }


    boolean containsTable(String... tables) {
        if (column == null || !checkEffective()) {
            return nextContainsTable(tables);
        }
        for (String table : tables) {
            String tableName = StringUtil.getTableNameWithAlias(table)[0];
            if (column.table != null && tableName.equals(column.table.name)) {
                return true;
            }
        }
        return nextContainsTable(tables);
    }

    boolean nextContainsTable(String... tables) {
        if (next == null) {
            return false;
        }
        return next.containsTable(tables);
    }

    @Override
    public String toString() {
        return "QueryCondition{" +
            "column=" + column +
            ", logic='" + logic + '\'' +
            ", value=" + value +
            ", effective=" + effective +
            '}';
    }

    @Override
    public QueryCondition clone() {
        try {
            QueryCondition clone = (QueryCondition) super.clone();
            // deep clone ...
            clone.column = ObjectUtil.clone(this.column);
            clone.value = ObjectUtil.cloneObject(this.value);
            clone.prev = clone.next = null;
            if (this.next != null) {
                clone.next = this.next.clone();
                clone.next.prev = clone;
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }

}
