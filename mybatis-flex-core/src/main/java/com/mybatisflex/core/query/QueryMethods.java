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

public class QueryMethods {

    public static FunctionQueryColumn count() {
        return new FunctionQueryColumn("COUNT", "*");
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

    public static QueryCondition exist(QueryWrapper queryWrapper) {
        return new OperatorSelectCondition(" EXIST ", queryWrapper);
    }

    public static QueryCondition notExist(QueryWrapper queryWrapper) {
        return new OperatorSelectCondition(" NOT EXIST ", queryWrapper);
    }


    public static QueryCondition not(QueryCondition childCondition) {
        return new OperatorQueryCondition(" NOT ", childCondition);
    }

    public static QueryCondition noCondition(){
        return QueryCondition.createEmpty();
    }


    private static QueryWrapper newWrapper() {
        return new QueryWrapper();
    }

    public static QueryWrapper select(QueryColumn... queryColumns) {
        return newWrapper().select(queryColumns);
    }

    public static QueryWrapper selectOne() {
        return select(new StringQueryColumn("1"));
    }

}
