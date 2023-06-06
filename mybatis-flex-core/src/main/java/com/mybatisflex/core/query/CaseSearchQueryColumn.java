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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CaseSearchQueryColumn extends QueryColumn implements HasParamsColumn {

    private QueryColumn queryColumn;
    private List<When> whens;
    private Object elseValue;

    @Override
    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder("CASE ");
        sql.append(queryColumn.toSelectSql(queryTables, dialect));
        for (When when : whens) {
            sql.append(" WHEN ").append(buildValue(when.searchValue)).append(" THEN ").append(buildValue(when.thenValue));
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
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder("CASE ");
        sql.append(queryColumn.toSelectSql(queryTables, dialect));
        for (When when : whens) {
            sql.append(" WHEN ").append(buildValue(when.searchValue)).append(" THEN ").append(buildValue(when.thenValue));
        }
        if (elseValue != null) {
            sql.append(" ELSE ").append(buildValue(elseValue));
        }
        sql.append(" END");
        return "(" + sql + ")";
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


    void addWhen(When when) {
        if (whens == null) {
            whens = new ArrayList<>();
        }
        whens.add(when);
    }

    @Override
    public QueryColumn as(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public <T> QueryColumn as(LambdaGetter<T> fn) {
        return as(LambdaUtil.getFieldName(fn));
    }


    @Override
    public Object[] getParamValues() {
        Object[] values = FlexConsts.EMPTY_ARRAY;
        if (elseValue instanceof HasParamsColumn) {
            values = ArrayUtil.concat(values, ((HasParamsColumn) elseValue).getParamValues());
        }
        return values;
    }


    public static class When {
        private Builder builder;
        private Object searchValue;
        private Object thenValue;

        public When(Builder builder, Object searchValue) {
            this.builder = builder;
            this.searchValue = searchValue;
        }

        public Builder then(Object thenValue) {
            this.thenValue = thenValue;
            this.builder.caseQueryColumn.addWhen(this);
            return builder;
        }
    }

    public static class Builder {

        private CaseSearchQueryColumn caseQueryColumn = new CaseSearchQueryColumn();

        public Builder(QueryColumn queryColumn) {
            this.caseQueryColumn.queryColumn = queryColumn;
        }

        public When when(Object searchValue) {
            return new When(this, searchValue);
        }

        public Builder else_(Object elseValue) {
            caseQueryColumn.elseValue = elseValue;
            return this;
        }

        public CaseSearchQueryColumn end() {
            return caseQueryColumn;
        }
    }
}
