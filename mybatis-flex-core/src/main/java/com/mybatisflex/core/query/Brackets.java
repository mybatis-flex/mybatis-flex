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

import java.util.List;

/**
 * 括号
 */
public class Brackets extends QueryCondition {

    private QueryCondition childCondition;


    public Brackets(QueryCondition childCondition) {
        this.childCondition = childCondition;
    }


    @Override
    public QueryCondition and(QueryCondition nextCondition) {
        connectToChild(nextCondition, SqlConnector.AND);
        return this;
    }

    @Override
    public QueryCondition or(QueryCondition nextCondition) {
        connectToChild(nextCondition, SqlConnector.OR);
        return this;
    }

    protected void connectToChild(QueryCondition nextCondition, SqlConnector connector) {
        childCondition.connect(nextCondition, connector);
    }

    @Override
    public Object getValue() {
        return checkEffective() ? WrapperUtil.getValues(childCondition) : null;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();
        if (checkEffective()) {
            String childSql = childCondition.toSql(queryTables, dialect);
            QueryCondition effectiveBefore = getEffectiveBefore();
            if (effectiveBefore != null) {
                childSql = effectiveBefore.connector + "(" + childSql + ")";
            }
            sql.append(childSql);
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }


    @Override
    public String toString() {
        return "Brackets{" +
                "childCondition=" + childCondition +
                '}';
    }
}
