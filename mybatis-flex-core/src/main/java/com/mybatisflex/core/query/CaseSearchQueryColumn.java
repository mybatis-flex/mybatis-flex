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
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CaseSearchQueryColumn extends QueryColumn implements HasParamsColumn {

    private QueryColumn queryColumn;
    private List<When> whens;
    private Object elseValue;

    @Override
    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        String sql = buildSql(queryTables, dialect);
        if (StringUtil.isNotBlank(alias)) {
            return WrapperUtil.withAlias(sql, alias, dialect);
        }
        return sql;
    }

    private String buildSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder(SqlConsts.CASE);
        sql.append(SqlConsts.BLANK).append(queryColumn.toSelectSql(queryTables, dialect));
        for (When when : whens) {
            sql.append(SqlConsts.WHEN).append(WrapperUtil.buildValue(queryTables, when.searchValue));
            sql.append(SqlConsts.THEN).append(WrapperUtil.buildValue(queryTables, when.thenValue));
        }
        if (elseValue != null) {
            sql.append(SqlConsts.ELSE).append(WrapperUtil.buildValue(queryTables, elseValue));
        }
        sql.append(SqlConsts.END);
        return sql.toString();
    }

    @Override
    public CaseSearchQueryColumn clone() {
        CaseSearchQueryColumn clone = (CaseSearchQueryColumn) super.clone();
        // deep clone ...
        clone.queryColumn = ObjectUtil.clone(this.queryColumn);
        clone.whens = CollectionUtil.cloneArrayList(this.whens);
        clone.elseValue = ObjectUtil.cloneObject(this.elseValue);
        return clone;
    }


    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return WrapperUtil.withBracket(buildSql(queryTables, dialect));
    }

    void addWhen(When when) {
        if (whens == null) {
            whens = new ArrayList<>();
        }
        whens.add(when);
    }


    @Override
    public Object[] getParamValues() {
        Object[] values = FlexConsts.EMPTY_ARRAY;
        if (elseValue instanceof HasParamsColumn) {
            values = ArrayUtil.concat(values, ((HasParamsColumn) elseValue).getParamValues());
        }
        return values;
    }


    public static class When implements CloneSupport<When> {

        private Object searchValue;
        private Object thenValue;

        public When(Object searchValue) {
            this.searchValue = searchValue;
        }

        public void setThenValue(Object thenValue) {
            this.thenValue = thenValue;
        }

        @Override
        public When clone() {
            try {
                When clone = (When) super.clone();
                // deep clone ...
                clone.searchValue = ObjectUtil.cloneObject(this.searchValue);
                clone.thenValue = ObjectUtil.cloneObject(this.thenValue);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw FlexExceptions.wrap(e);
            }
        }

    }


    public static class Builder {

        private CaseSearchQueryColumn caseQueryColumn = new CaseSearchQueryColumn();
        private When lastWhen;

        public Builder(QueryColumn queryColumn) {
            this.caseQueryColumn.queryColumn = queryColumn;
        }

        public Then when(Object searchValue) {
            lastWhen = new When(searchValue);
            return new Then(this);
        }

        public Builder else_(Object elseValue) {
            caseQueryColumn.elseValue = elseValue;
            return this;
        }

        public CaseSearchQueryColumn end() {
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
