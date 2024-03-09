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

import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.HashMap;

/**
 * @author michael
 */
public class SqlOperators extends HashMap<String, SqlOperator> {

    private static final SqlOperators EMPTY = new SqlOperators() {
        @Override
        public SqlOperator put(String key, SqlOperator value) {
            throw new IllegalArgumentException("Can not set SqlOperator for \"empty\" SqlOperators");
        }
    };

    public static SqlOperators empty() {
        return EMPTY;
    }

    public static SqlOperators of() {
        return new SqlOperators();
    }

    public static <T> SqlOperators of(LambdaGetter<T> getter, SqlOperator operator) {
        SqlOperators map = new SqlOperators(1);
        map.put(LambdaUtil.getFieldName(getter), operator);
        return map;
    }

    public static <T> SqlOperators of(String fieldName, SqlOperator operator) {
        SqlOperators map = new SqlOperators(1);
        map.put(fieldName, operator);
        return map;
    }

    public SqlOperators() {
    }

    public SqlOperators(int initialCapacity) {
        super(initialCapacity);
    }

    public SqlOperators(SqlOperators sqlOperators) {
        this.putAll(sqlOperators);
    }


    public <T> SqlOperators set(LambdaGetter<T> getter, SqlOperator operator) {
        this.put(LambdaUtil.getFieldName(getter), operator);
        return this;
    }

    public SqlOperators set(String fieldName, SqlOperator operator) {
        this.put(fieldName, operator);
        return this;
    }

    public SqlOperators set(QueryColumn column, SqlOperator operator) {
        this.put(column.getName(), operator);
        return this;
    }

}
