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
import com.mybatisflex.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticQueryColumn extends QueryColumn {

    private List<ArithmeticInfo> arithmeticInfos;

    public ArithmeticQueryColumn(Object value) {
        arithmeticInfos = new ArrayList<>();
        arithmeticInfos.add(new ArithmeticInfo(value));
    }

    @Override
    public QueryColumn add(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(" + ", queryColumn));
        return this;
    }

    @Override
    public QueryColumn add(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(" + ", number));
        return this;
    }

    @Override
    public QueryColumn subtract(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(" - ", queryColumn));
        return this;
    }

    @Override
    public QueryColumn subtract(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(" - ", number));
        return this;
    }

    @Override
    public QueryColumn multiply(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(" * ", queryColumn));
        return this;
    }

    @Override
    public QueryColumn multiply(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(" * ", number));
        return this;
    }

    @Override
    public QueryColumn divide(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(" / ", queryColumn));
        return this;
    }

    @Override
    public QueryColumn divide(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(" / ", number));
        return this;
    }

    @Override
    public QueryColumn as(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < arithmeticInfos.size(); i++) {
            sql.append(arithmeticInfos.get(i).toSql(queryTables, dialect, i));
        }
        if (StringUtil.isNotBlank(alias)) {
            return "(" + sql + ") AS " + dialect.wrap(alias);
        }
        return sql.toString();
    }


    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < arithmeticInfos.size(); i++) {
            sql.append(arithmeticInfos.get(i).toSql(queryTables, dialect, i));
        }
        return "(" + sql + ")";
    }


    static class ArithmeticInfo {
        private String symbol;
        private Object value;

        public ArithmeticInfo(Object value) {
            this.value = value;
        }

        public ArithmeticInfo(String symbol, Object value) {
            this.symbol = symbol;
            this.value = value;
        }

        private String toSql(List<QueryTable> queryTables, IDialect dialect, int index) {
            String valueSql;
            if (value instanceof QueryColumn) {
                valueSql = ((QueryColumn) value).toConditionSql(queryTables, dialect);
            } else {
                valueSql = String.valueOf(value);
            }
            return index == 0 ? valueSql : symbol  + valueSql;
        }
    }
}
