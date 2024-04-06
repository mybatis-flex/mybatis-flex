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
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.DIVISION_SIGN;
import static com.mybatisflex.core.constant.SqlConsts.MINUS_SIGN;
import static com.mybatisflex.core.constant.SqlConsts.MULTIPLICATION_SIGN;
import static com.mybatisflex.core.constant.SqlConsts.PLUS_SIGN;

public class ArithmeticQueryColumn extends QueryColumn implements HasParamsColumn {

    private List<ArithmeticInfo> arithmeticInfos;

    public ArithmeticQueryColumn(Object value) {
        arithmeticInfos = new ArrayList<>();
        arithmeticInfos.add(new ArithmeticInfo(value));
    }

    @Override
    public QueryColumn add(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(PLUS_SIGN, queryColumn));
        return this;
    }

    @Override
    public QueryColumn add(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(PLUS_SIGN, number));
        return this;
    }

    @Override
    public QueryColumn subtract(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(MINUS_SIGN, queryColumn));
        return this;
    }

    @Override
    public QueryColumn subtract(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(MINUS_SIGN, number));
        return this;
    }

    @Override
    public QueryColumn multiply(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(MULTIPLICATION_SIGN, queryColumn));
        return this;
    }

    @Override
    public QueryColumn multiply(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(MULTIPLICATION_SIGN, number));
        return this;
    }

    @Override
    public QueryColumn divide(QueryColumn queryColumn) {
        arithmeticInfos.add(new ArithmeticInfo(DIVISION_SIGN, queryColumn));
        return this;
    }

    @Override
    public QueryColumn divide(Number number) {
        arithmeticInfos.add(new ArithmeticInfo(DIVISION_SIGN, number));
        return this;
    }


    @Override
    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < arithmeticInfos.size(); i++) {
            sql.append(arithmeticInfos.get(i).toSql(queryTables, dialect, i));
        }
        if (StringUtil.isNotBlank(alias)) {
            return WrapperUtil.withAlias(sql.toString(), alias, dialect);
        }
        return sql.toString();
    }

    @Override
    public ArithmeticQueryColumn clone() {
        ArithmeticQueryColumn clone = (ArithmeticQueryColumn) super.clone();
        // deep clone ...
        clone.arithmeticInfos = CollectionUtil.cloneArrayList(this.arithmeticInfos);
        return clone;
    }


    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < arithmeticInfos.size(); i++) {
            sql.append(arithmeticInfos.get(i).toSql(queryTables, dialect, i));
        }
        return SqlConsts.BRACKET_LEFT + sql + SqlConsts.BRACKET_RIGHT;
    }

    @Override
    public Object[] getParamValues() {
        return arithmeticInfos.stream()
            .map(arithmeticInfo -> arithmeticInfo.value)
            .filter(HasParamsColumn.class::isInstance)
            .map(value -> ((HasParamsColumn) value).getParamValues())
            .flatMap(Arrays::stream)
            .toArray();
    }


    static class ArithmeticInfo implements CloneSupport<ArithmeticInfo> {

        private final String symbol;
        private final Object value;

        public ArithmeticInfo(Object value) {
            this(null, value);
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
            return index == 0 ? valueSql : symbol + valueSql;
        }

        @Override
        public ArithmeticInfo clone() {
            try {
                return (ArithmeticInfo) super.clone();
            } catch (CloneNotSupportedException e) {
                throw FlexExceptions.wrap(e);
            }
        }

    }

}
