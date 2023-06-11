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
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.*;

import java.util.ArrayList;
import java.util.List;

public class CaseQueryColumn extends QueryColumn implements HasParamsColumn {

    private List<When> whens;
    private Object elseValue;

    void addWhen(When when) {
        if (whens == null) {
            whens = new ArrayList<>();
        }
        whens.add(when);
    }


    @Override
    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder("CASE");
        for (When when : whens) {
            sql.append(" WHEN ").append(when.whenCondition.toSql(queryTables, dialect));
            sql.append(" THEN ").append(buildValue(when.thenValue));
        }
        if (elseValue != null) {
            sql.append(" ELSE ").append(buildValue(elseValue));
        }
        sql.append(" END");
        if (StringUtil.isNotBlank(alias)) {
            return "(" + sql + ") AS " + dialect.wrap(alias);
        }
        return sql.toString();
    }

    @Override
    public CaseQueryColumn clone() {
        CaseQueryColumn clone = (CaseQueryColumn) super.clone();
        // deep clone ...
        clone.whens = CollectionUtil.cloneArrayList(this.whens);
        return clone;
    }


    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder("CASE");
        for (When when : whens) {
            sql.append(" WHEN ").append(when.whenCondition.toSql(queryTables, dialect));
            sql.append(" THEN ").append(buildValue(when.thenValue));
        }
        if (elseValue != null) {
            sql.append(" ELSE ").append(buildValue(elseValue));
        }
        sql.append(" END");
        return "(" + sql + ")";
    }

    @Override
    public QueryColumn as(String alias) {
        this.alias = alias;
        return this;
    }


    private String buildValue(Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        } else if (value instanceof RawFragment) {
            return ((RawFragment) value).getContent();
        } else if (value instanceof QueryColumn) {
            return ((QueryColumn) value).toConditionSql(null, DialectFactory.getDialect());
        } else {
            return "'" + value + "'";
        }
    }

    @Override
    public Object[] getParamValues() {
        Object[] values = FlexConsts.EMPTY_ARRAY;
        for (When when : whens) {
            values = ArrayUtil.concat(values, WrapperUtil.getValues(when.whenCondition));
        }
        if (elseValue instanceof HasParamsColumn) {
            values = ArrayUtil.concat(values, ((HasParamsColumn) elseValue).getParamValues());
        }
        return values;
    }


    public static class When implements CloneSupport<When> {
        private QueryCondition whenCondition;
        private Object thenValue;

        public When(QueryCondition whenCondition) {
            this.whenCondition = whenCondition;
        }

        public void setThenValue(Object thenValue) {
            this.thenValue = thenValue;
        }

        @Override
        public When clone() {
            try {
                When clone = (When) super.clone();
                // deep clone ...
                clone.whenCondition = ObjectUtil.clone(this.whenCondition);
                clone.thenValue = ObjectUtil.cloneObject(this.thenValue);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw FlexExceptions.wrap(e);
            }
        }
    }

    public static class Builder {

        private CaseQueryColumn caseQueryColumn = new CaseQueryColumn();
        private When lastWhen;

        public Then when(QueryCondition condition) {
            lastWhen = new When(condition);
            return new Then(this);
        }

        public Builder else_(Object elseValue) {
            caseQueryColumn.elseValue = elseValue;
            return this;
        }

        public CaseQueryColumn end() {
            return caseQueryColumn;
        }

        public static class Then {

            private Builder builder;

            public Then(Builder builder) {
                this.builder = builder;
            }

            public Builder then(Object thenValue) {
                this.builder.lastWhen.setThenValue(thenValue);
                this.builder.caseQueryColumn.addWhen(builder.lastWhen);
                return builder;
            }
        }
    }
}
