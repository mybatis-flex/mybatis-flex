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

import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

public class QueryMethods {

    public static FunctionQueryColumn count() {
        return new FunctionQueryColumn("COUNT", new StringQueryColumn("*"));
    }

    public static FunctionQueryColumn count(String column) {
        return new FunctionQueryColumn("COUNT", column);
    }

    public static FunctionQueryColumn count(QueryColumn column) {
        return new FunctionQueryColumn("COUNT", column);
    }

    public static FunctionQueryColumn max(String column) {
        return new FunctionQueryColumn("MAX", column);
    }

    public static FunctionQueryColumn max(QueryColumn column) {
        return new FunctionQueryColumn("MAX", column);
    }

    public static FunctionQueryColumn min(String column) {
        return new FunctionQueryColumn("MIN", column);
    }

    public static FunctionQueryColumn min(QueryColumn column) {
        return new FunctionQueryColumn("MIN", column);
    }

    public static FunctionQueryColumn avg(String column) {
        return new FunctionQueryColumn("AVG", column);
    }

    public static FunctionQueryColumn avg(QueryColumn column) {
        return new FunctionQueryColumn("AVG", column);
    }

    public static FunctionQueryColumn sum(String column) {
        return new FunctionQueryColumn("SUM", column);
    }

    public static FunctionQueryColumn sum(QueryColumn column) {
        return new FunctionQueryColumn("SUM", column);
    }

    public static DistinctQueryColumn distinct(QueryColumn... columns) {
        return new DistinctQueryColumn(columns);
    }

    public static CaseQueryColumn.Builder case_() {
        return new CaseQueryColumn.Builder();
    }

    public static CaseSearchQueryColumn.Builder case_(QueryColumn queryColumn) {
        return new CaseSearchQueryColumn.Builder(queryColumn);
    }

    //CONVERT ( data_type [ ( length ) ] , expression [ , style ] )
    public static StringFunctionQueryColumn convert(String... params) {
        return new StringFunctionQueryColumn("CONVERT", params);
    }

    public static StringQueryColumn column(String column) {
        return new StringQueryColumn(column);
    }

    public static QueryColumn column(String table, String column) {
        return new QueryColumn(null, table, column);
    }

    public static QueryColumn column(String schema, String table, String column) {
        return new QueryColumn(schema, table, column);
    }

    public static <T> QueryColumn column(LambdaGetter<T> fn) {
        return LambdaUtil.getQueryColumn(fn);
    }

    public static SelectQueryColumn column(QueryWrapper queryWrapper) {
        return new SelectQueryColumn(queryWrapper);
    }

    public static QueryCondition exists(QueryWrapper queryWrapper) {
        return new OperatorSelectCondition(" EXISTS ", queryWrapper);
    }

    public static QueryCondition notExists(QueryWrapper queryWrapper) {
        return new OperatorSelectCondition(" NOT EXISTS ", queryWrapper);
    }

    public static QueryCondition not(QueryCondition childCondition) {
        return new OperatorQueryCondition(" NOT ", childCondition);
    }

    public static QueryCondition noCondition() {
        return QueryCondition.createEmpty();
    }

    private static QueryWrapper newWrapper() {
        return new QueryWrapper();
    }


    public static QueryWrapper select(QueryColumn... queryColumns) {
        return newWrapper().select(queryColumns);
    }

    public static QueryWrapper selectOne() {
        return select(column("1"));
    }

    public static QueryWrapper selectCount() {
        return select(count());
    }

    public static QueryWrapper selectCountOne() {
        return select(count("1"));
    }

    public static RawFragment raw(String raw) {
        return new RawFragment(raw);
    }

    public static RawFragment raw(String raw, Object... params) {
        return new RawFragment(raw, params);
    }

}
